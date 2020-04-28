package com.spark.bitrade.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
  * 广告排序类型
  * @author tansitao
  * @time 2018/8/27 16:28 
  */
@AllArgsConstructor
@Getter
public enum AdvertiseRankType {

    PRICE(0,"价格"),
    AMOUNT(1,"数量"),
    TRAN_NUM(2,"交易笔数");


    private Integer code;
    private String cnName;

}
