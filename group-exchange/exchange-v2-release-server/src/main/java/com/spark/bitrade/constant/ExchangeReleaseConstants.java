package com.spark.bitrade.constant;

import com.spark.bitrade.entity.constants.ExchangeConstants;

/**
 *  
 *
 * @author young
 * @time 2019.12.16 18:19
 */
public interface ExchangeReleaseConstants extends ExchangeConstants {

    /**
     * 订单前缀
     */
    String ORDER_PREFIX = "R";

    /**
     * 奖励任务
     */
    String TOPIC_AWARD_TASK = "msg-release-award-task";
}
