package com.spark.bitrade.vo;

import com.alibaba.fastjson.JSONObject;
import com.spark.bitrade.constant.SectionTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.11.11 10:54  
 */
@Data
public class UpCoinConfigVo {
    @ApiModelProperty("上币版块")
    private List<JSONObject> sectionTypes;
    @ApiModelProperty("当前版块")
    private SectionTypeEnum currentSection;
    @ApiModelProperty(value = "支付配置")
    private List<Map<String,String>> payConfigs;
    @ApiModelProperty(value = "交易码")
    private String tradeCode;
    @ApiModelProperty("引流开关状态 0关 1开")
    private Integer switchStatus;
    @ApiModelProperty("币种")
    private String coin;
    @ApiModelProperty("有效用户数")
    private Integer validPersonCount;
}
