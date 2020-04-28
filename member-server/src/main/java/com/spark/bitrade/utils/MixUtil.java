package com.spark.bitrade.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

public class MixUtil {

	public static String getLocalMachineInfo() {
		InetAddress ia = null;
		try {
			ia = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		String host = ia.getHostName();
		return host;
	}

	public static void main(String[] args) {
		for (int i = 0; i < 100; i++) {
			int t =  getRandom();
			
			System.out.println(t);
		}
		
		
	}

	public static String getCurrentDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(new Date());
	}

	public static int getRandom() {
		int t =  (int)(Math.random()*3000)+2000;
		return t;
	}
	
}
