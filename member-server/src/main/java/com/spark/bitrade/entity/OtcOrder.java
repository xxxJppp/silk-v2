package com.spark.bitrade.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author qiliao
 * @since 2020-04-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OtcOrder implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id; //编号

    private Integer advertiseType; //0买入1卖出

    private BigDecimal number;//交易数量
    
    private Long memberId; // 商家编号
    
    private Long customerId;//用户编号

    private Long coinId;//币种id
    
    private Integer status;//订单状态，仅关注3完成与5关闭
    
    private String orderSn;//订单号
    
    private Integer payMethod;//购买类型
    
    private Date payTime;
   

}
