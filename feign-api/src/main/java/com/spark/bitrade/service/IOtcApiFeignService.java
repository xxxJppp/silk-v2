package com.spark.bitrade.service;

import com.spark.bitrade.constant.FeignServiceConstant;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.10.02 10:03  
 */
@FeignClient(value = FeignServiceConstant.C2C_API_SERVER,path = "/otc-open/api/no-auth")
public interface IOtcApiFeignService {

    @PostMapping(value = "autoCancelOrder",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    MessageRespResult autoCancelOrder();

}
