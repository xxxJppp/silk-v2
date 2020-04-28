package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.entity.GpsLocation;
import com.spark.bitrade.entity.SilkPayOrder;
import com.spark.bitrade.entity.dto.SilkPayAccountDto;
import com.spark.bitrade.mapper.SilkPayAccountMapper;
import com.spark.bitrade.entity.SilkPayAccount;
import com.spark.bitrade.service.SilkPayAccountService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 支付账号(SilkPayAccount)表服务实现类
 *
 * @author wsy
 * @since 2019-07-18 10:38:05
 */
@Service("silkPayAccountService")
public class SilkPayAccountServiceImpl extends ServiceImpl<SilkPayAccountMapper, SilkPayAccount> implements SilkPayAccountService {

    /**
     * 进行订单匹配
     *
     * @param gpsLocation
     * @param amount
     * @param payType
     * @return true
     * @author shenzucai
     * @time 2019.07.27 9:40
     */
    @Override
    public List<SilkPayAccountDto> findMostSuitableAccount( GpsLocation gpsLocation, BigDecimal amount, Integer payType) {
        // 查找到符合条件的账号
        List<SilkPayAccountDto> findMostAccount =  baseMapper.findMostSuitableAccount(gpsLocation,amount,payType);
        return findMostAccount;
    }

    /**
     * 减少账户限额
     *
     * @param accountId
     * @param amount
     * @return true
     * @author shenzucai
     * @time 2019.08.05 17:20
     */
    @Override
    public Boolean reduceAccountLimit(Long accountId, BigDecimal amount) {
        UpdateWrapper<SilkPayAccount> silkPayAccountUpdateWrapper = new UpdateWrapper<SilkPayAccount>()
                .setSql("quota_surplus = quota_surplus - "+amount).eq("id",accountId).ge("quota_surplus",amount);
        return baseMapper.update(null,silkPayAccountUpdateWrapper) > 0;
    }

    /**
     * 还原用户限额
     *
     * @param accountId
     * @param silkPayOrder
     * @return true
     * @author shenzucai
     * @time 2019.08.05 17:20
     */
    @Override
    public Boolean cancelAccountLimit(Long accountId, SilkPayOrder silkPayOrder) {
        UpdateWrapper<SilkPayAccount> silkPayAccountUpdateWrapper = new UpdateWrapper<SilkPayAccount>()
                .setSql("quota_surplus = quota_surplus + "+silkPayOrder.getMoney())
                .eq("id",accountId)
                .ge("quota_surplus",0)
                .ge("quota_relieve",new Date());
        return baseMapper.update(null,silkPayAccountUpdateWrapper) > 0;
    }
}