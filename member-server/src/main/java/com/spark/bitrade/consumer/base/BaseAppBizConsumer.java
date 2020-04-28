package com.spark.bitrade.consumer.base;

import com.spark.bitrade.entity.Member;
import com.spark.bitrade.vo.AccountRunning;


public abstract class BaseAppBizConsumer extends ConsumerService {
    

    public boolean consumeMember(Member member,String msgId) {
    	return true;
    }

    public  boolean consumeAccountRunning(AccountRunning car,String msgId) {
    	return true;
    }
    @Override
    public abstract boolean consumeNonCanalMessage(String message,String msgId);
    
}