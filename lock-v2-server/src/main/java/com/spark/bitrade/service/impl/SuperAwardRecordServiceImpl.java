package com.spark.bitrade.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constant.InCommunityStatus;
import com.spark.bitrade.constant.SuperAwardType;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constants.LockMsgCode;
import com.spark.bitrade.dto.SuperAwardDto;
import com.spark.bitrade.entity.SilkDataDist;
import com.spark.bitrade.entity.SuperAwardRecord;
import com.spark.bitrade.entity.SuperMemberCommunity;
import com.spark.bitrade.entity.SuperPartnerCommunity;
import com.spark.bitrade.mapper.SuperAwardRecordMapper;
import com.spark.bitrade.mapper.SuperPartnerCommunityMapper;
import com.spark.bitrade.service.ISilkDataDistApiService;
import com.spark.bitrade.service.SuperAwardRecordService;
import com.spark.bitrade.service.SuperMemberCommunityService;
import com.spark.bitrade.trans.ServiceChargeEntity;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.DateUtil;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2019-07-16
 */
@Service
@Slf4j
public class SuperAwardRecordServiceImpl extends ServiceImpl<SuperAwardRecordMapper, SuperAwardRecord> implements SuperAwardRecordService {
    private static final String YYYYMMDD = "yyyy-MM-dd";
    private static final String YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";
    private static final String DAYSTART = " 00:00:00";
    private static final String DAYEND = " 23:59:59";
    private static List<Long> ROBOTIDS = Arrays.asList(new Long[]{155414L, 376619L, 376622L, 376623L});

    @Autowired
    private SuperPartnerCommunityMapper superPartnerCommunityMapper;

    @Autowired
    private SuperAwardRecordMapper superAwardRecordMapper;

    @Autowired
    private SuperMemberCommunityService superMemberCommunityService;

    @Autowired
    private ISilkDataDistApiService silkDataDistApiService;

    @Override
    public List<SuperAwardDto> feeAwardByDay(String dateStr) {
        String start = dateStr + DAYSTART;
        String end = dateStr + DAYEND;
        MessageRespResult<SilkDataDist> dis = silkDataDistApiService.findOne("SUPER_PARTNER_CONFIG", "COMMISSION_RATIO");
        AssertUtil.isTrue(dis.isSuccess() && dis.getData() != null, LockMsgCode.SUPER_CONFIG_NOT_FIND);
        BigDecimal rate = new BigDecimal(dis.getData().getDictVal());
        //再查询合伙人+1天是否有返佣记录 如果有奖励记录 则不再进行奖励
        //传入的时间+1天
        Date date = DateUtil.stringToDate(dateStr, YYYYMMDD);
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        //+1天
        calendar.add(Calendar.DATE,1);
        String dateAddOne = DateUtil.dateToString(calendar.getTime(), YYYYMMDD);
        String oneStart=dateAddOne+DAYSTART;
        String oneEnd=dateAddOne+DAYEND;
        QueryWrapper<SuperAwardRecord> sarw = new QueryWrapper<>();
        sarw.select(SuperAwardRecord.MEMBER_ID,SuperAwardRecord.COIN_UNIT)
                .eq(SuperAwardRecord.AWARD_TYPE, SuperAwardType.FEE_AWARD.getOrdinal())
                .lt(SuperAwardRecord.CREATE_TIME, oneEnd)
                .gt(SuperAwardRecord.CREATE_TIME, oneStart).groupBy(SuperAwardRecord.MEMBER_ID,SuperAwardRecord.COIN_UNIT);
        List<SuperAwardRecord> superAwardRecords = superAwardRecordMapper.selectList(sarw);
        //已经奖励过的用户
        List<String> wardPids = superAwardRecords.stream().map(p -> p.getMemberId()+p.getCoinUnit()).collect(Collectors.toList());
        log.info("=============================昨天已计算过的memberIds:{}==================================", wardPids);

        //查询资金记录表 查询出 币币交易的 排除机器人
        List<SuperAwardDto> superAwardDtos =
                superAwardRecordMapper.findSuperAwards(TransactionType.EXCHANGE.getOrdinal(), ROBOTIDS, start, end, rate,wardPids);
        calendar.set(Calendar.HOUR_OF_DAY,11);
        Date time = calendar.getTime();
        superAwardDtos.forEach(ss->ss.setCreateDate(time));
        if (CollectionUtils.isEmpty(superAwardDtos)) {
            return new ArrayList<>();
        }

        return superAwardDtos;
    }


