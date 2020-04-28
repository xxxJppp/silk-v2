package com.spark.bitrade.service;

import com.spark.bitrade.constant.FeignServiceConstant;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(FeignServiceConstant.LUCKY_TREASURE_SERVER)
public interface ILuckTreasureApiService {

    @GetMapping(value = "luckyTreasureApi/api/v2/luckyCommon/luckyGameSchedule")
    MessageRespResult<String> luckyGameSchedule();
}
