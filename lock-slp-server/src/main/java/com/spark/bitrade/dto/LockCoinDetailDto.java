package com.spark.bitrade.dto;

import com.spark.bitrade.entity.LockCoinDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * LockCoinDetailDto
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/7/25 13:49
 */
@AllArgsConstructor
@Getter
public class LockCoinDetailDto {

    /**
     * 前一个锁仓记录
     */
    private LockCoinDetail prev;

    /**
     * 追加锁仓记录
     */
    private LockCoinDetail append;
}
