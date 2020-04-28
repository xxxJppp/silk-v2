package com.spark.bitrade.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(includeFieldNames=true)
public class AccountRunning implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7642955111999300216L;
	private long id;
	private long memberId;
	private String coinUnit;
	private String refId;
	private BigDecimal tradeBalance;
	private BigDecimal tradeFrozen;
	private BigDecimal fee;
	private BigDecimal feeDiscount;
	private BigDecimal rate;
	private int tradeType;
//	private int bizType;
	private Integer orderMatchType;
	private Timestamp createTime;
	private Timestamp updateTime;
}
