package com.spark.bitrade.entity.constants;

/**
 * 缓存KEY定义
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/2 15:48
 */
public interface ExchangeRedisKeys {

    /**
     * 币币钱包同步缓存前缀
     */
    String EX_WALLET_SYNC_KEY = "exchange:wal:sync";

    /**
     * 正在处理中的成交明细队列
     */
    String EX_TRADE_TASKING_KEY = "exchange:Ing:t:";

    /**
     * 订单校验延迟任务
     */
    String EX_CHECK_ORDER_DELAY_TASK_KEY = "exchange:check:delay:task";

    /**
     * 订单校验任务
     */
    String EX_CHECK_ORDER_TASK_KEY = "exchange:check:task";

    /**
     * 订单校验正在进行的任务
     */
    String EX_CHECK_ORDER_ING_TASK_KEY = "exchange:check:ing:task";

    /**
     * 交易中的订单
     */
    String EX_ORDER_TRADING_PREFIX = "exchange:trading:";

    /**
     * 交易中订单的key
     *
     * @param orderId
     * @return
     */
    static String getOrderTradingKey(String orderId) {
        return new StringBuilder(EX_ORDER_TRADING_PREFIX)
                .append(orderId).toString();
    }


    /**
     * 交易订单缓存key
     *
     * @param memberId 会员id
     * @param symbol   交易对，eg：BTC/USDT
     * @return key=data:cywOrder：t:交易对:用户ID
     */
    @Deprecated
    static String getExchangeOrderTradingKey(Long memberId, String symbol) {
        //key=data:cywOrder:t:<交易对>:<用户ID>
//        return new StringBuilder(CYW_ORDER_KEY_PREFIX)
//                .append("t:")
//                .append(symbol.replace("/", ""))
//                .append(":")
//                .append(memberId)
//                .toString();
        throw new UnsupportedOperationException("该方法已废弃");
    }

    /**
     * 从订单id中获取交易对
     *
     * @param orderId 订单号，格式=S雪花流水ID_交易对
     * @return 交易对
     */
    @Deprecated
    static String parseSymbolFromOrderId(String orderId) {
        // return orderId.substring(orderId.lastIndexOf("_") + 1);
        throw new UnsupportedOperationException("该方法已废弃");
    }

}
