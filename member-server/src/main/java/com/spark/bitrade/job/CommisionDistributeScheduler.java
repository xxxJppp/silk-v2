package com.spark.bitrade.job;

import com.spark.bitrade.constant.DistributeTypeEnum;
import com.spark.bitrade.entity.MemberRecommendCommision;
import com.spark.bitrade.service.CommisionService;
import com.spark.bitrade.service.MemberRecommendCommisionService;
import com.spark.bitrade.utils.MixUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.spark.bitrade.service.impl.MemberBenefitsExtendsServiceImpl;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CommisionDistributeScheduler {

	@Autowired
	private MemberRecommendCommisionService memberRecommendCommisionService;

	@Autowired
	private CommisionService commisionService;

	@Autowired
	private MemberBenefitsExtendsServiceImpl extendsService;

	@Scheduled(cron = "0 0 0 */1 * ?")
	public void schedualCommisionTransfer() {

		log.info(MixUtil.getCurrentDate() + " start to transfer commision fee");

		List<MemberRecommendCommision> rcList = this.memberRecommendCommisionService.getMemberRecommendCommisionByStatus(10);

		List<MemberRecommendCommision> distributeResult = this.commisionService.transferCommision(rcList);

		List<MemberRecommendCommision> successDistributeList  = distributeResult.stream().filter(rc -> rc.getDistributeStatus() == DistributeTypeEnum.DISTRIBUTED.getCode()).collect(Collectors.toList());

		this.memberRecommendCommisionService.updateDistributeStatus(successDistributeList);
	}


//	@Scheduled(cron = "0 0 0/1 * * ?")
	@Scheduled(cron = "0 0/1 * * * ?")
	public void schedualCheckMemberVipEndTime() {
		log.info("=============== 检查会员到期定时任务 ===========");
		extendsService.updatetMemberrBenefits();
	}
}
