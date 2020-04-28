package com.spark.bitrade.service;

import com.spark.bitrade.constant.FeignServiceConstant;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 调用v1接口,行情获取
 * @author lc
 */
@FeignClient(FeignServiceConstant.BITRADE_OPEN_API)
public interface ITickerApiService {

    /**
     * 调用v1进行获取行情
     */
    @GetMapping("/api/v1/allticker")
    MessageRespResult<Object> allticker();

}
