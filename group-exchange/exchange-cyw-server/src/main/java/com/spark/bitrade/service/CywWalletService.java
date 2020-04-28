package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.CywWallet;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * 机器人钱包(CywWallet)表服务接口
 *
 * @author archx
 * @since 2019-09-02 14:42:41
 */
public interface CywWalletService extends IService<CywWallet> {

    /**
     * 查询钱包
     *
     * @param memberId 会员ID
     * @param coinUnit 币种
     * @return optional
     */
    Optional<CywWallet> findOne(Long memberId, String coinUnit);

    /**
     * 创建钱包
     *
     * @param memberId 会员ID
     * @param coinUnit 币种
     * @return bool
     */
    boolean create(Long memberId, String coinUnit);

    /**
     * 同步数据
     *
     * @param memberId      会员ID
     * @param coinUnit      币种
     * @param balance       余额
     * @param frozenBalance 冻结余额
     * @return bool
     */
    boolean sync(Long memberId, String coinUnit, BigDecimal balance, BigDecimal frozenBalance);
}