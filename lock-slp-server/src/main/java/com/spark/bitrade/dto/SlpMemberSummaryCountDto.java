package com.spark.bitrade.dto;

import lombok.Setter;

import java.math.BigDecimal;

/**
 * SlpMemberSummaryCountDto
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/7/4 15:40
 */
@Setter
public class SlpMemberSummaryCountDto {

    private BigDecimal sumTotalAmount;
    private BigDecimal sumTotalSubAmount;
    private Integer sumPromotion;

    public BigDecimal getTotalSubValidAmount() {
        return orElse(sumTotalSubAmount, BigDecimal.ZERO).add(orElse(sumTotalAmount, BigDecimal.ZERO));
    }

    public Integer getPromotion() {
        return orElse(sumPromotion, 0);
    }

    private <T> T orElse(T value, T defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return value;
    }
}
