package com.spark.bitrade.mq;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;

/***
  * 更新任务消息实体
 *
  * @author yangch
  * @time 2019-06-09 21:38:15
  */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateTaskMessage implements TaskMessage {
    /**
     * 消息类型：生成参与者更新任务，生成直接推荐人的更新任务，处理更新任务
     */
    private UpdateTaskMessageType type;

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
