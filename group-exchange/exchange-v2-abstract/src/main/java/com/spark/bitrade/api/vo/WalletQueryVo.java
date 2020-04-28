package com.spark.bitrade.api.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zhouhaifeng
 * @time 2019.11.19 14:43
 */
@Data
public class WalletQueryVo {
    private int page = 1;
    private int rows = 10;
    //钱包地址
    private String address;
    //币种名称
    private String coinId;
    //是否锁仓
    private Integer isLock;
    //最小可用余额
    private BigDecimal leastBalance;
    //最大可用余额
    private BigDecimal mostBalance;
    //最小冻结余额
    private BigDecimal leastFrozenBalance;
    //最大冻结余额
    private BigDecimal mostFrozenBalance;
    //最小总币数
    private BigDecimal leastTotalBalance;
    //最大总币数
    private BigDecimal mostTotalBalance;
    //会员id
    private String memberId;

}
