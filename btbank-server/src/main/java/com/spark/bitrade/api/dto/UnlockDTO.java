package com.spark.bitrade.api.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author shenzucai
 * @time 2019.10.24 22:35
 */
@Data
public class UnlockDTO {
    private Long id;
    private Long memberId;
    private BigDecimal amount;
    private Date createTime;
    /**
     * 0,接单，派单
     * 1，固定
     */
    private Integer type;
}
