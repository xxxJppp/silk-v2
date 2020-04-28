package com.spark.bitrade.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.MemberBenefitsOrder;
import com.spark.bitrade.entity.MemberRecommendCommision;
import com.spark.bitrade.param.ExchangeOrderReceipt;
import com.spark.bitrade.param.MemberBenefitsOrderReceipt;
import com.spark.bitrade.param.PageParam;
import com.spark.bitrade.vo.RecommendCommisionVo;


public interface MemberRecommendCommisionService extends IService<MemberRecommendCommision> {
	
	public void distributeExchageOrderFee(ExchangeOrderReceipt orderReceipt);
	
	public void distributeBenefitsOrder(MemberBenefitsOrderReceipt orderReceipt,MemberBenefitsOrder order);

	IPage<MemberRecommendCommision> getRecommendCommisionLists(Long memberId, PageParam param, int bizType);

	IPage<MemberRecommendCommision> getRecommendCommisionList(Long memberId, PageParam param, int bizType);
	
	public List<MemberRecommendCommision> getMemberRecommendCommisionByStatus(int status);
	
	public boolean updateDistributeStatus(List<MemberRecommendCommision> successDistributeList);

	List<MemberRecommendCommision> countMemberRecommendCommision(Long meberId);

	IPage<MemberRecommendCommision> getRecommendCommisionBySend(Long memberId, PageParam param);


	List<RecommendCommisionVo> findRecommendCommisionListMapper(Page<RecommendCommisionVo> commisionPage, Long memberId,
																String startTime, String endTime);
	
	
	boolean updateDistributingStatus(List<Long> ids);

}
