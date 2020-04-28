package com.spark.bitrade.entity.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * WalletSyncCountDto
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/9 17:45
 */
@Data
public class WalletSyncCountDto {

    private BigDecimal amount;
    private BigDecimal increment;
    private BigDecimal frozen;
}
