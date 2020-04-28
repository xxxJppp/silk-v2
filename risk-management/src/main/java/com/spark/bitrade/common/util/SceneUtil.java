package com.spark.bitrade.common.util;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;

import lombok.Data;

/**
 * 场景过滤条件
 * @author zhao
 *
 */
public class SceneUtil {

	public static String EQ = "EQ";//等值
	public static String LT = "LT";//字段小于
	public static String MT = "MT";//字段大于
	
	/**
	 * 多个条件判断均使用 && 符号
	 * @author zhao
	 *
	 */
	@Data
	public static class Parames {
		private String checkType;
		private String checkColumn;
		private double checkVal;
		
		public Parames() {}
		public Parames(String checkType , String column , double checkVal) {
			this.checkType = checkType;
			this.checkColumn = column;
			this.checkVal = checkVal;
		}
	}
	
	public static void main(String[] args) {
		List<Parames> ps = new ArrayList<SceneUtil.Parames>();
		Parames p = new Parames(EQ, "type", 0);
		Parames p2 = new Parames(MT, "amount", 0);
		
		ps.add(p);
		ps.add(p2);
		
		System.out.println(JSONArray.toJSONString(ps));
	}
}
