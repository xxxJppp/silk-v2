package com.spark.bitrade.service;

import com.spark.bitrade.constant.FeignServiceConstant;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.11.11 14:18  
 */
@FeignClient(FeignServiceConstant.ACCOUNT_SERVER)
public interface IMemberTransactionApiService {

    /**
     * 分页查询项目方提币充值统计
     *
     * @param size 分页.每页数量
     * @param current 分页.当前页码
     * @return 所有数据
     */
    @RequestMapping(value = "/acct/api/v2/memberTransaction/memberTransctionPage", method = { RequestMethod.POST})
    MessageRespResult list(@RequestParam(value = "type") TransactionType type,
                           @RequestParam(value = "current",defaultValue = "1")Integer current,
                           @RequestParam(value = "size",defaultValue = "10")Integer size,
                           @RequestParam(value = "startTime")Date startTime,
                           @RequestParam(value = "endTime")Date endTime,
                           @RequestParam(value = "coin")String coin);


}
