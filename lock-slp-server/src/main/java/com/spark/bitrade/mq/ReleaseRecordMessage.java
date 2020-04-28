package com.spark.bitrade.mq;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spark.bitrade.constant.SlpReleaseType;
import lombok.Data;

/**
 * ReleaseRecordMessage
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/7/16 10:25
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReleaseRecordMessage implements TaskMessage {

    /**
     * 记录ID
     */
    private String refId;

    /**
     * 释放类型
     */
    private SlpReleaseType type;

    @Override
    public String stringify() {
        return JSON.toJSONString(this);
    }
}
