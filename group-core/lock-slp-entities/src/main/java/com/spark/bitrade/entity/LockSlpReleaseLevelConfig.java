package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spark.bitrade.constant.BooleanEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 社区奖励级差配置表(LockSlpReleaseLevelConfig)表实体类
 *
 * @author yangch
 * @since 2019-06-18 21:27:03
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "社区奖励级差配置表")
public class LockSlpReleaseLevelConfig {

    /**
     * id=（币种+社区节点ID）
     */
    @TableId
    @ApiModelProperty(value = "id=（币种+社区节点ID）", example = "")
    private String id;

    /**
     * 币种，必须大写
     */
    @ApiModelProperty(value = "币种，必须大写", example = "")
    private String coinUnit;

    /**
     * 社区节点ID
     */
    @ApiModelProperty(value = "社区节点ID", example = "")
    private Integer levelId;

    /**
     * 社区节点名称
     */
    @ApiModelProperty(value = "社区节点名称", example = "")
    private String levelName;

    /**
     * 直推用户数量
     */
    @ApiModelProperty(value = "直推用户数量", example = "")
    private Integer promotionCount;

    /**
     * 理财本金总额，默认为0
     */
    @ApiModelProperty(value = "理财本金总额，默认为0", example = "")
    private BigDecimal performanceAmount;

    /**
     * 子节点数量
     */
    @ApiModelProperty(value = "子节点数量", example = "")
    private Integer subLevleCount;

    /**
     * 奖励比例
     */
    @ApiModelProperty(value = "奖励比例", example = "")
    private BigDecimal relaseRate;

    /**
     * 是否有平级奖（0=否，1=是）
     */
    @ApiModelProperty(value = "是否有平级奖（0=否，1=是）", example = "")
    private BooleanEnum enablePeers;

    /**
     * 平级奖励比例
     */
    @ApiModelProperty(value = "平级奖励比例", example = "")
    private BigDecimal peersRate;

    //  下级每日收益比例
    @ApiModelProperty(value = "下级每日收益比例(只适用于太阳等级，其他等级为0)", example = "")
    private BigDecimal subLevelRate;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序", example = "")
    private Integer sort;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间", example = "")
    private Date updateTime;


}