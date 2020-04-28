package com.spark.bitrade.consumer.base;

import com.spark.bitrade.entity.Member;
import com.spark.bitrade.vo.AccountRunning;


public abstract class BaseMemberConsumer extends ConsumerService {
    

    public abstract boolean consumeMember(Member member,String msgId);

    public  boolean consumeAccountRunning(AccountRunning car,String msgId) {
    	return true;
    }
    
    @Override
    public boolean consumeNonCanalMessage(String message,String msgId) {
    	return true;
    }
    
}