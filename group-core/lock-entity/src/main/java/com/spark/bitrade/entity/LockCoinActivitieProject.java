package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spark.bitrade.constant.ActivitieType;
import com.spark.bitrade.constant.LockSettingStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 锁仓活动配置(LockCoinActivitieProject)表实体类
 *
 * @author zhangYanjun
 * @since 2019-06-19 15:18:12
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "锁仓活动配置")
public class LockCoinActivitieProject {

    @TableId
    @ApiModelProperty(value = "", example = "")
    private Long id;

    /**
     * 管理员id
     */
    @ApiModelProperty(value = "管理员id", example = "")
    private Long adminId;

    /**
     * 活动参与数量（币数、份数）
     */
    @ApiModelProperty(value = "活动参与数量（币数、份数）", example = "")
    private BigDecimal boughtAmount;

    /**
     * 活动币种符号
     */
    @ApiModelProperty(value = "活动币种符号", example = "")
    private String coinSymbol;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 活动内容（富文本）
     */
    @ApiModelProperty(value = "活动内容（富文本）", example = "")
    private String description;

    /**
     * 活动截止时间
     */
    @ApiModelProperty(value = "活动截止时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    /**
     * 活动连接地址
     */
    @ApiModelProperty(value = "活动连接地址", example = "")
    private String link;

    /**
     * 最大购买数量（币数、份数）
     */
    @ApiModelProperty(value = "最大购买数量（币数、份数）", example = "")
    private BigDecimal maxBuyAmount;

    /**
     * 最低购买数量（币数、份数）
     */
    @ApiModelProperty(value = "最低购买数量（币数、份数）", example = "")
    private BigDecimal minBuyAmount;

    /**
     * 活动方案名称
     */
    @ApiModelProperty(value = "活动方案名称", example = "")
    private String name;

    /**
     * 活动计划数量（币数、份数）
     */
    @ApiModelProperty(value = "活动计划数量（币数、份数）", example = "")
    private BigDecimal planAmount;

    /**
     * 活动开始时间
     */
    @ApiModelProperty(value = "活动开始时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    /**
     * 活动状态，0未生效，1已生效，2已失效
     */
    @ApiModelProperty(value = "活动状态，0未生效，1已生效，2已失效", example = "")
    private LockSettingStatus status;

    /**
     * 活动类型，0锁仓活动，1理财锁仓，2其它，3 SLB节点产品.....
     */
    @ApiModelProperty(value = "活动类型，0锁仓活动，1理财锁仓，2其它，3 SLB节点产品......", example = "")
    private ActivitieType type;

    /**
     * 每份数量
     */
    @ApiModelProperty(value = "每份数量", example = "")
    private BigDecimal unitPerAmount;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 图片地址
     */
    @ApiModelProperty(value = "图片地址", example = "")
    private String imgUrl;

    /**
     * 收益图片
     */
    @ApiModelProperty(value = "收益图片", example = "")
    private String incomeImg;

    /**
     * 标题图片
     */
    @ApiModelProperty(value = "标题图片", example = "")
    private String titleImg;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序", example = "")
    private Integer sort;

    /**
     * 是否置顶{0:否,1:是}
     */
    @ApiModelProperty(value = "是否置顶{0:否,1:是}", example = "")
    private Integer top;

    /**
     * 是否显示{0:否,1:是}
     */
    @ApiModelProperty(value = "是否显示{0:否,1:是}", example = "")
    private Integer enableShow;


}