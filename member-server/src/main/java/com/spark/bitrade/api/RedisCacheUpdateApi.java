package com.spark.bitrade.api;

import com.spark.bitrade.constant.FeignServiceConstant;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author: Zhong Jiang
 * @date: 2019-11-26 11:11
 */
@FeignClient(FeignServiceConstant.MEMBER_SERVER)
public interface RedisCacheUpdateApi {

    @GetMapping("/memberApi/api/v2/member/memberRequireCondition/updateCache")
    MessageRespResult<Boolean> updateRequireConditionCache();
}
