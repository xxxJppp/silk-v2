package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.IocoActivityRule;

import java.math.BigDecimal;

/**
 * ioco对应活动规则(IocoActivityRule)表服务接口
 *
 * @author daring5920
 * @since 2019-07-03 14:38:58
 */
public interface IocoActivityRuleService extends IService<IocoActivityRule> {

    /**
     * 根据总推荐人数获取当前活动的规则
     * @author shenzucai
     * @time 2019.07.03 21:05
     * @param count
     * @return true
     */
    IocoActivityRule getActivityRuleByTotalCount(Integer count,Long activityId);

}