    @Override
    public List<SuperAwardDto> feeAward() {
        MessageRespResult<SilkDataDist> dis = silkDataDistApiService.findOne("SUPER_PARTNER_CONFIG", "COMMISSION_RATIO");
        AssertUtil.isTrue(dis.isSuccess() && dis.getData() != null, LockMsgCode.SUPER_CONFIG_NOT_FIND);
        BigDecimal rate = new BigDecimal(dis.getData().getDictVal());
        log.info("=====================合伙人手续奖励计算开始========================");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        String yester = DateUtil.dateToString(calendar.getTime(), YYYYMMDD);
        //获取昨天时间
        String yesterdayStart = yester + DAYSTART;
        String yesterdayEnd = yester + DAYEND;

        //获取当天的时间
        Calendar ca = Calendar.getInstance();
        String today = DateUtil.dateToString(ca.getTime(), YYYYMMDD);
        //获取昨天时间
        String todayStart = today + DAYSTART;
        String todayEnd = today + DAYEND;

        //再查询合伙人昨天是否有返佣记录 如果有奖励记录 则不再进行奖励
        QueryWrapper<SuperAwardRecord> sarw = new QueryWrapper<>();
        sarw.select(SuperAwardRecord.MEMBER_ID,SuperAwardRecord.COIN_UNIT)
                .eq(SuperAwardRecord.AWARD_TYPE, SuperAwardType.FEE_AWARD.getOrdinal())
                .lt(SuperAwardRecord.CREATE_TIME, todayEnd)
                .gt(SuperAwardRecord.CREATE_TIME, todayStart).groupBy(SuperAwardRecord.MEMBER_ID,SuperAwardRecord.COIN_UNIT);
        List<SuperAwardRecord> superAwardRecords = superAwardRecordMapper.selectList(sarw);
        //已经奖励过的用户
        List<String> wardPids = superAwardRecords.stream().map(p -> p.getMemberId()+p.getCoinUnit()).collect(Collectors.toList());
        log.info("=============================昨天已计算过的memberIds:{}==================================", wardPids);

        //查询资金记录表 查询出 币币交易的 排除机器人
        List<SuperAwardDto> superAwardDtos =
                superAwardRecordMapper.findSuperAwards(TransactionType.EXCHANGE.getOrdinal(), ROBOTIDS, yesterdayStart, yesterdayEnd, rate,wardPids);
        if (CollectionUtils.isEmpty(superAwardDtos)) {
            return new ArrayList<>();
        }
        log.info("=============================奖励明细list:{}==================================", JSON.toJSONString(superAwardDtos));
        return superAwardDtos;
    }


    @Override
    public void excuteFeeAward(List<SuperAwardDto> superAwardDtos) {
        log.info("异步返还手续费开始==========================");
        for (SuperAwardDto dto : superAwardDtos) {
            try {
                getService().excuteAsyncFee(dto);
            } catch (Exception e) {
                log.info("返还记录失败:{}", JSON.toJSONString(dto));
            }
        }
        log.info("异步返还手续费结束==========================");
    }


