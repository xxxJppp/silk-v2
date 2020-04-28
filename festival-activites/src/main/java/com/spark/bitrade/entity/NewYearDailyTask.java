package com.spark.bitrade.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class NewYearDailyTask implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long memberId;
    
    /**
     * 已完成任务键
     */
    private String taskKey;

    /**
     * 创建时间 yyyyMMdd
     */
    private String taskDateStr;
    
    /**
     * MQ消息id
     */
    private String messageId;
    
    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 更新时间
     */
    private Date createTime;
}
