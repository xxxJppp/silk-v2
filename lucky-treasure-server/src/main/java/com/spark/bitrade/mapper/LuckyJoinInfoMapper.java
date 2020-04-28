package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.controller.vo.LuckyRunBullListVo;
import com.spark.bitrade.entity.LuckyJoinInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 幸运宝参与明细 Mapper 接口
 * </p>
 *
 * @author qiliao
 * @since 2019-12-16
 */
public interface LuckyJoinInfoMapper extends BaseMapper<LuckyJoinInfo> {

	@Select("SELECT * FROM lucky_join_info WHERE num_id =#{gameId} AND member_id = #{memberId}")
	List<LuckyJoinInfo> selectInfoByMemberAndGameId(@Param("gameId")Long gameId , @Param("memberId")Long memberId);

	/**
	 * 查询用户参加赛牛统计
	 * @param memberId
	 * @param actId
	 * @return
	 */
	@Select("SELECT COUNT(1) as ticketNums,join_info as coinUnit FROM lucky_join_info WHERE member_id = #{memberId} and num_id=#{actId} GROUP BY join_info")
	List<LuckyRunBullListVo.MyJoinBulls> findMyJoinBulls(@Param("memberId") Long memberId, @Param("actId") Long actId);

	/**
	 * 查询我的小牛快跑中奖信息
	 * @param memberId
	 * @param actId
	 * @return
	 */
	@Select("SELECT " +
			" SUM(award_amount) AS memberLuckyAmount, " +
			" COUNT(1) AS memberLuckyCount, " +
			" SUM(add_award_amount) AS memberAddLuckyAmount, " +
			" append_wx AS isShare " +
			"FROM " +
			" lucky_join_info " +
			"WHERE " +
			" win = 1 AND member_id = #{memberId} and num_id=#{actId}  ")
    LuckyRunBullListVo findMyBullLucky(@Param("memberId") Long memberId, @Param("actId") Long actId);

	/**
	 * 用户参加的小牛快跑注数
	 * @param memberId
	 * @param actId
	 * @return
	 */
	@Select("SELECT COUNT(1) FROM lucky_join_info where member_id=#{memberId} and num_id=#{actId}")
	Optional<Integer> findMyJoinBullCount(@Param("memberId") Long memberId, @Param("actId") Long actId);

	/**
	 * 根据币种 活动ID 查询列表
	 * @param actId
	 * @param coinUnit
	 * @return
	 */
	@Select("select * from lucky_join_info where num_id=#{actId} and join_info=#{coinUnit}")
	List<LuckyJoinInfo> findLuckyMemberByActIdAndCoin(@Param("actId") Long actId, @Param("coinUnit") String coinUnit);
	/**
	 * 根据币种 活动ID 查询列表
	 * @param actId
	 * @param
	 * @return
	 */
	@Select("select * from lucky_join_info where num_id=#{actId} ")
	List<LuckyJoinInfo> findLuckyMemberByActId(@Param("actId") Long actId);
	/**
	 * 根据币种 活动ID 查询列表
	 * @param actId
	 * @param coinUnit
	 * @return
	 */
	@Select("select count(1) from lucky_join_info where num_id=#{actId} and join_info=#{coinUnit}")
	Integer countLuckyMemberByActIdAndCoin(@Param("actId") Long actId, @Param("coinUnit") String coinUnit);

	/**
	 * 查询小牛下注 种类个数
	 * @param actId
	 * @return
	 */
	@Select("SELECT COUNT(1) FROM (SELECT COUNT(1) FROM lucky_join_info WHERE num_id=#{actId} GROUP BY join_info) a")
	Integer countCoinByActId(@Param("actId") Long actId);

	/**
	 *
	 * @param actId
	 * @param memberId
	 * @return
	 */
	@Update("update lucky_join_info set append_wx=1 where member_id=#{memberId} and num_id=#{actId} and win=1")
	int updateBullShareStatus(@Param("actId") Long actId, @Param("memberId") Long memberId);

	@Select("SELECT l.create_time FROM lucky_join_info l WHERE l.join_info=#{coinUnit} AND l.num_id=#{actId} ORDER BY create_time ASC LIMIT 1")
	Date findJoinTimeByBull(@Param("actId") Long actId, @Param("coinUnit") String coinUnit);

	@Select("select member_id from lucky_join_info where num_id=#{actId} group by member_id")
	List<Long> findJonMemberIds(@Param("actId") Long actId);
}
