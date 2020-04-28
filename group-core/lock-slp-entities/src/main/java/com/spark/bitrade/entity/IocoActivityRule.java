package com.spark.bitrade.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * ioco对应活动规则(IocoActivityRule)表实体类
        *
        * @author daring5920
        * @since 2019-07-03 14:45:20
        */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "ioco对应活动规则")
public class IocoActivityRule {

    /**
     * 记录id
     */
    @TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "记录id", example = "")
    private Long id;

    /**
     * 关联的活动id
     */
    @ApiModelProperty(value = "关联的活动id", example = "")
    private Long activityId;

    /**
     * 最少社区人数（包含条件值）
     */
    @ApiModelProperty(value = "最少社区人数（包含条件值）", example = "")
    private BigDecimal gteMemberCount;

    /**
     * 最大社区人数（不包含条件值）
     */
    @ApiModelProperty(value = "最大社区人数（不包含条件值）", example = "")
    private BigDecimal ltMemberCount;

    /**
     * 总共申购的slp数量
     */
    @ApiModelProperty(value = "总共申购的slp数量", example = "")
    private BigDecimal maxSlpAmount;

    /**
     * 单次最低申购slp数量
     */
    @ApiModelProperty(value = "单次最低申购slp数量", example = "")
    private BigDecimal minSlpAmount;

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