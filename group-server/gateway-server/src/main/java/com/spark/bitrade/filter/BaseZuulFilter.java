package com.spark.bitrade.filter;

import com.alibaba.fastjson.JSONArray;
import com.netflix.zuul.ZuulFilter;
import com.spark.bitrade.util.MessageResult;

import java.text.MessageFormat;

/**
 *  
 *
 * @author young
 * @time 2019.11.12 16:56
 */
public abstract class BaseZuulFilter extends ZuulFilter {

    /**
     * 内容格式，根据返回的data数据进行消息的格式
     *
     * @param result
     * @param msg
     * @return
     */
    protected String getMessageFormat(MessageResult result, String msg) {
        if (result.getData() != null) {
            if (result.getData() instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) result.getData();
                msg = MessageFormat.format(msg, jsonArray.toArray());
            } else {
                msg = MessageFormat.format(msg, result.getData());
            }
        }
        return msg;
    }
}
