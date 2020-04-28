package com.spark.bitrade.vo;

import lombok.Builder;
import lombok.Data;

/**
 * JobReceipt
 *
 * @author Archx[archx@foxmail.com]
 * at 2019-07-08 21:09
 */
@Data
@Builder
public class JobReceipt {

    private String  recordId;
    private Boolean success;
    private String  message;

}
