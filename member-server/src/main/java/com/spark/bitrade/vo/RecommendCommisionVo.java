package com.spark.bitrade.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author: Zhong Jiang
 * @date: 2019-12-19 21:56
 */
@Data
public class RecommendCommisionVo {

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

    private String remarks;

    private BigDecimal tempCount;

    private String mqMsgId;

    private String orderMemberName;

    private Integer bizType;

    private Date createTime;

    private Date updateTime;
}
