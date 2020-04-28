package com.spark.bitrade;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;
import com.spark.bitrade.constant.Constant;
import com.spark.bitrade.convertor.ObjectConvertor;
import com.spark.bitrade.entity.MemberRecommendCommisionSetting;
import com.spark.bitrade.utils.ClazzUtil;
import com.spark.bitrade.utils.JsonUtil;
import com.spark.bitrade.vo.AccountRunning;
import com.spark.bitrade.vo.CanalAccountRunning;
import com.spark.bitrade.vo.CanalMessage;

import net.sf.json.JSONArray;


public class T {

	public static void main(String[] args) throws ClassNotFoundException, Exception {

		
//		List<MemberRecommendCommisionSetting> rcsList = Lists.newArrayList();
//		
//		MemberRecommendCommisionSetting s1 = new MemberRecommendCommisionSetting();
//		s1.setId(1);
//		s1.setLevelId(1);
//		s1.setRecommendLevel(0);
//		s1.setCommisionRatio(new BigDecimal(0.2));
//		
//		
//		MemberRecommendCommisionSetting s2 = new MemberRecommendCommisionSetting();
//		s2.setId(2);
//		s2.setLevelId(1);
//		s2.setRecommendLevel(1);
//		s2.setCommisionRatio(new BigDecimal(0.25));
//		
//		MemberRecommendCommisionSetting s3 = new MemberRecommendCommisionSetting();
//		s3.setId(3);
//		s3.setLevelId(1);
//		s3.setRecommendLevel(2);
//		s3.setCommisionRatio(new BigDecimal(0.225));
//		
//		MemberRecommendCommisionSetting s4 = new MemberRecommendCommisionSetting();
//		s4.setId(4);
//		s4.setLevelId(1);
//		s4.setRecommendLevel(3);
//		s4.setCommisionRatio(new BigDecimal(0.18));
//		
//		rcsList.add(s1);
//		rcsList.add(s2);
//		rcsList.add(s3);
//		rcsList.add(s4);
//		
//		
//		
//		System.out.println(getRecommendCommisionRatioByLevel(rcsList,10));
		
		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		long t1 = sdf.parse("2019-12-17 00:23:09").getTime();
//		long t2 = sdf.parse("2019-12-17 18:23:50").getTime();
//		
////		System.out.println(t1 + "--------------"+ t2);
//		
////		System.out.println(sdf.format(new Date(DateUtil.beginOfSecond(sdf.parse("2019-12-17 00:23:09")).getTime())));
//		
//	        
//	     String json = "[[1576418400000,0.01913,0.01991,0.01899,0.01968,323014.83388],[1576422000000,0.01956,0.02020,0.01948,0.01997,347810.90011],[1576425600000,0.01974,0.02005,0.01939,0.01953,348521.43853]]";
//	
		
		String s = "{\"data\":[{\"id\":\"1209370847337250817\",\"member_id\":\"360626\",\"coin_unit\":\"BT\",\"trade_balance\":\"140.0\",\"trade_frozen\":\"0.0\",\"fee\":\"60.0\",\"fee_discount\":\"40.0\",\"rate\":\"0.14285714\",\"trade_type\":\"3\",\"ref_id\":\"E3606261209370832401334274\",\"sync_id\":\"0\",\"signature\":\"Ko8PJegbshFa2dxImtUYrSUO2vCmOAW6SwuxOtaAWv5AEoHDyC0W1H3xuEmQAaeBIab2DYG2cEzOLfiAim1Vtg==\",\"status\":\"0\",\"remark\":\"匹配订单：E3605721209370171089616897\",\"create_time\":\"2019-12-24 15:10:54\",\"update_time\":\"2019-12-24 15:10:54\",\"order_match_type\":\"0\"}],\"database\":\"otc_sync\",\"es\":1577171445000,\"id\":89818,\"isDdl\":false,\"mysqlType\":{\"id\":\"bigint(20)\",\"member_id\":\"bigint(20)\",\"coin_unit\":\"varchar(50)\",\"trade_balance\":\"decimal(24,8)\",\"trade_frozen\":\"decimal(24,8)\",\"fee\":\"decimal(24,8)\",\"fee_discount\":\"decimal(24,8)\",\"rate\":\"decimal(24,8)\",\"trade_type\":\"int(11)\",\"ref_id\":\"varchar(128)\",\"sync_id\":\"bigint(20)\",\"signature\":\"varchar(512)\",\"status\":\"int(11)\",\"remark\":\"varchar(512)\",\"create_time\":\"datetime\",\"update_time\":\"datetime\",\"order_match_type\":\"int(11)\"},\"old\":null,\"pkNames\":[\"id\"],\"sql\":\"\",\"sqlType\":{\"id\":-5,\"member_id\":-5,\"coin_unit\":12,\"trade_balance\":3,\"trade_frozen\":3,\"fee\":3,\"fee_discount\":3,\"rate\":3,\"trade_type\":4,\"ref_id\":12,\"sync_id\":-5,\"signature\":12,\"status\":4,\"remark\":12,\"create_time\":93,\"update_time\":93,\"order_match_type\":4},\"table\":\"exchange_wallet_wal_record_2\",\"ts\":1577171455710,\"type\":\"INSERT\"}";
		
		CanalMessage cm = JsonUtil.json2Object(s, new TypeToken<CanalMessage>(){}.getType());
		
		System.out.println(cm);
		
		
		List<Map<String, Object>> list = cm.getData();
			for (Map<String, Object> map : list) {
				CanalAccountRunning car = ClazzUtil.mapToBean(map, Class.forName(Constant.CLAZZ_ACCOUNT_RUNNING));
				AccountRunning ar = ObjectConvertor.convert2AccountRunning(car);
				System.out.println(ar);
			}
	     

	}
	
	
	public static MemberRecommendCommisionSetting getRecommendCommisionRatioByLevel(List<MemberRecommendCommisionSetting> rcsList,int level) {
		MemberRecommendCommisionSetting notLimit = null;
		MemberRecommendCommisionSetting result = null;
		for (MemberRecommendCommisionSetting mrcs : rcsList) {
			if(mrcs.getRecommendLevel() == level) {
				result = mrcs;
			}
			if(mrcs.getRecommendLevel() == 0) {
				notLimit = mrcs;
			}
		}
		return null == result ? notLimit : result;
	}
}
