package com.spark.bitrade.job.handler;

import com.spark.bitrade.job.util.ArrayPage;
import com.spark.bitrade.service.IExchangeReleaseAwardTaskService;
import com.spark.bitrade.util.MessageRespResult;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ExchangeReleaseAwardTaskJobHandler
 *
 * @author Archx[archx@foxmail.com]
 * @since 2020/1/19 15:04
 */
@JobHandler(value = "ExchangeReleaseAwardTaskJobHandler")
@Component
public class ExchangeReleaseAwardTaskJobHandler extends IJobHandler {

    private IExchangeReleaseAwardTaskService releaseAwardTaskService;

    @Autowired
    public void setReleaseAwardTaskService(IExchangeReleaseAwardTaskService releaseAwardTaskService) {
        this.releaseAwardTaskService = releaseAwardTaskService;
    }

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("开始执行处理币币交易闪兑任务释放-释放任务");
        MessageRespResult<List<Long>> rl = releaseAwardTaskService.getExchangeReleaseTaskRecord();
        if (!rl.isSuccess()) {
            XxlJobLogger.log("================币币交易闪兑任务释放-释放任务执行失败=====================");
            return ReturnT.FAIL;
        }
        List<Long> data = rl.getData();
        new ArrayPage<>(data, 255).forEach(ids -> releaseAwardTaskService.exchangeReleaseTask(ids));

        XxlJobLogger.log("================币币交易闪兑任务释放-释放任务执行成功=====================");
//        return ReturnT.FAIL;
        return ReturnT.SUCCESS;
    }
}
