package com.spark.bitrade.job.handler;

import com.spark.bitrade.service.IFestivalActivitesService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@JobHandler(value = "newYearJobHandler")
@Component
public class NewYearJobHandler extends IJobHandler {

    @Autowired
    private IFestivalActivitesService festivalActivitesService;


    @Override
    public ReturnT<String> execute(String s) throws Exception {
        festivalActivitesService.timeFree();
        return ReturnT.SUCCESS;
    }
}

