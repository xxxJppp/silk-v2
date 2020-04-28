//package com.spark.bitrade.service.impl;
//
//import com.baomidou.mybatisplus.core.toolkit.IdWorker;
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.spark.bitrade.entity.ExchangeWallet;
//import com.spark.bitrade.entity.ExchangeWalletSnapshoot;
//import com.spark.bitrade.entity.dto.WalletSyncCountDto;
//import com.spark.bitrade.mapper.ExchangeWalletSnapshootMapper;
//import com.spark.bitrade.service.ExchangeWalletService;
//import com.spark.bitrade.service.ExchangeWalletSnapshootService;
//import com.spark.bitrade.service.ExchangeWalletSyncRecordService;
//import com.spark.bitrade.service.ICoinExchange;
//import com.spark.bitrade.util.DateUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//
///**
// * 机器人钱包每日快照表(ExchangeWalletSnapshoot)表服务实现类
// *
// * @author yangch
// * @since 2019-09-25 19:09:43
// */
//@Slf4j
//@Service("exchangeWalletSnapshootService")
//public class ExchangeWalletSnapshootServiceImpl extends ServiceImpl<ExchangeWalletSnapshootMapper, ExchangeWalletSnapshoot> implements ExchangeWalletSnapshootService {
//    @Autowired
//    private ExchangeWalletService walletService;
//    @Autowired
//    private ExchangeWalletSyncRecordService walletSyncRecordService;
//    @Autowired
//    private ICoinExchange coinExchange;
//
//
//    public ExchangeWalletSnapshoot getNewest(Long memberId, String coinUnit) {
//        // 获取最新的记录
//        return this.baseMapper.getNewest(memberId, coinUnit);
//    }
//
//    @Override
//    public void snapshootAll(Date snapshootTime) {
//        int opTime = getOptime(snapshootTime);
//
//        // 删除当日快照数据
//        int res = this.baseMapper.deleteData(opTime);
//        log.info("snapshoot >>> 删除当日快照数据, 记录数={}", res);
//
//        // 获取所有用户余额
//        List<ExchangeWallet> list = walletService.list();
//        log.info("snapshoot >>> 获取钱包记录，记录数={}", list.size());
//        if (list == null) {
//            return;
//        }
//
//        list.forEach(w -> {
//            snapshoot(opTime, w);
//        });
//    }
//
//    /**
//     * 生成数据快照
//     *
//     * @param opTime 数据周期
//     * @param wallet 钱包记录
//     */
//    public void snapshoot(int opTime, ExchangeWallet wallet) {
//        log.info("snapshoot >>> 开始生成快照数据，opTime={}, uid={}, coin={}", opTime, wallet.getMemberId(), wallet.getCoinUnit());
//
//        // 获取前一周期的数据
//        ExchangeWalletSnapshoot prevWalletsnapshoot = this.getNewest(wallet.getMemberId(), wallet.getCoinUnit());
//        log.info("snapshoot >>> 获取前一周期的数据，prevWalletsnapshoot={}", prevWalletsnapshoot);
//
//        Date startTime = prevWalletsnapshoot == null ? wallet.getCreateTime() : prevWalletsnapshoot.getSnapshootTime();
//        Date endTime = wallet.getUpdateTime();
//
//        // 获取汇率
//        BigDecimal rate = getRate(wallet.getCoinUnit());
//        log.info("snapshoot >>> 获取汇率，rate={}", rate);
//
//        //统计其他数据(备注：时间可能存在边界上的误差，所以调整了时间的范围，调整的范围小于同步的周期就不会有问题)
//        WalletSyncCountDto countDto = null;
//        if (DateUtil.compareDateMinute(endTime, startTime) != 0) {
//            countDto = walletSyncRecordService.sum(wallet.getMemberId(), wallet.getCoinUnit(),
//                    getDate(startTime, -5), getDate(endTime, 5));
//            log.info("snapshoot >>> 统计其它数据，data={}", countDto);
//        }
//
//        // 保存入库
//        ExchangeWalletSnapshoot entity = new ExchangeWalletSnapshoot();
//        entity.setId(IdWorker.getId());
//        entity.setOpTime(opTime);
//        entity.setMemberId(wallet.getMemberId());
//        entity.setCoinUnit(wallet.getCoinUnit());
//        entity.setBalance(wallet.getBalance());
//        entity.setFrozenBalance(wallet.getFrozenBalance());
//        if (prevWalletsnapshoot != null) {
//            entity.setPrevBalance(prevWalletsnapshoot.getBalance());
//            entity.setPrevFrozenBalance(prevWalletsnapshoot.getFrozenBalance());
//        } else {
//            entity.setPrevBalance(BigDecimal.ZERO);
//            entity.setPrevFrozenBalance(BigDecimal.ZERO);
//        }
//        if (countDto != null) {
//            entity.setSumBalance(countDto.getAmount());
//            entity.setSumFrozenBalance(countDto.getFrozen());
//            entity.setCumsumBalance(countDto.getIncrement());
//        } else {
//            entity.setSumBalance(BigDecimal.ZERO);
//            entity.setSumFrozenBalance(BigDecimal.ZERO);
//            entity.setCumsumBalance(BigDecimal.ZERO);
//        }
//        entity.setRate(rate);
//
//        //平衡关系：balance + frozen_balance = prev_balance + prev_frozen_balance + sum_balance + sum_frozen_balance
//        BigDecimal res = entity.getBalance().add(entity.getFrozenBalance())
//                .subtract(entity.getPrevBalance())
//                .subtract(entity.getPrevFrozenBalance())
//                .subtract(entity.getSumBalance())
//                .subtract(entity.getSumFrozenBalance());
//        if (res.compareTo(BigDecimal.ZERO) == 0) {
//            entity.setCheckStatus(1);
//        } else {
//            entity.setCheckStatus(2);
//        }
//        entity.setSnapshootTime(wallet.getUpdateTime());
//
//        // 保存快照数据
//        this.baseMapper.insert(entity);
//
//        log.info("snapshoot >>> 完成生成快照数据，opTime={}, uid={}, coin={}", opTime, wallet.getMemberId(), wallet.getCoinUnit());
//    }
//
//    private int getOptime(Date date) {
////        return Integer.parseInt(new SimpleDateFormat("MMddHHmm").format(date)); //测试
//        return Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(date));
//    }
//
//    private Date getDate(Date date, int addSecond) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(date);
//        calendar.add(Calendar.SECOND, addSecond);
//
//        return calendar.getTime();
//    }
//
//    private BigDecimal getRate(String coinUnit) {
////        return BigDecimal.valueOf(1.234); //测试
//        return coinExchange.getUsdExchangeRate(coinUnit).getData();
//    }
//}