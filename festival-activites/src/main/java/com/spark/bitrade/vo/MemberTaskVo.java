package com.spark.bitrade.vo;

import lombok.Data;

/**
 * <p>
 * 活动列表查询实体
 * </p>
 *
 * @author Administrator
 * @since 2020年1月7日
 */
@Data
public class MemberTaskVo {

	private String taskStatus;//活动标记
	private Integer complete;//是否完成 1完成0未完成
	private String completeAllCount;//活动完成总次数
	
	public MemberTaskVo() {}
	public MemberTaskVo(String taskKey , Integer complete , String completeAllCount) {
		this.taskStatus = taskKey ;
		this.complete = complete;
		this.completeAllCount = completeAllCount;
	}
}
