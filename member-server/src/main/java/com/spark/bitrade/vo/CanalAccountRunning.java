package com.spark.bitrade.vo;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(includeFieldNames=true)
public class CanalAccountRunning implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7642955111999300216L;
	private long id;
	private long member_id;
	private String coin_unit;
	private String trade_balance;
	private String trade_frozen;
	private String fee;
	private String fee_discount;
	private String rate;
	private int trade_type;
	private String ref_id;
	private int trade_status;
	private String remark;
//	private int biz_type;
	private String create_time;
	private String update_time;
	private Integer order_match_type;
	
}
