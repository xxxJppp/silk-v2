package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.MemberVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (Member)表数据库访问层
 *
 * @author archx
 * @since 2019-06-11 17:28:17
 */
public interface MemberMapper extends BaseMapper<Member> {

    List<Long> getInvitationRecord(@Param("memberId") Long memberId);


    List<Long> getInvitationRecordIdInInviterId(List<Long> idList);

    List<MemberVo> getInvitationRecordPage(Page page, @Param("idList") List<Long> idList);
}