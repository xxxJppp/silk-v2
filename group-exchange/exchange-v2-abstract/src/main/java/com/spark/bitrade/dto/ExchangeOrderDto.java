package com.spark.bitrade.dto;

import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.ExchangeOrderDetail;
import lombok.Data;

import java.util.List;

/**
 *  
 *
 * @author young
 * @time 2019.10.25 14:17
 */
@Data
public class ExchangeOrderDto extends ExchangeOrder {
    private List<ExchangeOrderDetail> detail;
}
