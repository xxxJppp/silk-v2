package com.spark.bitrade.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.entity.LockSlpReleasePlanRecord;
import com.spark.bitrade.job.ReleaseHandleJob;
import com.spark.bitrade.service.LockSlpReleasePlanRecordService;
import com.spark.bitrade.util.MessageRespResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * 本金返还记录表(LockSlpReleasePlanRecord)控制层
 *
 * @author yangch
 * @since 2019-06-18 21:27:03
 */
@RestController
@RequestMapping("api/v2/lockSlpReleasePlanRecord")
@Api(description = "本金返还记录表控制层")
@Slf4j
public class LockSlpReleasePlanRecordController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private LockSlpReleasePlanRecordService lockSlpReleasePlanRecordService;
    @Autowired
    private ReleaseHandleJob releaseHandleJob;

    /**
     * 分页查询所有数据
     *
     * @param size                     分页.每页数量
     * @param current                  分页.当前页码
     * @param lockSlpReleasePlanRecord 查询实体
     * @return 所有数据
     */
    @ApiOperation(value = "分页查询所有数据接口", notes = "分页查询所有数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "分页.每页数量。eg：10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "分页.当前页码.eg：从1开始", name = "current", defaultValue = "1", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "实体对象", name = "lockSlpReleasePlanRecord", dataTypeClass = LockSlpReleasePlanRecord.class)
    })
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<IPage<LockSlpReleasePlanRecord>> list(Integer size, Integer current, LockSlpReleasePlanRecord lockSlpReleasePlanRecord) {
        return success(this.lockSlpReleasePlanRecordService.page(new Page<>(current, size), new QueryWrapper<>(lockSlpReleasePlanRecord)));
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
    public MessageRespResult<LockSlpReleasePlanRecord> get(@RequestParam("id") Serializable id) {
        return success(this.lockSlpReleasePlanRecordService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param lockSlpReleasePlanRecord 实体对象
     * @return 新增结果
     */
    @ApiOperation(value = "新增数据接口", notes = "新增数据接口")
    @ApiImplicitParam(value = "实体对象", name = "lockSlpReleasePlanRecord", dataTypeClass = LockSlpReleasePlanRecord.class)
    @RequestMapping(value = "/add", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult add(LockSlpReleasePlanRecord lockSlpReleasePlanRecord) {
        return success(this.lockSlpReleasePlanRecordService.save(lockSlpReleasePlanRecord));
    }

//    /**
//     * 修改数据
//     *
//     * @param lockSlpReleasePlanRecord 实体对象
//     * @return 修改结果
//     */
//    @ApiOperation(value = "修改数据接口", notes = "修改数据接口")
//    @ApiImplicitParam(value = "实体对象", name = "lockSlpReleasePlanRecord", dataTypeClass =LockSlpReleasePlanRecord.class )
//    @RequestMapping(value = "/update", method = {RequestMethod.GET, RequestMethod.POST})
//    public MessageRespResult update(LockSlpReleasePlanRecord lockSlpReleasePlanRecord) {
//        return success(this.lockSlpReleasePlanRecordService.updateById(lockSlpReleasePlanRecord));
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
//        return success(this.lockSlpReleasePlanRecordService.removeByIds(idList));
//    }

    //TODO 本金返还记录重做接口（releaseHandleJob.execute(target)）
    @ApiOperation(value = "本金返还记录重做接口", notes = "本金返还记录重做接口")
    @RequestMapping(value = "/planRecordRedo", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult planRecordRedo() {
        List<LockSlpReleasePlanRecord> list = lockSlpReleasePlanRecordService.getHandleFaild();
        for (LockSlpReleasePlanRecord planRecord : list) {
            try {
                releaseHandleJob.execute(planRecord);
            } catch (Exception ex) {
                log.error("本金释放任务处理失败:plan_record_id-{}", planRecord.getId(), ex);
            }
        }

        return success();
    }


}