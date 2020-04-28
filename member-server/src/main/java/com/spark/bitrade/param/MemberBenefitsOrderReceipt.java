package com.spark.bitrade.param;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberBenefitsOrderReceipt {

	//引用单据ID
	private String refId;
	//单据用户ID
	private long orderMemberId;
	//佣金原始币种(DB中会转换成平台币)
	private String commisionUnit;
	//原始返佣币数
	private BigDecimal commisionQuantity;
	//支付类型(仅限会员购买使用)
	private int payType;
	//锁仓天数(仅限会员购买使用)
	private long lockDay;
	//支付时间(仅限会员购买使用)
	private Date payTime;
	
	private String mqMsgId;

	public MemberBenefitsOrderReceipt(String refId, long orderMemberId, String commisionUnit, BigDecimal commisionQuantity,
			int bizType, Date payTime) {
		super();
		this.refId = refId;
		this.orderMemberId = orderMemberId;
		this.commisionUnit = commisionUnit;
		this.commisionQuantity = commisionQuantity;
		this.payType = bizType;
		this.payTime = payTime;
	}
	
	
}
