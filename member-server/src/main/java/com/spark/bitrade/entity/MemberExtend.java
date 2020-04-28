package com.spark.bitrade.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(includeFieldNames=true)
public class MemberExtend {

	private long memberId;
	private int levelId;
	private long id;
}
