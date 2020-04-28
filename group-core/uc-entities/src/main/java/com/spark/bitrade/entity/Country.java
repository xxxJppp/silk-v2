package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * (Country)表实体类
 *
 * @author wsy
 * @since 2019-06-14 14:39:13
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "")
public class Country {

    @TableId
    @ApiModelProperty(value = "中文名称", example = "")
    private String zhName;

    @ApiModelProperty(value = "区号", example = "")
    private String areaCode;

    @ApiModelProperty(value = "英文名称", example = "")
    private String enName;

    @ApiModelProperty(value = "语言", example = "")
    private String language;

    @ApiModelProperty(value = "当地货币缩写", example = "")
    private String localCurrency;

    @ApiModelProperty(value = "", example = "")
    private Integer sort;


}