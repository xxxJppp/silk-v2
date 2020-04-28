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
 * 自动化脚本(SilkPayScript)表实体类
 *
 * @author wsy
 * @since 2019-07-18 10:36:06
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "自动化脚本")
public class SilkPayScript {

    /**
     * 脚本编号
     */
    @TableId
    @ApiModelProperty(value = "脚本编号", example = "")
    private Long id;

    /**
     * 脚本名称
     */
    @ApiModelProperty(value = "脚本名称", example = "")
    private String name;

    /**
     * 脚本文件
     */
    @ApiModelProperty(value = "脚本文件", example = "")
    private String file;

    /**
     * 脚本版本
     */
    @ApiModelProperty(value = "脚本版本", example = "")
    private String version;

    /**
     * 启动入口
     */
    @ApiModelProperty(value = "启动入口", example = "")
    private String bootEntry;

    /**
     * 工具版本：限制工具最低版本
     */
    @ApiModelProperty(value = "工具版本：限制工具最低版本", example = "")
    private Integer minVersion;

    /**
     * 脚本状态：0-历史版本 1-最先版本
     */
    @ApiModelProperty(value = "脚本状态：0-历史版本 1-最先版本", example = "")
    private Integer state;

    /**
     * 脚本描述
     */
    @ApiModelProperty(value = "脚本描述", example = "")
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