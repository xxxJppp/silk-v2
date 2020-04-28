package com.spark.bitrade.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author: Zhong Jiang
 * @date: 2019-11-28 14:26
 */
@Data
public class MemberBenefitsExtendsVo {

    private Long id;

    private Long memberId;

    private Integer levelId;

    /**
     * 生效时间
     */
    private Date startTime;

    /**
     * 失效时间
     */
    private Date endTime;

    @ApiModelProperty(value = "购买类型 {10:购买,20：锁仓, 30: 社区人数}")
    private Integer operationType;

    @ApiModelProperty(value = "社区人数")
    private Integer communitySize = 0;

    @ApiModelProperty(value = "社区名称")
    private String communityName = "";


    private BigDecimal openVipAmount = BigDecimal.ZERO;

    private Integer openVipDays;

}
