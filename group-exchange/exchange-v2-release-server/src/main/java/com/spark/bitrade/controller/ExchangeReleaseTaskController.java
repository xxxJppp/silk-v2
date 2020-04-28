package com.spark.bitrade.controller;

import com.spark.bitrade.constant.ExchangeReleaseMsgCode;
import com.spark.bitrade.entity.ExchangeReleaseTask;
import com.spark.bitrade.service.ExchangeReleaseTaskService;
import com.spark.bitrade.util.MessageRespResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * 币币交易释放任务(内部服务)
 *
 * @author lc
 * @since 2019/12/17
 */
@RestController
@RequestMapping("/service/v2/task")
@Api(description = "币币交易释放任务服务控制层")
public class ExchangeReleaseTaskController extends ApiController {

    @Autowired
    ExchangeReleaseTaskService releaseTaskService;

    /**
     * 获取锁仓待释放记录
     */
    @RequestMapping(value = "/getExchangeReleaseTaskRecord", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<List<ExchangeReleaseTask>> getExchangeReleaseTaskRecord() {
        return releaseTaskService.taskReleaseRecord();
    }


    /**
     * 执行锁仓释放
     */
    @RequestMapping(value = "/exchangeReleaseTask", method = {RequestMethod.POST})
    public void exchangeReleaseTask(@RequestBody List<Object> exchangeReleaseRecord) {
        releaseTaskService.releaseTasks(exchangeReleaseRecord);
    }

    /**
     * 释放任务接口
     *
     * @param taskId 任务ID
     * @return
     */
    @ApiOperation(value = "释放任务接口", notes = "释放任务接口")
    @ApiImplicitParam(value = "任务ID", name = "taskId", dataType = "int", required = true)
    @RequestMapping(value = "/releaseTask", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<Boolean> releaseTask(Long taskId) {
        ExchangeReleaseTask releaseTask = releaseTaskService.getById(taskId);
        if (Objects.nonNull(releaseTask)) {
            return this.success(releaseTaskService.releaseTask(releaseTask));
        } else {
            return this.failed(ExchangeReleaseMsgCode.ERROR_TASK_INEXISTENCE);
        }
    }


}
