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
 * 应用管理(SilkPayApp)表实体类
 *
 * @author wsy
 * @since 2019-07-19 16:27:42
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "应用管理")
public class SilkPayApp {

    /**
     * 编号
     */
    @TableId
    @ApiModelProperty(value = "编号", example = "")
    private Long id;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称", example = "")
    private String appLabel;

    /**
     * 包名
     */
    @ApiModelProperty(value = "包名", example = "")
    private String packageName;

    /**
     * 内部版本
     */
    @ApiModelProperty(value = "内部版本", example = "")
    private Integer versionCode;

    /**
     * 外部版本
     */
    @ApiModelProperty(value = "外部版本", example = "")
    private String versionName;

    /**
     * 状态 0-隐藏  1-显示
     */
    @ApiModelProperty(value = "状态 0-隐藏  1-显示", example = "")
    private Integer state;

    /**
     * 应用大小：单位(KB)
     */
    @ApiModelProperty(value = "应用大小：单位(KB)", example = "")
    private Integer appSize;

    /**
     * 应用文件
     */
    @ApiModelProperty(value = "应用文件", example = "")
    private String appFile;

    /**
     * 图标文件
     */
    @ApiModelProperty(value = "图标文件", example = "")
    private String appIcon;

    /**
     * 源文件名
     */
    @ApiModelProperty(value = "源文件名", example = "")
    private String originalName;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序", example = "")
    private Integer sort;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述", example = "")
    private String remark;

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