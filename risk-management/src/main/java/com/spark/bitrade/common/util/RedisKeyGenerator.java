package com.spark.bitrade.common.util;

/**
 * Redis key 生成
 *
 */
public class RedisKeyGenerator {
	
	/**
	 * 监控场景集合
	 * @return
	 */
	public static String riskSceneGet() {
		return "risk:scene:list";
	}
	
	/**
	 * 汇总数据更新锁
	 * @param memberId
	 * @return
	 */
	public static String getSummaryUpdateKey(String memberId) {
		return String.format("risk:lock:summary:update:%s", memberId);
	}
	
	/**
	 * 待更新任务集合
	 * @return
	 */
	public static String detailedWorkList() {
		return "risk:detailed:work:list";
	}
}
