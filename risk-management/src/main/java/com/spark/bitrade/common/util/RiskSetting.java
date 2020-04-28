package com.spark.bitrade.common.util;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.shiro.crypto.hash.SimpleHash;

/**
 * 风控配置
 * @author zhao
 *
 */
public class RiskSetting {

	public static class RiskDetailedType {
		public static final String RISK_ADJUSTMENT = "风险调整";
		public static final String RISK_FB_BUY = "法币入场";
		public static final String RISK_FB_PAY = "法币出场";
		public static final String INIT_DATA = "人工调账";
	}
	
	public static class ConfigSetting {
		public static final String RISK_OPEN_NAME = "风控开关 0关闭 1 开启";
		public static final String RISK_OPEN_KEY = "risk:setting:open";
		
		public static final String RISK_COEFFICIENT_NAME = "风险系数";
		public static final String RISK_COEFFICIENT_KEY = "risk:setting:coefficient";
	}
	
	public static String getAbstrackKey(BigDecimal money ,String unti , Long memberId , String inout , Date time) {
		return new SimpleHash("md5", money.toString() + "_" +unti + "_" + inout, memberId.toString() + "_" + time.getTime(), 2).toHex().toLowerCase();
	}
}
