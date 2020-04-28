package com.spark.bitrade.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * (OtcOrder)实体类
 *
 * @author ss
 * @date 2020-03-19 10:23:49
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OtcOrder implements Serializable {
    private static final long serialVersionUID = -10954927812139093L;
    
    private Long id;
    
    private Long advertiseId;
    
    private Integer advertiseType;
    
    private String aliNo;
    
    private String bank;
    
    private String branch;
    
    private String cardNo;
    
    private Date cancelTime;
    /**
    * 手续费
    */
    private BigDecimal commission;
    
    private String country;
    
    private Date createTime;
    
    private Long customerId;
    
    private String customerName;
    
    private String customerRealName;
    /**
    * 最高交易额
    */
    private BigDecimal maxLimit;
    
    private Long memberId;
    
    private String memberName;
    
    private String memberRealName;
    /**
    * 最低交易额
    */
    private BigDecimal minLimit;
    /**
    * 交易金额
    */
    private BigDecimal money;
    /**
    * 交易数量
    */
    private BigDecimal number;
    
    private Long orderSn;
    
    private String payMode;
    
    private Date payTime;
    /**
    * 价格
    */
    private BigDecimal price;
    
    private Date releaseTime;
    
    private String remark;
    /**
    * 订单状态：0=已取消/1=未付款/2=已付款/3=已完成/4=申诉中
    */
    private Integer status;
    
    private Integer timeLimit;
    
    private Long version;
    
    private String wechat;
    
    private Long coinId;
    
    private String qrCodeUrl;
    
    private String qrWeCodeUrl;
    
    private String payCode;
    
    private Integer isManualCancel;
    
    private Long cancelMemberId;
    
    private String epayNo;
    
    private Integer payMethod;
    
    private Date closeTime;
    /**
    * 订单来源类型
    */
    private Integer orderSourceType;
    /**
    * 是否为一键交易
    */
    private Integer isOneKey;
    /**
    * 订单金额
    */
    private BigDecimal orderMoney;
    /**
    * 服务费
    */
    private BigDecimal serviceMoney;
    /**
    * 服务费率
    */
    private BigDecimal serviceRate;
    /**
    * 付款账号信息
    */
    private String payMethodInfo;
    
    private String wechatNick;
    
    private Integer isMerchantsBuy;


}