package com.spark.bitrade.job.handler;

import com.spark.bitrade.service.IExchangeReleaseTaskService;
import com.spark.bitrade.util.MessageRespResult;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ExchangeReleleaseTaskJobHandler
 *
 * @author lc
 * @since 2019/12/17
 */
@JobHandler(value = "ExchangeReleaseTaskJobHandler")
@Component
public class ExchangeReleaseTaskJobHandler extends IJobHandler {

    @Autowired
    private IExchangeReleaseTaskService releaseTaskService;

    /**
     * 任务参数说明
     * @return state
     */
    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("开始执行处理币币交易释放-释放任务");
        MessageRespResult<List<Object>> rl = releaseTaskService.getExchangeReleaseTaskRecord();
        if (!rl.isSuccess()){
            XxlJobLogger.log("================币币交易释放-释放任务执行失败=====================");
            return ReturnT.FAIL;
        }
        releaseTaskService.exchangeReleaseTask(rl.getData());
        XxlJobLogger.log("================币币交易释放-释放任务执行成功=====================");
//        return ReturnT.FAIL;
        return ReturnT.SUCCESS;
    }
}
