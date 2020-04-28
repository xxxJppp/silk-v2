package com.spark.bitrade.service;

import com.spark.bitrade.constant.FeignServiceConstant;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.07.18 10:48  
 */
@FeignClient(name = FeignServiceConstant.LOCK_SERVER2,path = "lock2")
public interface ISuperPartnerService {

    /**
     * 社区成员在币币交易的手续费,管理人获得20%的奖励,奖励可以与推荐返佣叠加
     * @return
     */
    @PostMapping("superPartner/feeAward")
    MessageRespResult feeAward();

    /**
     * .社群成员每产生一个活跃用户（单月币币交易额达到60万CNY，仅限该用户在社区中首次达成）， 奖励社区负责人500个SLU
     * @return
     */
    @PostMapping("superPartner/memberActiveAward")
    MessageRespResult memberActiveAward();


    /**
     * .传时间参数手续费统计
     * @Param
     * @return
     */
    @PostMapping("superPartner/revertFeeAward")
    MessageRespResult revertFeeAward(@RequestParam("dateStr") String dateStr);
}




























