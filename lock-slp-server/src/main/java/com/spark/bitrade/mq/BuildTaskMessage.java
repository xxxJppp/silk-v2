package com.spark.bitrade.mq;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/***
  * 构建任务消息实体
 *
  * @author yangch
  * @time 2019-06-09 21:38:15
  */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BuildTaskMessage implements TaskMessage {

    /**
     * 消息类型：处理锁仓数据任务，生成参与者更新任务，生成直接推荐人的更新任务，生成直接推荐人加速释放任务
     */
    private BuildTaskMessageType type;

    /**
     * 业务ID
     */
    private String refId;


    @Override
    public String stringify() {
        return JSON.toJSONString(this);
    }
}
