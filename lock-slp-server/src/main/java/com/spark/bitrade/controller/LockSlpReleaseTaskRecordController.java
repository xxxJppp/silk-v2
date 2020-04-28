package com.spark.bitrade.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.entity.LockSlpReleaseTaskRecord;
import com.spark.bitrade.job.ReleaseHandleJob;
import com.spark.bitrade.service.LockSlpReleaseTaskRecordService;
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
 * 推荐人奖励基金释放记录表(LockSlpReleaseTaskRecord)控制层
 *
 * @author yangch
 * @since 2019-06-18 21:27:03
 */
@RestController
@RequestMapping("api/v2/lockSlpReleaseTaskRecord")
@Api(description = "推荐人奖励基金释放记录表控制层")
@Slf4j
public class LockSlpReleaseTaskRecordController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private LockSlpReleaseTaskRecordService lockSlpReleaseTaskRecordService;
    @Autowired
    private ReleaseHandleJob releaseHandleJob;

    /**
     * 分页查询所有数据
     *
     * @param size 分页.每页数量
     * @param current 分页.当前页码
     * @param lockSlpReleaseTaskRecord 查询实体
     * @return 所有数据
     */
    @ApiOperation(value = "分页查询所有数据接口", notes = "分页查询所有数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "分页.每页数量。eg：10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "分页.当前页码.eg：从1开始", name = "current", defaultValue = "1", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "实体对象", name = "lockSlpReleaseTaskRecord", dataTypeClass =LockSlpReleaseTaskRecord.class )
    })
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<IPage<LockSlpReleaseTaskRecord>> list(Integer size, Integer current, LockSlpReleaseTaskRecord lockSlpReleaseTaskRecord) {
        return success(this.lockSlpReleaseTaskRecordService.page(new Page<>(current, size), new QueryWrapper<>(lockSlpReleaseTaskRecord)));
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
    public MessageRespResult<LockSlpReleaseTaskRecord> get(@RequestParam("id") Serializable id) {
        return success(this.lockSlpReleaseTaskRecordService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param lockSlpReleaseTaskRecord 实体对象
     * @return 新增结果
     */
    @ApiOperation(value = "新增数据接口", notes = "新增数据接口")
    @ApiImplicitParam(value = "实体对象", name = "lockSlpReleaseTaskRecord", dataTypeClass =LockSlpReleaseTaskRecord.class )
    @RequestMapping(value = "/add", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult add(LockSlpReleaseTaskRecord lockSlpReleaseTaskRecord) {
        return success(this.lockSlpReleaseTaskRecordService.save(lockSlpReleaseTaskRecord));
    }

//    /**
//     * 修改数据
//     *
//     * @param lockSlpReleaseTaskRecord 实体对象
//     * @return 修改结果
//     */
//    @ApiOperation(value = "修改数据接口", notes = "修改数据接口")
//    @ApiImplicitParam(value = "实体对象", name = "lockSlpReleaseTaskRecord", dataTypeClass =LockSlpReleaseTaskRecord.class )
//    @RequestMapping(value = "/update", method = {RequestMethod.GET, RequestMethod.POST})
//    public MessageRespResult update(LockSlpReleaseTaskRecord lockSlpReleaseTaskRecord) {
//        return success(this.lockSlpReleaseTaskRecordService.updateById(lockSlpReleaseTaskRecord));
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
//        return success(this.lockSlpReleaseTaskRecordService.removeByIds(idList));
//    }


    //TODO 加速释放记录重做接口（releaseHandleJob.execute(target)）
    @ApiOperation(value = "加速释放记录重做接口", notes = "加速释放记录重做接口")
    @RequestMapping(value = "/planRecordRedo", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult planRecordRedo() {
        List<LockSlpReleaseTaskRecord> list = lockSlpReleaseTaskRecordService.getHandleFaild();
        for (LockSlpReleaseTaskRecord taskRecord : list) {
            try {
                releaseHandleJob.execute(taskRecord);
            } catch (Exception ex) {
                log.error("加速释放任务处理失败:task_record_id-{}", taskRecord.getId(), ex);
            }
        }

        return success();
    }
}