package com.spark.bitrade.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class RedPackRecieveDetailVo {

    /**
     * 活动ID
     */
    @ApiModelProperty(value = "活动ID")
    private Long redpackId;

    /**
     * 活动名称
     */
    @ApiModelProperty(value = "活动名称")
    private String redpackName;

    /**
     * 会员id
     */
    @ApiModelProperty(value = "会员id")
    private Long memberId;

    /**
     * 领取币种
     */
    @ApiModelProperty(value = "领取币种")
    private String receiveUnit;

    /**
     * 领取数量
     */
    @ApiModelProperty(value = "领取数量")
    private BigDecimal receiveAmount;

    /**
     * 领取状态{0:未领取1:已领取,:已收回}
     */
    @ApiModelProperty(value = "领取状态{0:未领取1:已领取,:已收回}")
    private Integer receiveStatus;

    /**
     * 领取时间
     */
    @ApiModelProperty(value = "领取时间")
    private Date receiveTime;

    /**
     * 用户类型:{1:新会员,2:游客, 3:老会员}
     */
    @ApiModelProperty(value = "用户类型:{1:新会员,2:游客, 3:老会员}")
    private Integer userType;

    @ApiModelProperty("抽奖时间")
    private Date createTime;

    @ApiModelProperty("新会员总数(每条记录都一样)")
    private Integer newMemberCount;
}
