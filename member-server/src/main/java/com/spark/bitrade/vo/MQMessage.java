package com.spark.bitrade.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class MQMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5090044164336615264L;
	private String topic;
	private String tag;
	private String message;
}
