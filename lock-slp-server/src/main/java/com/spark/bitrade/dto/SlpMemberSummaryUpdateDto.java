package com.spark.bitrade.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * SlpMemberSummaryUpdateDto
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/7/3 20:21
 */
@Data
@AllArgsConstructor
public class SlpMemberSummaryUpdateDto {

    private String id;
    private BigDecimal amount;
    private BigDecimal realAmount;
    private BigDecimal rate;
    private BigDecimal remain;
    private Date time;
}
