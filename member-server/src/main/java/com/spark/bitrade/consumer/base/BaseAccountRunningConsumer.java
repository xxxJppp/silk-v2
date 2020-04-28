package com.spark.bitrade.consumer.base;

import com.spark.bitrade.entity.Member;
import com.spark.bitrade.vo.AccountRunning;


public abstract class BaseAccountRunningConsumer extends ConsumerService {
    

    public boolean consumeMember(Member member,String msgId) {
    	return true;
    }
    
    @Override
    public boolean consumeNonCanalMessage(String message,String msgId) {
    	return true;
    }

    public abstract boolean consumeAccountRunning(AccountRunning car,String msgId);
    
}