package com.spark.bitrade.constant;

public class BtBankSystemConfig {

    public static final String BT_BANK_MINER_CONFIG = "BT_BANK_MINER_CONFIG";


    public static final String REDIS_MINER_ORDER_PREFIX = "entity:btbank:minner:order:";

    public static final String REDIS_DICT_PREFIX = "entity:btbank:dict:";

    /**
     * 秒杀抢单佣金比例
     */
    public static final String SECKILL_COMMISSION_RATE = "SECKILL_COMMISSION_RATE";

    /**
     * 派单佣金比例
     */
    public static final String DISPATCH_COMMISSION_RATE = "DISPATCH_COMMISSION_RATE";

    /**
     * 固定佣金比例
     */
    public static final String FIXED_COMMISSION_RATE = "FIXED_COMMISSION_RATE";
    /**
     * 矿池最低划转金额
     */
    public static final String MINIMUM_TRANSFER_AMOUNT = "MINIMUM_TRANSFER_AMOUNT";

    /**
     * 接单开关
     */
    public static final String RECEIVING_ORDER_SWITCH = "RECEIVING_ORDER_SWITCH";

    /**
     * 转入开关
     */
    public static final String TRANSFER_SWITCH = "TRANSFER_SWITCH";

    /**
     * 抢单开关
     */
    public static final String SECKILL_SWITCH = "SECKILL_SWITCH";

    /**
     * 派单开关
     */
    public static final String DISPATCH_SWITCH = "DISPATCH_SWITCH";

    /**
     * 奖励财务账户
     */
    public static final String BTBANK_REWARD_SOURCE = "BTBANK_REWARD_SOURCE";

    /**
     * 派单时间，分钟
     */
    public static final String DISPATCH_TIME = "DISPATCH_TIME";

    /**
     * 抢单解锁时间，分钟
     */
    public static final String UNLOCK_TIME = "UNLOCK_TIME";

    /**
     * App自动刷新矿池抢单列表间隔，秒
     */
    public static final String AUTO_REFRESH_RATE = "AUTO_REFRESH_RATE";
}
