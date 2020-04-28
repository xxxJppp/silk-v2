package com.spark.bitrade.entity.dto;

import com.spark.bitrade.entity.SilkPayAccount;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author shenzucai
 * @time 2019.07.27 10:05
 */
@Data
public class SilkPayAccountDto extends SilkPayAccount {
    private BigDecimal distance;
}
