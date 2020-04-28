package com.spark.bitrade.mapper.dto;

import com.spark.bitrade.entity.constants.WalTradeType;
import lombok.Data;

import java.math.BigDecimal;

/**
 * WalRecordDto
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/26 14:25
 */
@Data
public class WalRecordDto {

    private Long id;
    private String refId;
    private WalTradeType tradeType;
    private BigDecimal tradeBalance;
    private BigDecimal tradeFrozen;

}
