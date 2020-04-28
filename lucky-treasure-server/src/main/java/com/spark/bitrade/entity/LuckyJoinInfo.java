package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.enums.SettleStatus;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 幸运宝参与明细
 * </p>
 *
 * @author qiliao
 * @since 2019-12-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class LuckyJoinInfo implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 活动ID
     */
    @ApiModelProperty(value = "活动id")
    private Long numId;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private Long memberId;

    /**
     * 参与信息票号、牛种等
     */
    @ApiModelProperty(value = "参与信息票号、牛种等")
    private String joinInfo;

    /**
     * 是否中奖 0未1已
     */
    @ApiModelProperty(value = "是否中奖")
    private BooleanEnum win;

    /**
     * 是否追加(分享)
     */
    @ApiModelProperty(value = "是否追加")
    private BooleanEnum appendWx;

    /**
     * 中奖数量
     */
    @ApiModelProperty(value = "中奖数量")
    private BigDecimal awardAmount;

    /**
     * 追加中奖数量
     */
    @ApiModelProperty(value = "追加中奖数量")
    private BigDecimal addAwardAmount;

    /**
     * 更新人
     */
    @ApiModelProperty(value = "创建人")
    private Long createId;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人")
    private Long updateId;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    /**
     * 删除状态0正常1删除
     */
    @ApiModelProperty(value = "删除状态")
    private BooleanEnum deleteState;
    /**
     * 结算状态 0 未中奖   , 1:已退款 2:退款失败 3:已发放奖金 4:奖金发放失败
     */
    @ApiModelProperty(value = "结算状态")
    private SettleStatus settleStatus;

}











