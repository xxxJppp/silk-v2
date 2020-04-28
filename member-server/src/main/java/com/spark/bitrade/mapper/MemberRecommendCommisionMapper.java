package com.spark.bitrade.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.entity.MemberRecommendCommision;
import com.spark.bitrade.vo.RecommendCommisionVo;


public interface MemberRecommendCommisionMapper extends BaseMapper<MemberRecommendCommision> {
	
	
	@Insert("<script> INSERT INTO member_recommend_commision "
	        + "(ref_id,deliver_to_member_id,order_member_id,invite_level,commision_unit,commision_quantity,platform_unit_cny_rate,distribute_status,accumulative_quantity,biz_type,mq_msg_id,commision_usdt_qty,platform_unit_rate) "
	        + "VALUES "
	        + "<foreach collection = 'list' item='mrc' separator=',' > "
	        + " (#{mrc.refId}, "
	        + " #{mrc.deliverToMemberId},"
	        + " #{mrc.orderMemberId},"
	        + " #{mrc.inviteLevel},"
	        + " #{mrc.commisionUnit}, "
	        + " #{mrc.commisionQuantity}, "
	        + " #{mrc.platformUnitCnyRate}, "
	        + " #{mrc.distributeStatus}, "
	        + " #{mrc.accumulativeQuantity}, "
	        + " #{mrc.bizType}, "
	        + " #{mrc.mqMsgId}, "
	        + " #{mrc.commisionUsdtQty}, "
	        + " #{mrc.platformUnitRate}) "
	        + "</foreach> "
	        + "</script>")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	public void batchInsert(@Param("list") List<MemberRecommendCommision> list);
	
	
	@SelectProvider(type=MemberRecommendCommisionProvider.class, method="query")
	public BigDecimal getMemberAccumulativeQuantity( @Param("memberId") long memberId, @Param("distributingCommisionIdList" ) String distributingCommisionIdList);

	@Select("SELECT * FROM member_recommend_commision WHERE  distribute_status = #{status} ORDER BY deliver_to_member_id DESC")
	public List<MemberRecommendCommision> getMemberRecommendCommisionByStatus(int status);
	
	@Update("update member_recommend_commision  set distribute_status = 20, transfer_id = #{rc.transferId}, distribute_time = now() where id = #{rc.id}")
	public boolean updateDistributeStatus(@Param("rc") MemberRecommendCommision rc);

	@Select("select sum(mrc.commision_quantity) as tempCount, mrc.biz_type, mrc.commision_unit from member_recommend_commision mrc where mrc.deliver_to_member_id = #{mid} and distribute_status = 20 GROUP BY biz_type, commision_unit")
	List<MemberRecommendCommision> countMemberRecommendCommisionByBizType(@Param("mid") Long memberId);


	List<RecommendCommisionVo> findMemberRecommendCommisionList(@Param("page") IPage page, @Param("memberId") Long memberId,
																@Param("startTime") String startTime, @Param("endTime") String endTime);
	
	
	@Update("update member_recommend_commision  set distribute_status = 11,update_time = now() where id in(#{ids}) ")
	public Integer updateDistributingStatus(String ids);


}
