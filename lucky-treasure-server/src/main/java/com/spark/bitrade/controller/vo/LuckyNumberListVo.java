package com.spark.bitrade.controller.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.entity.LuckyJoinInfo;
import com.spark.bitrade.entity.LuckyNumberManager;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "幸运号活动列表")
@Data
public class LuckyNumberListVo {
	  private Long id;

	    /**
	     * 活动名称
	     */
	  @ApiModelProperty(value = "活动名称")
	    private String name;

	    /**
	     * 活动开始时间
	     */
	  @ApiModelProperty(value = "活动开始时间")
	    private Date startTime;

	    /**
	     * 活动结束时间
	     */
	  @ApiModelProperty(value = "活动结束时间")
	    private Date endTime;

	    /**
	     * 活动开奖时间
	     */
	  @ApiModelProperty(value = "活动开奖时间")
	    private Date luckyTime;

	    /**
	     * 活动币种
	     */
	  @ApiModelProperty(value = "活动币种")
	    private String unit;

	    /**
	     * 活动票面价
	     */
	  @ApiModelProperty(value = "活动票面价")
	    private BigDecimal amount;

	    /**
	     * 单人最大票数
	     */
	  @ApiModelProperty(value = "单人最大票数")
	    private Integer singleMaxNum;

	    /**
	     * 是否隐藏  {0:否(不隐藏) 1:是(隐藏)}
	     */
	  @ApiModelProperty(value = "是否隐藏  {0:否(不隐藏) 1:是(隐藏)}")
	    private BooleanEnum hidden;

	    /**
	     * 最小开奖票数
	     */
	  @ApiModelProperty(value = "最小开奖票数")
	    private Integer minTicketNum;

	    /**
	     * 备注
	     */
	  @ApiModelProperty(value = "备注")
	    private String remarks;

	    /**
	     * 活动类型{0:幸运号,1:小牛快跑}
	     */
	  @ApiModelProperty(value = "活动类型{0:幸运号,1:小牛快跑}")
	    private Integer actType;

	    /**
	     * 是否结算
	     */
	  @ApiModelProperty(value = "是否结算")
	    private BooleanEnum isSettlement;

	    /**
	     * 参与总人数（活动结束后更新，活动期间在缓存中获取）
	     */
	  @ApiModelProperty(value = "参与总人数（活动结束后更新，活动期间在缓存中获取）")
	    private Integer joinMemberCount;

	    /**
	     * 参与总金额
	     */
	  @ApiModelProperty(value = "参与总金额")
	    private BigDecimal joinMemberAmount;

	    /**
	     * 购买总票数
	     */
	  @ApiModelProperty(value = "购买总票数")
	    private Integer joinTicketCount;

	    /**
	     * 中奖票数
	     */
	  @ApiModelProperty(value = "中奖票数")
	    private Integer winNum;

	    /**
	     * 中奖票号（多票号 英文半角逗号分隔）
	     */
	  @ApiModelProperty(value = "中奖票号（多票号 英文半角逗号分隔）")
	    private String winTickets;

	    /**
	     * 中奖总人数
	     */
	  @ApiModelProperty(value = "中奖总人数")
	    private Integer winMemberCount;

	  @ApiModelProperty(value = "个人参与明细")
	    private List<LuckyJoinInfo> joins;
	  @ApiModelProperty(value = "个人中奖号")
	   private List<String> memberWinTickets;
	  @ApiModelProperty(value = "个人中奖总金额")
	  private BigDecimal memberWinMoney;
	  @ApiModelProperty(value = "个人界面展示中奖追加总金额")
	  private BigDecimal memberWinAppendMoney;
	  @ApiModelProperty(value = "个人实际中奖追加总金额")
	  private BigDecimal memberAppednWx;
	  @ApiModelProperty(value = "平台手续费")
	  private BigDecimal platformProfit;
		public LuckyNumberListVo() {
		}
		
		public LuckyNumberListVo(LuckyNumberManager manager) {
			this.platformProfit = manager.getPlatformProfit();
			this.actType = manager.getActType();
			this.amount = manager.getAmount();
			this.endTime = manager.getEndTime();
			this.hidden = manager.getHidden();
			this.id = manager.getId();
			this.isSettlement = manager.getIsSettlement();
			this.joinMemberAmount = manager.getJoinMemberAmount();
			this.joinMemberCount = manager.getJoinMemberCount();
			this.joinTicketCount = manager.getJoinTicketCount();
			this.luckyTime = manager.getLuckyTime();
			this.minTicketNum = manager.getMinTicketNum();
			this.name = manager.getName();
			this.remarks = manager.getRemarks();
			this.singleMaxNum = manager.getSingleMaxNum();
			this.startTime = manager.getStartTime();
			this.unit = manager.getUnit();
			this.winMemberCount = manager.getWinMemberCount();
			this.winNum = manager.getWinNum();
			this.winTickets = manager.getWinTickets();
		}
	    
	    
}