    @Transactional(rollbackFor = Exception.class)
    public void excuteAsyncFee(SuperAwardDto dto) {
        if (dto.getAwardFee().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        WalletTradeEntity addW = new WalletTradeEntity();
        addW.setType(TransactionType.SUPER_PARTNER_AWARD);
        //社区负责人
        addW.setMemberId(dto.getPartnerId());
        addW.setCoinUnit(dto.getCoinUnit());
        //减少的数量
        addW.setTradeBalance(dto.getAwardFee());
        //冻结 锁仓余额
        addW.setTradeLockBalance(BigDecimal.ZERO);
        addW.setTradeFrozenBalance(BigDecimal.ZERO);
        addW.setComment("超级合伙人每日手续费20%奖励");
        addW.setServiceCharge(new ServiceChargeEntity());

        //先本地执行记录
        SuperAwardRecord record = new SuperAwardRecord();
        record.setCoinUnit(dto.getCoinUnit());
        record.setCostAward(dto.getAwardFee());
        if(dto.getCreateDate()!=null){
            record.setCreateTime(dto.getCreateDate());
            record.setUpdateTime(dto.getCreateDate());
        }else {
            record.setCreateTime(new Date());
            record.setUpdateTime(new Date());
        }
        record.setMemberId(dto.getMemberId());
        record.setPartnerId(dto.getPartnerId());
        record.setAwardType(SuperAwardType.FEE_AWARD);
        record.setTotalAmount(dto.getTotalAmount());
        int insert = superAwardRecordMapper.insert(record);

        addW.setRefId(String.valueOf(record.getId()));
        if (insert > 0) {
            boolean b = superMemberCommunityService.traceWallet(addW);
            log.info("=============================超级合伙人每日手续费20%奖励partnerId:{},结果:{}==================================", dto.getPartnerId(), b);
        }
    }


    /**
     * 查询用户上个月BB交易量
     *
     * @return
     */
    @Override
    public List<SuperAwardDto> memberActiveAward() {
        //获取上个月第一天和最后一天
        Date[] date = getDate(-1);
        long startDay = date[0].getTime();
        long endDay = date[1].getTime();
        //查询已经计算过的用户 可以重复统计
        List<SuperAwardDto> bbExchangeTotal = superAwardRecordMapper.findBBExchangeTotal(startDay, endDay, ROBOTIDS);
        log.info("=============================待计算的记录:{}==================================", JSON.toJSONString(bbExchangeTotal));
        return bbExchangeTotal;
    }

    @Override
    public void excuteBBExchange(List<SuperAwardDto> superAwardDtos) {
        for (SuperAwardDto dto : superAwardDtos) {
            try {
                getService().excuteAsyncBB(dto);
            } catch (Exception e) {
                log.error("返还记录失败:{}", JSON.toJSONString(dto));
            }
        }
    }

    @Override
    @Async
    public void runFeeCaluate() {
        List<SuperAwardDto> superAwardDtos = getService().feeAward();
        if(!CollectionUtils.isEmpty(superAwardDtos)){
            getService().excuteFeeAward(superAwardDtos);
        }
    }

    @Override
    @Async
    public void runBBExchange() {
        List<SuperAwardDto> superAwardDtos = getService().memberActiveAward();
        if(!CollectionUtils.isEmpty(superAwardDtos)){
            getService().excuteBBExchange(superAwardDtos);
        }
    }

    @Override
    @Async
    public void runFeeCaluateByDay(String dateStr) {
        List<SuperAwardDto> superAwardDtos = getService().feeAwardByDay(dateStr);
        if(!CollectionUtils.isEmpty(superAwardDtos)){
            getService().excuteFeeAward(superAwardDtos);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void excuteAsyncBB(SuperAwardDto dto) {

        BigDecimal totalAmount = dto.getTotalAmount();
        Long memberId = dto.getMemberId();
        Long partnerId = dto.getPartnerId();
        log.info("BB交易用户memberId:{},partnerId:{},amount:{}", memberId, partnerId, totalAmount);
        if (totalAmount.compareTo(new BigDecimal("600000")) >= 0 && partnerId != null && memberId != null) {
            //查询该用户是否发过 已发过的不统计
            QueryWrapper<SuperMemberCommunity> smc2 = new QueryWrapper<>();
            smc2.eq(SuperMemberCommunity.MEMBER_ID, memberId)
                    .eq("give_status", BooleanEnum.IS_TRUE.getOrdinal());
            List<SuperMemberCommunity> list = superMemberCommunityService.list(smc2);
            if (!CollectionUtils.isEmpty(list)) {
                return;
            }

            SuperAwardRecord record = new SuperAwardRecord();
            record.setPartnerId(partnerId);
            record.setMemberId(memberId);
            record.setTotalAmount(totalAmount);
            record.setCoinUnit("CNY");
            record.setAwardType(SuperAwardType.ACTIVE_AWARD);
            record.setCreateTime(new Date());
            record.setUpdateTime(new Date());
            superAwardRecordMapper.insert(record);

            QueryWrapper<SuperMemberCommunity> smc = new QueryWrapper<>();
            smc.eq(SuperMemberCommunity.MEMBER_ID, memberId)
                    .eq(SuperMemberCommunity.STATUS, InCommunityStatus.IN_COMMUNITY.getOrdinal());
            SuperMemberCommunity one = superMemberCommunityService.getOne(smc);
            if (one != null) {
                one.setIsActive(BooleanEnum.IS_TRUE);
                superMemberCommunityService.updateById(one);
                log.info("活跃用户memberId:{}", memberId);
            }

        }
    }

    /**
     * 计算日期
     *
     * @param m
     * @return
     */
    private Date[] getDate(int m) {
        Calendar start = Calendar.getInstance();
        start.add(Calendar.MONTH, m);
        start.set(Calendar.DAY_OF_MONTH, 1);
        start.set(start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.DAY_OF_MONTH), 0, 0, 0);

        Calendar end = Calendar.getInstance();
        end.add(Calendar.MONTH, m);
        end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));
        end.set(end.get(Calendar.YEAR), end.get(Calendar.MONTH), end.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        return new Date[]{start.getTime(), end.getTime()};
    }

    public SuperAwardRecordServiceImpl getService() {
        return SpringContextUtil.getBean(SuperAwardRecordServiceImpl.class);
    }




}






















