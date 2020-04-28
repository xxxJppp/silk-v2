package com.spark.bitrade.service;

import com.spark.bitrade.constant.FeignServiceConstant;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * IExchangeReleaseAwardTaskService
 *
 * @author Archx[archx@foxmail.com]
 * @since 2020/1/19 14:52
 */
@FeignClient(FeignServiceConstant.SERVICE_EXCHANGE_V2_RELEASE)
public interface IExchangeReleaseAwardTaskService {

    /**
     * 获取待解锁记录
     *
     * @return
     */
    @RequestMapping("/exchange2-release/service/v2/award/task/pending")
    MessageRespResult<List<Long>> getExchangeReleaseTaskRecord();

    /**
     * 释放任务
     *
     * @param taskIds 需执行释放的记录
     * @return
     */
    @RequestMapping("/exchange2-release/service/v2/award/task/release")
    MessageRespResult<List<Object>> exchangeReleaseTask(@RequestParam("taskIds") List<Long> taskIds);

}
