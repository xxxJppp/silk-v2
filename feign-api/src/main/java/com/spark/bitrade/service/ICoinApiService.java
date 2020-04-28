package com.spark.bitrade.service;

import com.spark.bitrade.constant.FeignServiceConstant;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.11.12 15:21  
 */
@FeignClient(FeignServiceConstant.ACCOUNT_SERVER)
public interface ICoinApiService {

    /**
     * 通过unit获取coin_id
     *
     * @param unit 币种单位
     * @return 单条数据
     */
    @RequestMapping(value = "/acct/v2/coin/getCoinNameByUnit", method = {RequestMethod.GET})
    MessageRespResult<String> getCoinNameByUnit(@RequestParam("unit") String unit);
}
