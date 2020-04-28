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
 *  @time 2019.08.06 18:36  
 */
@JobHandler(value = "superPartnerRepairHandler")
@Component
public class SuperPartnerRepairHandler extends IJobHandler {


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
    public ReturnT<String> execute(String dateStr) throws Exception {

        XxlJobLogger.log("================合伙人手续费奖励计算开始=====================");
        MessageRespResult feeAward = superPartnerService.revertFeeAward(dateStr);
        if (feeAward.isSuccess()) {
            XxlJobLogger.log("================合伙人手续费奖励计算成功=====================");
            return ReturnT.SUCCESS;
        }
        XxlJobLogger.log("================合伙人手续费奖励计算失败=====================");
        return ReturnT.FAIL;
    }
}













