package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.spark.bitrade.entity.NewYearDailyTask;
import com.spark.bitrade.mapper.NewYearDailyTaskMapper;
import com.spark.bitrade.service.NewYearDailyTaskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.vo.MemberDailyTaskVo;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户任务列表 用户进入首页新增初始化 服务实现类
 * </p>
 *
 * @author zhaopeng
 * @since 2020-1-7
 */
@Service
public class NewYearDailyTaskServiceImpl extends ServiceImpl<NewYearDailyTaskMapper, NewYearDailyTask> implements NewYearDailyTaskService {

}
