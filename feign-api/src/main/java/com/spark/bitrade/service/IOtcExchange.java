package com.spark.bitrade.service;

import com.spark.bitrade.constant.FeignServiceConstant;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 适配btbank-otc-api
 * @author shenzucai
 * @time 2019.10.02 8:44
 */
@FeignClient(FeignServiceConstant.OTC_SERVER)
public interface IOtcExchange {

    /**
     * 调用v1进行放行
     * @author shenzucai
     * @time 2019.10.02 8:47
     * @param orderSn
     * @param memberId
     * @return true
     */
    @PostMapping("/otc/order/releaseBOAForApi")
    MessageRespResult<Object> releaseBOAForApi(@RequestParam("orderSn") String orderSn, @RequestParam("memberId") Long memberId);

}
