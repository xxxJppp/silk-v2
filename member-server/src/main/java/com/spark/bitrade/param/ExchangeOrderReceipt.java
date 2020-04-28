package com.spark.bitrade.param;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeOrderReceipt {

	//引用单据ID
	private String refId;
	//单据用户ID
	private long orderMemberId;
	//佣金原始币种(DB中会转换成平台币)
	private String commisionUnit;
	//原始返佣币数
	private BigDecimal commisionQuantity;
	
	//业务类型
	private Integer orderMatchType;
	
	private Date txTime;
	
	private String mqMessageId;
	
	//交易原始收费币种对返佣币种的汇率
	private BigDecimal txUnit2CommisionUnit;
	
	//返佣币种对CNY的汇率
	private BigDecimal commisionUnit2CNY;

	public ExchangeOrderReceipt(String refId, long orderMemberId, String commisionUnit, Integer orderMatchType,BigDecimal commisionQuantity,
			 Date txTime, String mqMessageId) {
		super();
		this.refId = refId;
		this.orderMemberId = orderMemberId;
		this.commisionUnit = commisionUnit;
		this.commisionQuantity = commisionQuantity;
		this.orderMatchType = orderMatchType;
		this.txTime = txTime;
		this.mqMessageId = mqMessageId;
	}
	
	
}
