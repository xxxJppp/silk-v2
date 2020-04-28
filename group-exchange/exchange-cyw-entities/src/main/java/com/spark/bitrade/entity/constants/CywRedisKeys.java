package com.spark.bitrade.entity.constants;

/**
 * 缓存KEY定义
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/2 15:48
 */
public interface CywRedisKeys {

    /**
     * 机器人钱包常量定义
     */
    String CYW_WALLET_KEY_PREFIX = "data:wallet:";

    /**
     * 机器人钱包同步缓存前缀
     */
    String CYW_WALLET_SYNC_KEY = "data:wal:sync";

    /**
     * 机器人订单常量定义
     */
    String CYW_ORDER_KEY_PREFIX = "data:cywOrder:";

    /**
     * 已完成订单任务队列
     */
    String CYW_COMPLETED_TASK_KEY = "data:cywTask:s";

    /**
     * 已完成订单任务正在处理的队列
     */
    String CYW_COMPLETED_TASKING_KEY = "data:cywIng:s";

    /**
     * 已撤销订单任务队列
     */
    String CYW_CANCELED_TASK_KEY = "data:cywTask:c";

    /**
     * 已撤销订单任务正在处理的队列
     */
    String CYW_CANCELED_TASKING_KEY = "data:cywIng:c";

    /**
     * 正在处理中的成交明细队列
     */
    String CYW_TRADE_TASKING_KEY = "data:cywIng:t:";

    /**
     * 订单校验延迟任务
     */
    String CYW_CHECK_ORDER_DELAY_TASK_KEY = "data:cywCheck:delay:task";

    /**
     * 订单校验任务
     */
    String CYW_CHECK_ORDER_TASK_KEY = "data:cywCheck:task";

    /**
     * 订单校验正在进行的任务
     */
    String CYW_CHECK_ORDER_ING_TASK_KEY = "data:cywCheck:ing:task";

    /**
     * 获取钱包缓存Key
     *
     * @param memberId 会员id
     * @param coinUnit 币种
     * @return key
     */
    static String getCywWalletKey(Long memberId, String coinUnit) {
        return CYW_WALLET_KEY_PREFIX + coinUnit + ":" + memberId;
    }

    /**
     * 交易订单缓存key
     *
     * @param memberId 会员id
     * @param symbol   交易对，eg：BTC/USDT
     * @return key=data:cywOrder：t:交易对:用户ID
     */
    static String getCywOrderTradingKey(Long memberId, String symbol) {
        //key=data:cywOrder:t:<交易对>:<用户ID>
        return new StringBuilder(CYW_ORDER_KEY_PREFIX)
                .append("t:")
                .append(symbol.replace("/", ""))
                .append(":")
                .append(memberId)
                .toString();
    }

    /**
     * 撤销订单缓存key
     *
     * @param symbol 交易对，eg：BTC/USDT
     * @return key=data:cywOrder:c:交易对
     */
    static String getCywOrderCanceledKey(String symbol) {
        //key=data:cywOrder:c:<交易对>:<用户ID>
        return new StringBuilder(CYW_ORDER_KEY_PREFIX)
                .append("c:")
                .append(symbol.replace("/", ""))
                .toString();
    }

    /**
     * 撤销订单缓存keys
     *
     * @return key=data:cywOrder:c:*
     */
    static String getCywOrderCanceledKeys() {
        //key=data:cywOrder:c:*
        return new StringBuilder(CYW_ORDER_KEY_PREFIX)
                .append("c:*")
                .toString();
    }

    /**
     * 已完成订单缓存key
     *
     * @param symbol 交易对，eg：BTC/USDT
     * @return key=data:cywOrder:s:交易对
     */
    static String getCywOrderCompletedKey(String symbol) {
        //key=data:cywOrder:s:<交易对>:<用户ID>
        return new StringBuilder(CYW_ORDER_KEY_PREFIX)
                .append("s:")
                .append(symbol.replace("/", ""))
                .toString();
    }

    /**
     * 已完成订单缓存keys
     *
     * @return key=data:cywOrder:s:*
     */
    static String getCywOrderCompletedKeys() {
        //key=data:cywOrder:s:*
        return new StringBuilder(CYW_ORDER_KEY_PREFIX)
                .append("s:*")
                .toString();
    }

    /**
     * 获取交易订单缓存keys
     *
     * @param symbol 交易对，eg：BTC/USDT
     * @return key=data:cywOrder:交易对:用户ID
     */
    static String getCywOrderTradingKeys(String symbol) {
        //key=data:cywOrder:<交易对>:<用户ID>
        return new StringBuilder(CYW_ORDER_KEY_PREFIX)
                .append("t:")
                .append(symbol.replace("/", ""))
                .append(":*")
                .toString();
    }

    /**
     * 从订单id中获取交易对
     *
     * @param orderId 订单号，格式=S雪花流水ID_交易对
     * @return 交易对
     */
    static String parseSymbolFromOrderId(String orderId) {
        return orderId.substring(orderId.lastIndexOf("_") + 1);
    }

}
