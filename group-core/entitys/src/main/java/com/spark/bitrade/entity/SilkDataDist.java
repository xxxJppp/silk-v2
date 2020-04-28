package com.spark.bitrade.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 系统配置(SilkDataDist)表实体类
 *
 * @author daring5920
 * @since 2019-05-08 20:29:01
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "系统配置")
public class SilkDataDist {

    /**
     * 配置编号
     */
    @TableId
    @ApiModelProperty(value = "配置编号", example = "")
    private String dictId;
    /**
     * 配置KEY
     */
    @TableId
    @ApiModelProperty(value = "配置KEY", example = "")
    private String dictKey;

    /**
     * 值
     */
    @ApiModelProperty(value = "值", example = "")
    private String dictVal;

    /**
     * 数据类型
     */
    @ApiModelProperty(value = "数据类型", example = "")
    private String dictType;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述", example = "")
    private String remark;

    /**
     * 状态{0:失效,1:生效}
     */
    @ApiModelProperty(value = "状态{0:失效,1:生效}", example = "")
    private Integer status;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序", example = "")
    private Integer sort;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "")
    @TableField(value="create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间", example = "")
    @TableField(value="update_time", fill = FieldFill.INSERT_UPDATE, update="NOW()")
    private Date updateTime;


}