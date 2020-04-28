package com.spark.bitrade.api.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 分页结果
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/10 14:43
 */
@Data
@Builder
public class PageResultVo<T> {

    private long total;
    private List<T> rows;
    private boolean hasNext;
    private int pageNo;
    private int pageSize;

}
