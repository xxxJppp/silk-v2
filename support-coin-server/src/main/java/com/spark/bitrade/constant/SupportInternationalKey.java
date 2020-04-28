package com.spark.bitrade.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *  
 * 国际化KEY枚举    
 *  @author liaoqinghui  
 *  @time 2019.11.05 11:20  
 */
@AllArgsConstructor
@Getter
public enum SupportInternationalKey {

    /**
     * 上币简介KEY唯一标识
     */
    UP_COIN_INTRO("UP_COIN_INTRO","上币简介KEY"),

    UP_COIN_UPDATE_INTRO("UPDATE_UP_COIN_INTRO","币种基本信息修改申请KEY"),

    ZH_CH("_CN","中文后缀"),

    ZH_HK("_HK","中文后缀"),

    KO_KR("_KO","中文后缀"),

    US_EN("_EN","中文后缀"),

    PROJECT_INTRO_KEY("PROJECT_INTRO_KEY","项目简介key"),
    ;

    private String key;

    private String desc;

}
