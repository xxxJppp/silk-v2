package com.spark.bitrade.common;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Redis key 生成
 *
 */
public class ReidsKeyGenerator {
	
	/**
	 * 活动已完成钥匙合成人员总数
	 */
	public static String getCompleteMemberCount() {
		return  "newyear:mineral:complete:count";
	}
	
	/**
	 * 活动中矿石种类(名称)   set
	 */
	public static String getMineralType() {
		return  "newyear:mineral:type";
	}
	/**
	 * 矿石种类加载锁
	 */
	public static String getMineralTypeLock() {
		return  "newyear:mineral:typelock";
	}
	
	/**
	 * 令牌token生成
	 * @return
	 * @author zhaopeng
	 * @since 2019年12月31日
	 */
	public static String synthesisToken() {
		return "newyear:mineral:synthesis:token";
	}
	
	/**
	 * 已合成令牌的用户id集合
	 * 合成后加入到集合中用于重复合成排除和合成总数、开奖检查
	 * @return
	 * @author zhaopeng
	 * @since 2019年12月31日
	 */
	public static String getSynthesisMemberIdSetKey() {
		return "newyear:mineral:synthesis:members";
	}
	
	/**
	 * 获得已开奖的用户id集合
	 * @return
	 * @author zhaopeng
	 * @since 2020年1月13日
	 */
	public static String getLotteryMembers() {
		return "newyear:mineral:lottery:members";
	}
	
	/**
	 * 获得开奖锁
	 * @param memberId
	 * @return
	 * @author zhaopeng
	 * @since 2020年1月13日
	 */
	public static String getLotteryLock(String memberId) {
		return String.format("newyear:mineral:lottery:lock:%s", memberId);
	}
	
	/**
	 * 最新生成令牌用户的信息
	 * value :  memberName_时间戳
	 * @return
	 * @author zhaopeng
	 * @since 2019年12月31日
	 */
	public static String getSynthesisMemberTop10() {
		return "newyear:mineral:synthesis:topMember";
	}
	
	/**
	 * 矿石消耗锁
	 * @param memberId
	 * @return
	 * @author zhaopeng
	 * @since 2019年12月31日
	 */
	public static String getMineralLock(String memberId) {
		return String.format("newyear:mineral:lock:%s", memberId);
	}

	/**
	 * 矿石剩余数量
	 * @param type
	 * @return
	 */
	public static String getMineralKey(Integer type) {
		return type == 999 ? "mineral:surplus:*" : "mineral:surplus:" + type;
	}

	/**
	 * 矿石剩余矿石池（所有）
	 * @return
	 */
	public static String getMineralNumberNotZeroKey() {
		return "mineral:notZero:size";
	}

	/**
	 * 获得挖矿机会锁
	 * @param key
	 * @param memberId
	 * @return
	 * @author zhaopeng
	 * @since 2020年1月8日
	 */
	public static String taskStatusLock(String key , String memberId) {
		return String.format("newyear:mineral:taskStatusLock:%s", key) + ":"+memberId;
	}
	
	/**
	 * 个人信息初始化锁
	 * @param memberId
	 * @return
	 * @author zhaopeng
	 * @since 2020年1月8日
	 */
	public static String taskMemberInfoCreateLock(String memberId) {
		return "newyear.mineral:taskMember:createLock:" + memberId;
	}
	
	/**
	 * 推荐好友完成次数
	 * @param memberId
	 * @return
	 * @author zhaopeng
	 * @since 2020年1月8日
	 */
	public static String getTaskRegistStatus(String memberId) {
		return "newyear:mineral:taskRegistStatusStatus:" + memberId;
	}
	
	/**
	 * 首次币币交易完成次数
	 * @param memberId
	 * @return
	 * @author zhaopeng
	 * @since 2020年1月7日
	 */
	public static String getTaskExchangeStatus(String memberId) {
		return "newyear:mineral:taskExchangeStatus:" + memberId;
	}
	
	/**
	 * 首次充币完成次数
	 * @param memberId
	 * @return
	 * @author zhaopeng
	 * @since 2020年1月7日
	 */
	public static String getTaskRechargeStatus(String memberId) {
		return "newyear:mineral:taskRechargeStatus:" + memberId;
	}
	
	/**
	 * 首次法币完成次数
	 * @param memberId
	 * @return
	 * @author zhaopeng
	 * @since 2020年1月7日
	 */
	public static String getTaskOtcStatus(String memberId) {
		return "newyear:mineral:taskOtcStatus:" + memberId;
	}
	
	/**
	 * 币币交易买单10分钟完成次数
	 * @param memberId
	 * @return
	 * @author zhaopeng
	 * @since 2020年1月7日
	 */
	public static String getTaskPutStatus(String memberId) {
		return "newyear:mineral:taskPutStatus:" + memberId;
	}
	
	/**
	 * 首次登录完成总次数
	 * @param memberId
	 * @return
	 * @author zhaopeng
	 * @since 2020年1月7日
	 */
	public static String getTaskLoginStatus(String memberId) {
		return "newyear:mineral:taskLoginStatus:" + memberId;
	}
	
	/**
	 * 获取初始化static表锁
	 * @return
	 * @author zhaopeng
	 * @since 2020年1月15日
	 */
	public static String getInitStaticLock(String coin) {
		return "newyear:mineral:initstatic:lock:"+coin;
	}
	
	/**
	 * 获取用户指定币种释放锁
	 * @param memberId
	 * @param unit
	 * @return
	 * @author zhaopeng
	 * @since 2020年2月3日
	 */
	public static String getrecordCheckList(String date) {
		return "newyear:recordcheckList:" + date;
	}
	
	/**
	 * 释放锁
	 * @param memberId
	 * @param unit
	 * @return
	 * @author zhaopeng
	 * @since 2020年2月3日
	 */
	public static String getRecordCheckLock(String memberId ,  String unit) {
		return String.format("newyear:recordcheck:lock:%s:%s", memberId , unit);
	}
	
	/**
	 * 获取用户
	 * @param memberId
	 * @param unit
	 * @return
	 * @author zhaopeng
	 * @since 2020年2月3日
	 */
	public static String getMemberUnitNum(String memberId , String unit) {
		return String.format("newyear:recordcheck:less:%s:%s:" + (new SimpleDateFormat("yyyyMMdd").format(new Date())), memberId , unit);
	}
}
