package com.spark.bitrade.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * SLP加速释放页面，直推部门从SLP取值的VO
 *
 * @author zhongxj
 * @since 2019-07-10 09:27:03
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@ApiModel(description = "SLP加速释放页面，直推部门从SLP取值的VO")
public class PromotionMemberExtensionVo {

    /**
     * 社区节点名称
     */
    @ApiModelProperty(value = "社区节点名称", example = "土星")
    private String levelName;

    /**
     * 有效性（0-无效；1-有效）
     */
    @ApiModelProperty(value = "有效性（0-无效；1-有效）", example = "0")
    private Integer status;
}
