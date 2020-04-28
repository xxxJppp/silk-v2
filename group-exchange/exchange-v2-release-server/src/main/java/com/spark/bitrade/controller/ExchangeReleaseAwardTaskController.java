package com.spark.bitrade.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.entity.ExchangeReleaseAwardTask;
import com.spark.bitrade.service.ExchangeReleaseAwardTaskService;
import com.spark.bitrade.util.MessageRespResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 币币交易-推荐人奖励任务表(ExchangeReleaseAwardTask)控制层
 *
 * @author yangch
 * @since 2020-01-17 17:18:53
 */
@RestController
@RequestMapping("/service/v2/award/task")
@Api(description = "币币交易-推荐人奖励任务表控制层")
public class ExchangeReleaseAwardTaskController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private ExchangeReleaseAwardTaskService exchangeReleaseAwardTaskService;

    /**
     * 分页查询所有数据
     *
     * @param size                     分页.每页数量
     * @param current                  分页.当前页码
     * @param exchangeReleaseAwardTask 查询实体
     * @return 所有数据
     */
    @ApiOperation(value = "分页查询所有数据接口", notes = "分页查询所有数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "分页.每页数量。eg：10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "分页.当前页码.eg：从1开始", name = "current", defaultValue = "1", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "实体对象", name = "exchangeReleaseAwardTask", dataTypeClass = ExchangeReleaseAwardTask.class)
    })
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<IPage<ExchangeReleaseAwardTask>> list(Integer size, Integer current, ExchangeReleaseAwardTask exchangeReleaseAwardTask) {
        return success(this.exchangeReleaseAwardTaskService.page(new Page<>(current, size), new QueryWrapper<>(exchangeReleaseAwardTask)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @ApiOperation(value = "通过主键查询单条数据接口", notes = "通过主键查询单条数据接口")
    @ApiImplicitParam(value = "主键", name = "id", dataTypeClass = Serializable.class, required = true)
    @RequestMapping(value = "/get", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<ExchangeReleaseAwardTask> get(@RequestParam("id") Serializable id) {
        return success(this.exchangeReleaseAwardTaskService.getById(id));
    }

    @ApiOperation(value = "获取需要处理的任务IDs", notes = "返回获取需要处理的任务IDs")
    @RequestMapping(value = "/pending", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<List<Long>> records() {
        List<Long> collect = exchangeReleaseAwardTaskService.findAwaitReleaseTask().stream()
                .map(ExchangeReleaseAwardTask::getId).collect(Collectors.toList());
        return success(collect);
    }

    @ApiOperation(value = "释放任务", notes = "处理未处理的任务")
    @ApiImplicitParam(value = "任务ID", name = "taskId", dataType = "int", required = true)
    @RequestMapping(value = "/release", params = "taskId", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult release(@RequestParam("taskId") Long taskId) {
        exchangeReleaseAwardTaskService.releaseTask(taskId);
        return success();
    }

    @ApiOperation(value = "释放任务", notes = "批量处理未处理的任务")
    @ApiImplicitParam(value = "任务ID", name = "taskId", dataType = "int", required = true)
    @RequestMapping(value = "/release", params = "taskIds", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult release(@RequestParam("taskIds") List<Long> taskIds) {
        taskIds.parallelStream().forEach(exchangeReleaseAwardTaskService::releaseTask);
        return success();
    }

}