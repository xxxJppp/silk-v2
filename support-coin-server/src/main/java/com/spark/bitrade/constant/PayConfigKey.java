package com.spark.bitrade.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.11.06 15:21  
 */
@AllArgsConstructor
@Getter
public enum PayConfigKey {


    /**
     * 转版管理支付配置KEY
     */
    PAY_CONFIG_CHANGE_SECTION("PAY_CONFIG_CHANGE_SECTION", "转版管理支付配置KEY"),
    /**
     * 引流开关管理支付配置KEY
     */
    PAY_CONFIG_STREAM_SWITCH("PAY_CONFIG_STREAM_SWITCH", "引流开关管理支付配置KEY"),
    /**
     * 新增交易对支付配置KEY
     */
    PAY_CONFIG_COIN_MATCH("PAY_CONFIG_COIN_MATCH", "新增交易对支付配置KEY")
    ;

    private String key;

    private String desc;


}
