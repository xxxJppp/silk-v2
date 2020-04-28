package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * (TestShardingSphere)表实体类
 *
 * @author young
 * @since 2019-06-09 17:18:08
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "")
public class TestShardingSphere {

    @TableId
    @ApiModelProperty(value = "", example = "")
    private Integer sid;

    @ApiModelProperty(value = "", example = "")
    private String col2;


}