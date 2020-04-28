package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.MemberWallet;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.vo.MemberWalletCountVo;
import com.spark.bitrade.vo.MemberWalletVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 会员钱包表服务接口
 *
 * @author yangch
 * @since 2019-06-15 16:14:18
 */
public interface MemberWalletService extends IService<MemberWallet> {

    /**
     * 根据币种和用户ID获取钱包
     *
     * @param coinId   币种ID
     * @param memberId 用户ID
     * @return
     */
    MemberWallet findByCoinAndMemberId(String coinId, Long memberId);

    /**
     * 根据币种和用户ID获取钱包
     *
     * @param unit   币种
     * @param memberId 用户ID
     * @return
     */
    MemberWallet findByUnitAndMemberId(String unit, Long memberId);

    /**
     * 加减钱包余额
     *
     * @param walletId           钱包ID
     * @param tradeBalance       交易余额，整数为加/负数为减
     * @param tradeFrozenBalance 交易冻结余额，整数为加/负数为减
     * @param tradeLockBalance   交易锁仓余额，整数为加/负数为减
     * @return
     */
    Boolean trade(Long walletId, BigDecimal tradeBalance, BigDecimal tradeFrozenBalance, BigDecimal tradeLockBalance) throws MessageCodeException;

    /**
     *  创建钱包账户
     *
     * @param memberId 会员ID
     * @param coinId   币种
     *                  
     */
    MemberWallet createMemberWallet(long memberId, String coinId);

    /**
     *  会员余额接口
     *  huyu
     * @param memberId 会员ID
     *                  
     */
    List<MemberWalletVo> findAllBalanceByMemberId(Long memberId );

    /**
     * 项目方持仓统计
     *
     * @param coinId
     * @param balanceStart  持仓币数下限
     * @param balanceEnd    持仓币数上限
     * @param page
     * @param pageSize
     * @return
     */
    MemberWalletCountVo countMemberWallet(String coinId, BigDecimal balanceStart, BigDecimal balanceEnd,
                                          Integer page, Integer pageSize);



}