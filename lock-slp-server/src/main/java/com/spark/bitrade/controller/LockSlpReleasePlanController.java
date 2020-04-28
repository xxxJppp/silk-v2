package com.spark.bitrade.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.constant.SlpProcessStatus;
import com.spark.bitrade.consumer.task.impl.HandleLockSlpTask;
import com.spark.bitrade.entity.LockSlpReleasePlan;
import com.spark.bitrade.job.DailyReleaseJob;
import com.spark.bitrade.mq.TaskMessage;
import com.spark.bitrade.mq.TaskMessageWrapper;
import com.spark.bitrade.service.LockSlpReleasePlanService;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.vo.JobReceipt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 本金返还计划表(LockSlpReleasePlan)控制层
 *
 * @author yangch
 * @since 2019-06-18 21:27:03
 */
@RestController
@RequestMapping("api/v2/lockSlpReleasePlan")
@Api(description = "本金返还计划表控制层")
@Slf4j
public class LockSlpReleasePlanController extends ApiController {

    /**
     * 服务对象
     */
    @Resource
    private LockSlpReleasePlanService lockSlpReleasePlanService;

    private DailyReleaseJob dailyReleaseJob;
    private TaskMessageWrapper taskMessageWrapper;
    @Autowired
    private HandleLockSlpTask handleLockSlpTask;


    /**
     * 分页查询所有数据
     *
     * @param size               分页.每页数量
     * @param current            分页.当前页码
     * @param lockSlpReleasePlan 查询实体
     * @return 所有数据
     */
    @ApiOperation(value = "分页查询所有数据接口", notes = "分页查询所有数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "分页.每页数量。eg：10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "分页.当前页码.eg：从1开始", name = "current", defaultValue = "1", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "实体对象", name = "lockSlpReleasePlan", dataTypeClass = LockSlpReleasePlan.class)
    })
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<IPage<LockSlpReleasePlan>> list(Integer size, Integer current, LockSlpReleasePlan lockSlpReleasePlan) {
        return success(this.lockSlpReleasePlanService.page(new Page<>(current, size), new QueryWrapper<>(lockSlpReleasePlan)));
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
    public MessageRespResult<LockSlpReleasePlan> get(@RequestParam("id") Serializable id) {
        return success(this.lockSlpReleasePlanService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param lockSlpReleasePlan 实体对象
     * @return 新增结果
     */
    @ApiOperation(value = "新增数据接口", notes = "新增数据接口")
    @ApiImplicitParam(value = "实体对象", name = "lockSlpReleasePlan", dataTypeClass = LockSlpReleasePlan.class)
    @RequestMapping(value = "/add", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult add(LockSlpReleasePlan lockSlpReleasePlan) {
        return success(this.lockSlpReleasePlanService.save(lockSlpReleasePlan));
    }

    /**
     * 获取规定时间返回类应该释放的返还计划
     *
     * @param releaseAt 最后释放时间戳
     * @return resp
     */
    @GetMapping("/range")
    public MessageRespResult<List<LockSlpReleasePlan>> getLockSlpReleasePlan(@RequestParam("releaseAt") Long releaseAt) {
        return success(lockSlpReleasePlanService.getLatestReleaseAt(releaseAt));
    }

    /**
     * 释放任务
     *
     * @param planId   返还计划ID
     * @param datetime 日期
     * @return resp
     */
    @PostMapping("/release")
    public MessageRespResult<JobReceipt> releaseLockSlpReleasePlan(@RequestParam("planId") Long planId,
                                                                   @RequestParam("datetime") String datetime) {
        JobReceipt execute = dailyReleaseJob.execute(planId, datetime);
        taskMessageWrapper.flush(planId);
        return success(execute);
    }

    /**
     * 释放任务检查
     *
     * @return resp
     */
    @PostMapping("/check")
    public MessageRespResult<JobReceipt> releaseCompletedCheck() {
        JobReceipt execute = dailyReleaseJob.completeCheck();
        return success(execute);
    }

    @ApiOperation(value = "触发更新任务的重做接口", notes = "触发更新任务的重做接口")
    @RequestMapping(value = "/updateRedo", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult updateRedo() {
        QueryWrapper<LockSlpReleasePlan> wrapper = new QueryWrapper<>();
        wrapper.eq("task_status", SlpProcessStatus.NOT_PROCESSED);
        List<LockSlpReleasePlan> list = lockSlpReleasePlanService.list(wrapper);
        log.info("更新任务重做开始==============================");
        for (LockSlpReleasePlan releasePlan : list) {
            log.info("plan_id-{}==============================", releasePlan.getId());
            List<LockSlpReleasePlan> plan = new ArrayList<>();
            plan.add(releasePlan);
            try {
                //推送广播消息
                List<TaskMessage> nextTask = null;
                nextTask = handleLockSlpTask.next(plan, null);
                if (nextTask != null) {
                    for (TaskMessage taskMessage : nextTask) {
                        taskMessageWrapper.dispatch(taskMessage.getTopic(), taskMessage.stringify(), 2);
                    }
                } else {
                    log.info("无后续任务 [ plan_id = {} ] ", releasePlan.getId());
                }
            } catch (Exception ex) {
                log.error("触发更新任务的重做失败：plan_id-{}", releasePlan.getId());
            }
        }
        return success();
    }


//    /**
//     * 修改数据
//     *
//     * @param lockSlpReleasePlan 实体对象
//     * @return 修改结果
//     */
//    @ApiOperation(value = "修改数据接口", notes = "修改数据接口")
//    @ApiImplicitParam(value = "实体对象", name = "lockSlpReleasePlan", dataTypeClass =LockSlpReleasePlan.class )
//    @RequestMapping(value = "/update", method = {RequestMethod.GET, RequestMethod.POST})
//    public MessageRespResult update(LockSlpReleasePlan lockSlpReleasePlan) {
//        return success(this.lockSlpReleasePlanService.updateById(lockSlpReleasePlan));
//    }
//
//    /**
//     * 删除数据
//     *
//     * @param idList 主键集合
//     * @return 删除结果
//     */
//    @DeleteMapping
//    @ApiOperation(value = "删除数据接口", notes = "删除数据接口")
//    @ApiImplicitParam(value = "主键集合", name = "idList", dataTypeClass = List.class, required = true)
//    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
//    public MessageRespResult delete(@RequestParam("idList") List<Serializable> idList) {
//        return success(this.lockSlpReleasePlanService.removeByIds(idList));
//    }


    @Autowired
    public void setDailyReleaseJob(DailyReleaseJob dailyReleaseJob) {
        this.dailyReleaseJob = dailyReleaseJob;
    }

    @Autowired
    public void setTaskMessageWrapper(TaskMessageWrapper taskMessageWrapper) {
        this.taskMessageWrapper = taskMessageWrapper;
    }
}