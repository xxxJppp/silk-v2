package com.spark.bitrade.utils;

public class KeyGenerator {

	public static String getMemberInviteChainKey(Long memberId) {
		return "member:invite-chain:" + memberId;
	}
	
	public static String getRecommendCommisionKey(int levelId) {
		return "member:recommend-commision:" + levelId;
	}

	public static String getRequireConditionKey(int levelId) {
		return levelId == 999 ? "member:require-condition:*" : "member:require-condition:" + levelId;
	}

	public static String getmemberBenefitsExtend(long memberId) {
		return "member:berBenefits-extend:" + memberId;
	}

	public static String getMemberKey(long memberId) {
		return "member:member:" + memberId;
	}
	
	public static String getMemberExtendKey(long memberExtendId) {
		return "member:member-extend:" + memberExtendId;
	}

	public static String getPlatformTokenAnnualInterestRateKey() {
		return "member:platform-token-annual-interest-rate";
	}

	public static String getPlatformTokenKey() {
		return "member:platform-token";
	}
	
	public static String getMemberRecommendCommisionUnitKey() {
		return "member:member-recommend-commision-unit";
	}
	
	
	public static String getTokenExchangeFeeCommisionUnitKey() {
		return "member:token-exchange-fee-commision-unit";
	}

	public static String getBenefitsSettingKey(int levelId) {
		return "member:benefits-setting:" + levelId;
	}

	/**
	 * 用户 开通、升级、续费 vip 总费用记录
	 * @return
	 */
	public static String getMemberOpenVipAoumt(long memberId) {
		return "member:open-vip:amount:" + memberId;
	}

	public static String getDistributingCommisionList() {
		return "member:distributing-commision-ids";
	}
	
	/**
	 * otc订单返佣任务线程
	 * @return
	 * @author zhaopeng
	 * @since 2020年4月7日
	 */
	public static String getOtcOrderWorksKey() {
		return "member:otcorder:works";
	}
}
