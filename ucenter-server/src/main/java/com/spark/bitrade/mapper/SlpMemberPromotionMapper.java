package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.entity.PromotionMemberDTO;
import com.spark.bitrade.entity.SlpMemberPromotion;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 会员推荐关系表(SlpMemberPromotion)表数据库访问层
 *
 * @author wsy
 * @since 2019-06-20 10:02:09
 */
public interface SlpMemberPromotionMapper extends BaseMapper<SlpMemberPromotion> {

    @Select("select updataTotal(#{memberId}) from dual")
    Integer updateTotal(@Param("memberId") Long memberId);

    @Select("select queryRecipt(#{memberId},#{cMemberId}) from dual")
    int queryRecipt(@Param("memberId") Long memberId,@Param("cMemberId") Long cMemberId);

//    @Select("SELECT m.id AS memberId, m.username, s.create_time AS createTime, level FROM ( " +
//            "SELECT 0 AS level, member_id, create_time FROM slp_member_promotion WHERE inviter_id = #{memberId} " +
//            "UNION ALL " +
//            "SELECT 1 AS level, member_id, create_time FROM slp_member_promotion WHERE inviter_id IN(SELECT member_id FROM slp_member_promotion WHERE inviter_id = #{memberId})" +
//            "UNION ALL " +
//            "SELECT 2 AS level, member_id, create_time FROM slp_member_promotion WHERE inviter_id IN(SELECT member_id FROM slp_member_promotion WHERE inviter_id IN(SELECT member_id FROM slp_member_promotion WHERE inviter_id = #{memberId})) " +
//            ") s LEFT JOIN member m ON m.id = s.member_id ORDER BY s.create_time DESC")
    @Select("SELECT m.id AS memberId, m.username, s.create_time AS createTime, 0 AS level FROM slp_member_promotion s LEFT JOIN member m ON m.id = s.member_id WHERE s.inviter_id = #{memberId} ORDER BY s.create_time DESC")
    List<PromotionMemberDTO> selectInviterList(IPage<PromotionMemberDTO> page, @Param("memberId") Long memberId);
}