package com.spark.bitrade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.api.vo.ExchangeWalletVo;
import com.spark.bitrade.api.vo.WalletQueryVo;
import com.spark.bitrade.entity.ExchangeWallet;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * 机器人钱包(ExchangeWallet)表服务接口
 *
 * @author archx
 * @since 2019-09-02 14:42:41
 */
public interface ExchangeWalletService extends IService<ExchangeWallet> {

    /**
     * 查询钱包
     *
     * @param memberId 会员ID
     * @param coinUnit 币种
     * @return optional
     */
    Optional<ExchangeWallet> findOne(Long memberId, String coinUnit);

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

    /**
     * 冻结余额
     *
     * @param memberId 会员ID
     * @param coinUnit 币种
     * @param amount   冻结余额
     * @param frozen   冻结余额
     * @return bool
     */
    boolean freeze(Long memberId, String coinUnit, BigDecimal amount, BigDecimal frozen);

    /**
     * 重置钱包数据
     *
     * @param memberId      会员ID
     * @param coinUnit      币种
     * @param balance       余额
     * @param frozenBalance 冻结余额
     * @return bool
     */
    boolean reset(Long memberId, String coinUnit, BigDecimal balance, BigDecimal frozenBalance);

    /**
     * 根据条件查询币币账户列表
     *
     * @param vo
     * @return
     */
    IPage<ExchangeWalletVo> findList(WalletQueryVo vo);
}