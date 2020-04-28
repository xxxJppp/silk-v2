package com.spark.bitrade.constant;

import lombok.AllArgsConstructor;


@AllArgsConstructor
public enum BizTypeEnum {
	  MEMBER_BUY(10,"会员购买"),


    MEMBER_LOCK(20,"会员错仓"),


  

    TOKEN_EXCHANGE(30,"币币交易"),
	
	CURRENCY_GET(40 ,"法币交易返佣") ,
    INV_MEMBER_GET(50 ,"直推会员USDC法币交易返佣") ,
    INV_CT_GET(60 ,"直推商家USDC法币交易返佣") 
    
    ;

    private final int code;

    private final String name;

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
