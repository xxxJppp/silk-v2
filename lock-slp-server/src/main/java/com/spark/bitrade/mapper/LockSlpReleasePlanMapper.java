package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.entity.LockSlpReleasePlan;
import com.spark.bitrade.vo.LockSlpMemberRecordDetailVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 本金返还计划表(LockSlpReleasePlan)表数据库访问层
 *
 * @author yangch
 * @since 2019-06-18 21:27:03
 */
public interface LockSlpReleasePlanMapper extends BaseMapper<LockSlpReleasePlan> {

    BigDecimal findTotalToBoReleasedByMemberId(Long memberId);

    BigDecimal findNormalReleasedAmount(Long memberId);

    BigDecimal findFastResleasedAmount(Long memberId);

    List<LockSlpMemberRecordDetailVo> findMemberRecordDetails(@Param("memberId") Long memberId,IPage page);
}