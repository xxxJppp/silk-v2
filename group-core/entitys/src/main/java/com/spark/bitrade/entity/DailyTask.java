package com.spark.bitrade.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 用户任务列表 用户进入首页新增初始化
 * </p>
 *
 * @author qiliao
 * @since 2019-12-30
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "年终活动 - 每日任务")
public class DailyTask implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 用户id
     */
    private Long memberId;

    /**
     * 任务类型
     * { 1:推荐好友注册, 2:每日首次登录, 3:每日首次币币交易, 4:每日首次充币,
     *   5:每日首次法币交易买入成交, 6:每日挂1次币币交易买单超过10分钟 }
     */
    private Integer dailyTaskType;

    /**
     * 完成时间
     */
    private Date completeTime;

}
