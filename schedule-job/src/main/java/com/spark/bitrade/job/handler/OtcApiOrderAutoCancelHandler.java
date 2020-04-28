package com.spark.bitrade.job.handler;

import com.spark.bitrade.service.IOtcApiFeignService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.10.02 09:55  
 */
@JobHandler(value = "otcApiOrderAutoCancelHandler")
@Component
public class OtcApiOrderAutoCancelHandler extends IJobHandler {

    @Autowired
    private IOtcApiFeignService otcApiFeignService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        otcApiFeignService.autoCancelOrder();
        return ReturnT.SUCCESS;
    }
}
























