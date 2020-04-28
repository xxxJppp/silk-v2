package com.spark.bitrade.api.client;

import com.spark.bitrade.api.vo.TransferDirectVo;
import com.spark.bitrade.entity.CywWalletWalRecord;

import java.math.BigDecimal;

/**
 * TransferClient
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/10 15:48
 */
public interface TransferClient {

    /**
     * 转账接口
     *
     * @param memberId   会员id
     * @param coinInUnit 币种
     * @param amount     数量
     * @param direct     方向
     * @return record
     */
    CywWalletWalRecord transfer(Long memberId, String coinInUnit, BigDecimal amount, TransferDirectVo direct);
}
