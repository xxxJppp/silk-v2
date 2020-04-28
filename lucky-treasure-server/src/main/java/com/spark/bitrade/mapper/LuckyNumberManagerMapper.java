package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.controller.param.ListBullParam;
import com.spark.bitrade.controller.vo.LuckyRunBullListVo;
import com.spark.bitrade.entity.LuckyNumberManager;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 欢乐幸运号活动信息表 Mapper 接口
 * </p>
 *
 * @author qiliao
 * @since 2019-12-16
 */
public interface LuckyNumberManagerMapper extends BaseMapper<LuckyNumberManager> {

	@Select("SELECT  * FROM lucky_number_manager WHERE id = #{id} AND act_type = #{actType}")
	LuckyNumberManager selectNumberManagerByIdAndType(@Param("id")Long id ,@Param("actType") Integer actType );

	List<LuckyRunBullListVo> listBulls(@Param("param") ListBullParam param, @Param("page") IPage page,@Param("memberActIds") List<Long> memberActIds);

	LuckyRunBullListVo detailBull(@Param("actId") Long actId);

	@Select("SELECT o.num_id FROM lucky_join_info o WHERE o.member_id=#{memberId} GROUP BY o.num_id")
	List<Long> findMemberActIds(@Param("memberId") Long memberId);

	@Select("SELECT " +
			" m.* " +
			"FROM " +
			" lucky_number_manager m " +
			"LEFT JOIN lucky_join_info j ON j.num_id = m.id " +
			"WHERE " +
			" is_settlement = 1 AND j.member_id=#{memberId} " +
			"ORDER BY " +
			" lucky_time DESC " +
			"LIMIT 1")
	LuckyNumberManager findLastSettleLucky(@Param("memberId") Long memberId);
}
