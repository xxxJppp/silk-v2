package com.spark.bitrade.service;

import com.spark.bitrade.constant.FeignServiceConstant;
import com.spark.bitrade.entity.DailyTask;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 年终活动调用Api
 *
 * @author: Zhong Jiang
 * @date: 2020-01-02 10:36
 */
@FeignClient(FeignServiceConstant.FESTIVAL_ACTIVITES)
public interface IFestivalActivitesService {

    @PostMapping(value = "/newYearApi/api/v2/task/update")
    MessageRespResult<Boolean> updateMemberDailyTask(DailyTask dailyTask);


    @RequestMapping(value ="/newYearApi/api/v2/myOre/timeFree",method = {RequestMethod.POST})
    MessageRespResult<Boolean> timeFree();
}
