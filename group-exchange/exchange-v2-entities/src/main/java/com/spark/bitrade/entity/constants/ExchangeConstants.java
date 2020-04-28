package com.spark.bitrade.entity.constants;

/**
 * 常量定义
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/11 10:10
 */
public interface ExchangeConstants {

    /**
     * 币币交易钱包同步通知 Kafka主题
     */
    String KAFKA_MSG_EX_WALLET_WAL_SYNC_TASK = "msg_ex_wallet_wal_sync_task";

    /**
     * 币币钱包同步成功通知
     */
    String KAFKA_MSG_EX_WALLET_SYNC_SUCCEED = "push_ex_wallet_sync_succeed";

    /**
     * 订单前缀
     */
    String ORDER_PREFIX = "E";
}
