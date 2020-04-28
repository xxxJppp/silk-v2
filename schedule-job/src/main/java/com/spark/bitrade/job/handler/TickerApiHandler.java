package com.spark.bitrade.job.handler;

import com.spark.bitrade.service.ITickerApiService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *  @author lc
 *  @time 2020.01.15
 */
@JobHandler(value = "tickerApiHandler")
@Component
public class TickerApiHandler extends IJobHandler {

    @Autowired
    private ITickerApiService tickerApiService;

    @Override
    public ReturnT<String> execute(String dateStr) throws Exception {
        XxlJobLogger.log("================非小号API获取行情=====================");
        tickerApiService.allticker();
        return ReturnT.SUCCESS;
    }



}













