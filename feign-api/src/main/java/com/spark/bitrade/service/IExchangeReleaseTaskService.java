package com.spark.bitrade.service;

import com.spark.bitrade.constant.FeignServiceConstant;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 *    
 *  @author lc  
 *  @time 2019.12.17  
 */
//@FeignClient(name = FeignServiceConstant.SERVICE_EXCHANGE_V2_RELEASE,path = "/exchange2-release/service/v2/task")
@FeignClient(FeignServiceConstant.SERVICE_EXCHANGE_V2_RELEASE)
public interface IExchangeReleaseTaskService {

    /**
     * 获取待解锁记录
     * @return
     */
   // @PostMapping("/getExchangeReleaseTaskRecord")
    @RequestMapping("/exchange2-release/service/v2/task/getExchangeReleaseTaskRecord")
    MessageRespResult<List<Object>> getExchangeReleaseTaskRecord();

    /**
     * 交易释放规则任务
     * @param exchangeReleaseRecord 需执行释放的记录
     * @return
     */
    @RequestMapping("/exchange2-release/service/v2/task/exchangeReleaseTask")
    MessageRespResult<List<Object>> exchangeReleaseTask(@RequestBody List<Object> exchangeReleaseRecord);


}




























