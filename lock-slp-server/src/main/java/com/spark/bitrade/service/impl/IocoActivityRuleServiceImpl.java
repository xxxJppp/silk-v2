package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.mapper.IocoActivityRuleMapper;
import com.spark.bitrade.entity.IocoActivityRule;
import com.spark.bitrade.service.IocoActivityRuleService;
import org.springframework.stereotype.Service;

/**
 * ioco对应活动规则(IocoActivityRule)表服务实现类
 *
 * @author daring5920
 * @since 2019-07-03 14:38:58
 */
@Service("iocoActivityRuleService")
public class IocoActivityRuleServiceImpl extends ServiceImpl<IocoActivityRuleMapper, IocoActivityRule> implements IocoActivityRuleService {

    /**
     * 根据总推荐人数获取当前活动的规则
     *
     * @param count
     * @param activityId
     * @return true
     * @author shenzucai
     * @time 2019.07.03 21:05
     */
    @Override
    public IocoActivityRule getActivityRuleByTotalCount(Integer count, Long activityId) {

        QueryWrapper<IocoActivityRule> activityRuleQueryWrapper = new QueryWrapper<IocoActivityRule>()
                .eq("activity_id",activityId)
                .le("gte_member_count",count)
                .gt("lt_member_count",count);

        return baseMapper.selectOne(activityRuleQueryWrapper);
    }
}