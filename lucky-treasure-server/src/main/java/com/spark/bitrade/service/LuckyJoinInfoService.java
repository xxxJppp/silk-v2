package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.controller.param.ListLuckyNumberParam;
import com.spark.bitrade.controller.vo.LuckyRunBullListVo;
import com.spark.bitrade.entity.LuckyJoinInfo;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.util.MessageRespResult;

import java.util.List;
import java.util.Map;
/**
 * <p>
 * 幸运宝参与明细 服务类
 * </p>
 *
 * @author qiliao
 * @since 2019-12-16
 */
public interface LuckyJoinInfoService extends IService<LuckyJoinInfo> {

	public Map<String, Object> activityRecord(Long member);

	MessageRespResult<List<String>> joinLuckyNumberGame( Member member ,int payCount ,Long gameId , String coinUnit);

	MessageRespResult<String> appendWx(Member member ,ListLuckyNumberParam param);

	/**
	 * 查询我参加的小牛快跑 的票数和 币种
	 * @param memberId
	 * @param actId
	 * @return
	 */
	List<LuckyRunBullListVo.MyJoinBulls> findMyJoinBulls(Long memberId, Long actId);

	/**
	 * 查询我的小牛快跑中奖信息
	 * @param memberId
	 * @param actId
	 * @return
	 */
	LuckyRunBullListVo findMyBullLucky(Long memberId,Long actId);
	/**
	 * 用户参加的小牛快跑注数
	 * @param memberId
	 * @param actId
	 * @return
	 */
    Integer findMyJoinBullCount(Long memberId, Long actId);
	/**
	 * 更新分享状态
	 */
	int updateBullShareStatus(Long actId,Long memberId);

	/**
	 * 读消息
	 * @param memberId
	 * @param actId
	 * @param type 1详情读取  2分享
	 */
	void readAck(Long memberId,String actId,Integer type);

	/**
	 * 活动改变推送消息
	 * @param memberId
	 * @param actId
	 * @param isLucky 是否中奖
	 */
	void actChangeSend(String memberId, String actId,boolean isLucky);
}















