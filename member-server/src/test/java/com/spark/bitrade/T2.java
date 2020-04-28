package com.spark.bitrade;

import java.text.SimpleDateFormat;
import java.util.Date;

public class T2 {

	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		System.out.println(sdf.format(new Date(1577077200000L)));
	}
}
