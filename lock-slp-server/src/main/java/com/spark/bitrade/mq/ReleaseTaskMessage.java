package com.spark.bitrade.mq;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;

/***
  * 加速释放任务消息实体
 *
  * @author yangch
  * @time 2019-06-09 21:38:15
  */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReleaseTaskMessage implements TaskMessage {
    /**
     * 消息类型:生成直接推荐人加速释放任务,推荐人社区奖励加速释放任务,推荐人分享收益加速释放任务
     */
    private ReleaseTaskMessageType type;

    /**
     * 业务ID
     */
    private String refId;

    /**
     * 无环推荐链记录
     */
    private ArrayList<Long> acyclicRecommendChain = new ArrayList<>();

    @Override
    public String stringify() {
        return JSON.toJSONString(this);
    }
}
