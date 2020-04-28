package com.spark.bitrade.mapper;

import com.spark.bitrade.entity.NewYearDailyTask;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.vo.MemberDailyTaskVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 用户任务列表 用户进入首页新增初始化 Mapper 接口
 * </p>
 *
 * @author qiliao
 * @since 2019-12-30
 */
public interface NewYearDailyTaskMapper extends BaseMapper<NewYearDailyTask> {
    @Select("SELECT sum(task_regist_status) as totalTaskRegist, " +
            "sum(task_login_status) as totalTaskLogin, " +
            "sum(task_exchange_status) as totalTaskExchange, " +
            "sum(task_recharge_status) as totalTaskRecharge, " +
            "sum(task_otc_status) as totalTaskOtc, " +
            "sum(task_put_status) as totalTaskPut " +
            "FROM new_year_daily_task where member_id = #{memberId} ")
    MemberDailyTaskVo countDailyTaskByMemberId(@Param("memberId") Long memberId);
}
