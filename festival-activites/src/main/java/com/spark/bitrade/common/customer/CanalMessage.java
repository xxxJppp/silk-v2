package com.spark.bitrade.common.customer;

import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(includeFieldNames=true)
public class CanalMessage {
	private String messageId;
	private String type;
	private String table;
	private List<Map<String, Object>> data;
	private String database;
	private List<Map<String, Object>> old;

}
