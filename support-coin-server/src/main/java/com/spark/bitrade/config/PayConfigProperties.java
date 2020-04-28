package com.spark.bitrade.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.11.14 16:44  
 */
@Data
@Component
@ConfigurationProperties("support.pay.config")
public class PayConfigProperties {

    /**
     * 创新区增加一个交易对价格
     */
    private Integer innovativeCoin;
    /**
     * 主区增加一个交易对价格
     */
    private Integer mainCoin;

    private Map<Integer, Integer> changeSectionPay;

    private Map<Integer, Integer> streamPay;
}
