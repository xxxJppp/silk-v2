package com.spark.bitrade.api.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Administrator
 * @time 2019.10.25 20:09
 */
@Data
public class MinerOrdersVO<T> {
    List<T> content;
    Long totalElements;
}
