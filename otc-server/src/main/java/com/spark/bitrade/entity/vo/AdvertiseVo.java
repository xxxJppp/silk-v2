package com.spark.bitrade.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description:
 * @author: ss
 * @date: 2020/3/20
 */
@Data
public class AdvertiseVo {
    private long id;
    /**
     * 广告类型 0购买 1出售
     */
    private Integer advertiseType;
    /**
     * 最低单笔交易额
     */
    private BigDecimal minLimit;

    /**
     * 最高单笔交易额
     */
    private BigDecimal maxLimit;
    /**
     * 广告上下架状态
     * com.spark.bitrade.enums.AdvertiseControlStatus;
     */
    private Integer status;
    private BigDecimal remainAmount;

    private String coinUnit;
    private Integer coinId;

    /**
     * 单价
     */
    private BigDecimal price;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 价格类型 0固定的，1变化的
     */
    private Integer priceType;
    /**
     * 溢价比例
     */
    private BigDecimal premiseRate;


    /**
     * 编号
     */
    private Long currencyId;

    /**
     * 法币名称
     */
    private String currencyName;

    /**
     * 法币单位
     */
    private String currencyUnit;

    /**
     * 法币符号
     */
    private String currencySymbol;
}
