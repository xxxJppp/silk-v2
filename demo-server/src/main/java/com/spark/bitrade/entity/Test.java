package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * (Test)表实体类
 *
 * @author young
 * @since 2019-06-09 15:56:33
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "test")
public class Test {

    @TableId
    @ApiModelProperty(value = "col1", example = "col1")
    private Integer col1;

    @ApiModelProperty(value = "col2", example = "col2")
    private String col2;


}