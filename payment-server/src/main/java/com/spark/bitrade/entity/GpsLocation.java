package com.spark.bitrade.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author shenzucai
 * @time 2019.07.27 09:30
 */
@Data
public class GpsLocation {
    private BigDecimal longitude;
    private BigDecimal latitude;

    @Override
    public String toString() {
        return longitude.toPlainString() +","+latitude.toPlainString();
    }
}
