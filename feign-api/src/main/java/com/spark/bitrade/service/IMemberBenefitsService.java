package com.spark.bitrade.service;

import com.spark.bitrade.entity.Member;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.spark.bitrade.constant.FeignServiceConstant;
import com.spark.bitrade.util.MessageResult;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(FeignServiceConstant.MEMBER_SERVER)
public interface IMemberBenefitsService {


    @PostMapping("/memberApi/v2/member/benefits/unlock")
    MessageResult memberBenefitsUnlock();
    
    
    @PostMapping("/memberApi/v2/member/benefits/distribute")
    MessageResult distributeMemberBenefits();


    @GetMapping("/memberApi/api/v2/member/memberBenefitsOrder/give")
    MessageRespResult<Integer> giveMemberVip1(@RequestParam("memberId") Long memberId,  @RequestParam("appId") Integer appId);

    @PostMapping("/memberApi/api/v2/member/memberLevel/isMemberLevel_5")
    MessageRespResult<Boolean> getMemberLevelByMember(@RequestParam("memberId") Long memberId);
}
