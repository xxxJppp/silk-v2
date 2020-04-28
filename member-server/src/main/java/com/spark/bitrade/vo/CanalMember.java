package com.spark.bitrade.vo;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(includeFieldNames=true)
public class CanalMember implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7642955111999300216L;
	private long id;
	private long inviter_id;
}
