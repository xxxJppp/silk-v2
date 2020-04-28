package com.spark.bitrade.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * <p>
 * 会员邀请佣金
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@ToString(includeFieldNames=true)
public class MemberRecommendCommision implements Serializable {

    private static final long serialVersionUID = 1L;
    
    
    

    public MemberRecommendCommision(String refId, Long deliverToMemberId, Long orderMemberId, Integer inviteLevel,
			String commisionUnit, BigDecimal commisionQuantity, BigDecimal platformUnitCnyRate,
			BigDecimal platformUnitRate, Integer distributeStatus, BigDecimal accumulativeQuantity, Integer bizType, BigDecimal commisionUsdtQty) {
		super();
		this.refId = refId;
		this.deliverToMemberId = deliverToMemberId;
		this.orderMemberId = orderMemberId;
		this.inviteLevel = inviteLevel;
		this.commisionUnit = commisionUnit;
		this.commisionQuantity= commisionQuantity == null ? new BigDecimal(0) : commisionQuantity.setScale(8, RoundingMode.DOWN);
		this.platformUnitCnyRate = platformUnitCnyRate;
		this.platformUnitRate = platformUnitRate;
		this.distributeStatus = distributeStatus;
		this.accumulativeQuantity = accumulativeQuantity == null ? new BigDecimal(0) : accumulativeQuantity.setScale(8, RoundingMode.DOWN);
		this.bizType = bizType;
		this.commisionUsdtQty= commisionUsdtQty;
	}

	@TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String refId;

    /**
     * 推荐人
     */
    @ApiModelProperty(value = "推荐人")
    private Long deliverToMemberId;

    /**
     * 交易会员
     */
    @ApiModelProperty(value = "交易会员")
    private Long orderMemberId;

    /**
     * 邀请层级
     */
    @ApiModelProperty(value = "邀请层级")
    private Integer inviteLevel;

    /**
     * 佣金币种
     */
    @ApiModelProperty(value = "佣金币种")
    private String commisionUnit;

    /**
     * 佣金数量
     */
    @ApiModelProperty(value = "佣金数量")
    private BigDecimal commisionQuantity;

    /**
     * 汇率
     */
    private BigDecimal platformUnitCnyRate;
    private BigDecimal platformUnitRate;

    /**
     * 发放状态
     */
    @ApiModelProperty(value = "发放状态{10-未发放, 20-已发放}")
    private Integer distributeStatus;

    /**
     * 发放时间
     */
    @ApiModelProperty(value = "发放时间")
    private Date distributeTime;

    /**
     * 累计未发数量
     */
    @ApiModelProperty(value = "累计未发数量")
    private BigDecimal accumulativeQuantity;

    /**
     * 转账记录id
     */
    private Long transferId;

    @ApiModelProperty(value = "备注")
    @TableField(exist = false)
    private String remarks;

    @TableField(exist = false)
    private BigDecimal tempCount;
    
    @TableField("mq_msg_id")
    private String mqMsgId;

    @TableField(exist = false)
    private String orderMemberName;
    
    @TableField("commision_usdt_qty")
    private BigDecimal commisionUsdtQty;

    /**
     * 业务类型
     */
    private Integer bizType;

    private Date createTime;

    private Date updateTime;

    public static String CREATE_TIME = "create_time";

    public static String DELIVER_TO_MEMBER_ID = "deliver_to_member_id";

    public static String BIZ_TYPE = "biz_type";

    public static String ORDER_MEMBER_ID = "order_member_id";

    public static String DISTRIBUTE_TIME = "distribute_time";
}
