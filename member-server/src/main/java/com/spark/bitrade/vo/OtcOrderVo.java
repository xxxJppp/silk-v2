package com.spark.bitrade.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class OtcOrderVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2711086273950572139L;
	private long id; //法币订单编号
	private int status;//法币订单状态
	private String messageId;//消息编号
}
