package com.spark.bitrade.common.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ThreadPoolUtils {

	private static Log logger = LogFactory.getLog(ThreadPoolUtils.class);
	private static ThreadPoolExecutor fixedThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(30);
	static{
		fixedThreadPool.setKeepAliveTime(5, TimeUnit.SECONDS); 
	}
	
	public static synchronized void putThread(Thread t) {
		fixedThreadPool.execute(t);
		logger.info("Task join queue , Current number of jobs 【"+(fixedThreadPool.getActiveCount())+"】,Number of threads alive【"+fixedThreadPool.getPoolSize()+"】");
	}
}
