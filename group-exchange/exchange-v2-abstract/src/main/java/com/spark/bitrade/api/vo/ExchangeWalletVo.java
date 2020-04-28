package com.spark.bitrade.api.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zhouhaifeng
 * @time 2019.11.19 15:28
 */
@Data
public class ExchangeWalletVo {
    private String id;
    //会员id
    private Long memberId;
    //用户名
    private String username;
    //邮箱
    private String email;
    //会员注册时间
    private Date registrationTime;
    //手机号
    private String mobilePhone;
    //姓名
    private String realName;
    //币种名称
    private String coinId;
    //钱包地址
    private String address;
    //可用币数
    private BigDecimal balance = BigDecimal.ZERO;
    //冻结币数
    private BigDecimal frozenBalance = BigDecimal.ZERO;
    //总币数
    private BigDecimal totalBalance;
    //钱包状态
    private Integer isLock;
    //活期宝余额
    private BigDecimal hqb;
    //账户类型
    private Integer accountType;
}
