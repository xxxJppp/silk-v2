package com.spark.bitrade.job.handler;

import com.spark.bitrade.service.ISuperPartnerService;
import com.spark.bitrade.util.MessageRespResult;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.07.19 15:35  
 */
@JobHandler(value = "superPartnerActiveJobHandler")
@Component
public class SuperPartnerActiveJobHandler extends IJobHandler {

    @Autowired
    private ISuperPartnerService superPartnerService;
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        XxlJobLogger.log("================成员BB交易计算开始=====================");
        MessageRespResult feeAward = superPartnerService.memberActiveAward();
        if (feeAward.isSuccess()){
            XxlJobLogger.log("================成员BB交易计算成功=====================");
            return ReturnT.SUCCESS;
        }
        XxlJobLogger.log("================成员BB交易计算失败=====================");
        return ReturnT.FAIL;
    }
}
