package com.spark.bitrade.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 *
 * @author: Zhong Jiang
 * @date: 2019-12-30 17:35
 */
@Data
public class MiningIndexVo {

    @ApiModelProperty(value = "活动名称")
    private String activityName;

    @ApiModelProperty(value = "剩余挖矿次数")
    private Integer digTimes;

    @ApiModelProperty(value = "活动结束时间")
    private Date endTime;

    @ApiModelProperty(value = "活动规则")
    private String activityRule;

}
