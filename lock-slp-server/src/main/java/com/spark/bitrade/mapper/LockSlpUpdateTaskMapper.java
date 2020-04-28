package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.entity.LockSlpUpdateTask;
import com.spark.bitrade.vo.LockRecordsVo;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 更新推荐人实时数据任务表(LockSlpUpdateTask)表数据库访问层
 *
 * @author yangch
 * @since 2019-06-18 21:27:03
 */
public interface LockSlpUpdateTaskMapper extends BaseMapper<LockSlpUpdateTask> {
    /**
     * 获取锁仓记录
     *
     * @param memberId  会员ID
     * @param startTime 获取起始时间,时间戳
     * @param endTime   获取结束时间,时间戳
     * @return 锁仓记录
     * @author zhongxj
     */
    List<LockRecordsVo> listLockRecords(IPage<LockRecordsVo> page, @Param("memberId") Long memberId, @Param("startTime") Long startTime, @Param("endTime") Long endTime);

}