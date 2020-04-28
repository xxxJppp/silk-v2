package com.spark.bitrade.job.handler;

import com.spark.bitrade.service.ILuckTreasureApiService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@JobHandler(value = "luckyTreasureJobHandler")
@Component
public class LuckyTreasureJobHandler extends IJobHandler {

    @Autowired
    private ILuckTreasureApiService luckyTreasureApiService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {

        luckyTreasureApiService.luckyGameSchedule();

        return ReturnT.SUCCESS;
    }
}
