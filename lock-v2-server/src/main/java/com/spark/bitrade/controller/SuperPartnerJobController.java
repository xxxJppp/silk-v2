package com.spark.bitrade.controller;

import com.spark.bitrade.service.ISuperPartnerService;
import com.spark.bitrade.service.SuperAwardRecordService;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.07.18 10:47  
 */
@RestController
public class SuperPartnerJobController implements ISuperPartnerService {

    @Autowired
    private SuperAwardRecordService superAwardRecordService;


    @Override
    public MessageRespResult feeAward() {
        superAwardRecordService.runFeeCaluate();
        return MessageRespResult.success();
    }

    @Override
    public MessageRespResult memberActiveAward() {

        superAwardRecordService.runBBExchange();
        return MessageRespResult.success();
    }

    /**
     * 修复之前的手续费交易
     * @return
     */
    @Override
    public MessageRespResult revertFeeAward(@RequestParam("dateStr") String dateStr){

        superAwardRecordService.runFeeCaluateByDay(dateStr);
        return MessageRespResult.success();
    }


}






















