package com.spark.bitrade.vo;

import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(includeFieldNames=true)
public class CanalMessage {
	private String type;
	private String table;
	private long ts;
	private List<Map<String, Object>> data;
	private long es;
	private String database;

}
