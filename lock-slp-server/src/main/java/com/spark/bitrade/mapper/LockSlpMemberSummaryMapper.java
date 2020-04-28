package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.dto.SlpMemberSummaryCountDto;
import com.spark.bitrade.dto.SlpMemberSummaryUpdateDto;
import com.spark.bitrade.entity.LockSlpMemberSummary;
import com.spark.bitrade.vo.LockSummationVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 会员社区奖励实时统计表(LockSlpMemberSummary)表数据库访问层
 *
 * @author yangch
 * @since 2019-06-18 21:27:03
 */
public interface LockSlpMemberSummaryMapper extends BaseMapper<LockSlpMemberSummary> {

    int updateBySummaryDto(SlpMemberSummaryUpdateDto dto);

    /**
     * 统计下级信息
     *
     * @param inviterId 邀请人ID
     * @return dto
     */
    SlpMemberSummaryCountDto countSubSummary(@Param("id") long inviterId, @Param("unit") String coinUnit);

    /**
     * 更新推荐人
     *
     * @param id        id
     * @param inviterId inviter id
     * @return int
     */
    @Update("update lock_slp_member_summary set inviter_id = #{inviterId} where id = #{id}")
    int updateInviterId(@Param("id") String id, @Param("inviterId") Long inviterId);

    /**
     * SLP加速释放页面，查询锁仓汇总
     *
     * @param inviterId 推荐人ID
     * @param startTime 查询起始时间
     * @param endTime   查询结束时间
     * @return SLP加速记录
     */
    LockSummationVo sumTotalLock(@Param("inviterId") Long inviterId, @Param("startTime") Long startTime, @Param("endTime") Long endTime);

    /**
     * 统计推荐关系表中直推用户数
     *
     * @param inviterId
     * @return long
     * @author zhangYanjun
     * @time 2019.07.22 18:07
     */
    @Select("select count(1) from slp_member_promotion where inviter_id = #{inviterId}")
    int countSlpMemberPromotion(@Param("inviterId") Long inviterId);

    /**
     * 获取当前会员或者伞下会员，锁仓总人数
     *
     * @param memberId 会员ID集合
     * @return 有效总人数
     */
    @Select("select count(1) from lock_slp_member_summary where inviter_id = #{memberId }")
    int countEffectiveTotal(Long memberId);

}