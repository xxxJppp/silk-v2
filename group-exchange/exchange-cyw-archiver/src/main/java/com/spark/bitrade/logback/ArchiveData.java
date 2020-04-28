package com.spark.bitrade.logback;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 归档数据
 *
 * @author Pikachu
 * @since 2019/11/4 15:42
 */
@Data
@AllArgsConstructor
public class ArchiveData {

    private String type;
    private String clazz;
    private Object data;
}
