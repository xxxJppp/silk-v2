package com.spark.bitrade.service;

import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.trans.WalletExchangeEntity;

/**
 *  个人币种兑换接口
 *  备注：个人账户下，将一种币种兑换为另外一种币种（如：币币交易、闪兑）
 *
 * @author young
 * @time 2019.06.24 18:39
 */
public interface IWalletExchangeService {
    /**
     * 币种兑换（如：币币交易）
     *
     * @param exchangeEntity 币种交换实体
     * @return 交换成功，返回前后前的钱包余额等数据
     * @throws MessageCodeException
     */
    boolean exchange(WalletExchangeEntity exchangeEntity) throws MessageCodeException;
}
