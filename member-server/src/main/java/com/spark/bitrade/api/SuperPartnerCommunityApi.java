package com.spark.bitrade.api;

import com.spark.bitrade.constant.FeignServiceConstant;
import com.spark.bitrade.constant.LockType;
import com.spark.bitrade.entity.LockCoinDetail;
import com.spark.bitrade.entity.SuperPartnerCommunity;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * @author: Zhong Jiang
 * @date: 2019-11-28 17:23
 */
@FeignClient(name = FeignServiceConstant.LOCK_SERVER2,path = "lock2")
public interface SuperPartnerCommunityApi {

    @PostMapping("/api/v2/superPartner/countCommunityNumber")
    MessageRespResult<SuperPartnerCommunity> findPartnerCommunityNumber(@RequestHeader("appId") Integer appId,
                                                                        @RequestHeader("apiKey") String apiKey);

}
