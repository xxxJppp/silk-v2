package com.spark.bitrade.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 布朗计划，直推部门VO
 *
 * @author zhongxj
 * @date 2019.7.12
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@ApiModel(description = "布朗计划，直推部门VO")
public class PromotionMemberVO {
    /**
     * 有效直推人数
     */
    @ApiModelProperty(value = "有效直推人数", example = "0")
    private Integer effectiveTotal;
    /**
     * 直推部门明细
     */
    @ApiModelProperty(value = "直推部门明细", example = "")
    private List<PromotionMemberDetailsVO> list;
}