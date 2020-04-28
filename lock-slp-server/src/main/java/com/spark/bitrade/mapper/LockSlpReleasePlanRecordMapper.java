package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.entity.LockSlpReleasePlanRecord;
import com.spark.bitrade.vo.LockSlpPlanRecordsVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 本金返还记录表(LockSlpReleasePlanRecord)表数据库访问层
 *
 * @author yangch
 * @since 2019-06-18 21:27:03
 */
public interface LockSlpReleasePlanRecordMapper extends BaseMapper<LockSlpReleasePlanRecord> {
    /**
     * 获取静态释放记录
     *
     * @param page     分页信息
     * @param memberId 会员ID
     * @return com.spark.bitrade.util.MessageRespResult<com.baomidou.mybatisplus.core.metadata.IPage < com.spark.bitrade.vo.LockSlpPlanRecordsVo>>
     * @author zhangYanjun
     * @time 2019.08.08 18:03
     */
    @Select("select r.release_time,r.release_in_amount,p.plan_name from lock_slp_release_plan_record r left join lock_slp_release_plan p on r.ref_plan_id = p.id where r.member_id = #{memberId} and r.status = 1 and r.release_task_status = 1 and r.release_in_amount > 0 order by r.release_time desc")
    List<LockSlpPlanRecordsVo> findMyReleaseRecord(IPage<LockSlpPlanRecordsVo> page, @Param("memberId") Long memberId);
}