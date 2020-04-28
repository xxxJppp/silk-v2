package com.spark.bitrade.common.listen;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import com.spark.bitrade.common.consumer.EventConsumer;

@Component
public class CloseListen implements ApplicationListener<ContextClosedEvent> {

	private Log logger = LogFactory.getLog(this.getClass());
	
	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		logger.info("服务停止，终止异步更新....");
		EventConsumer.WORK_CHECK_DO = false; //停止异步线程继续处理更新
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
