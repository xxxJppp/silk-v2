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
 *  超级合伙人定时任务  
 *  @author liaoqinghui  
 *  @time 2019.07.17 17:49  
 */
@JobHandler(value = "superPartnerJobHandler")
@Component
public class SuperPartnerJobHandler extends IJobHandler {



    @Autowired
    private ISuperPartnerService superPartnerService;

    /**
     * 批量计算合伙人收益 社区成员在币币交易的手续费,管理人获得20%的奖励,奖励可以与推荐返佣叠加
     *
     * @param s
     * @return
     * @throws Exception
     */
    @Override
    public ReturnT<String> execute(String s) throws Exception {

        XxlJobLogger.log("================合伙人手续费奖励计算开始=====================");
        MessageRespResult feeAward = superPartnerService.feeAward();
        if (feeAward.isSuccess()){
            XxlJobLogger.log("================合伙人手续费奖励计算成功=====================");
            return ReturnT.SUCCESS;
        }
        XxlJobLogger.log("================合伙人手续费奖励计算失败=====================");
        return ReturnT.FAIL;
    }


}


















