package com.spark.bitrade.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.07.17 14:17  
 */
@Data
public class SuperPartnerCommunityForm {
//modify by qhliao 会员体系 取消锁仓
//    @ApiModelProperty("资金密码")
//    @NotBlank(message = "资金密码不能为空")
//    private String moneyPassword;

    @ApiModelProperty("社区名称")
    @NotBlank(message = "社区名称不能为空")
    private String communityName;

    @ApiModelProperty("申请理由")
    private String applyReason;

    @ApiModelProperty("微信二维码地址")
    @NotBlank(message = "微信二维码地址不能为空")
    private String wechatCodeUrl;

    @ApiModelProperty("微信号")
    @NotBlank(message = "微信号不能为空")
    private String wechatNum;

    @ApiModelProperty("推荐人id")
    private Long referrerId;
//modify by qhliao 会员体系 取消锁仓
//    @ApiModelProperty("币种 前端不传")
//    private String coinUnit;
//
//    @ApiModelProperty("锁仓金额 前端不传")
//    private BigDecimal amount;
//
//    @ApiModelProperty("USDT汇率 前端不传 SLU相对USDT的")
//    private BigDecimal usdtRate=BigDecimal.ZERO;
//
//    @ApiModelProperty("CNYT汇率 前端不传 CNY相对USDT的")
//    private BigDecimal cnytRate=BigDecimal.ZERO;

}
