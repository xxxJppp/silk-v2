package com.spark.bitrade.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.entity.LockSlpReleaseTask;
import com.spark.bitrade.service.LockSlpReleaseTaskService;
import org.springframework.web.bind.annotation.*;
import com.spark.bitrade.util.*;
import com.spark.bitrade.controller.ApiController;
import io.swagger.annotations.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * 推荐人奖励基金释放任务表(LockSlpReleaseTask)控制层
 *
 * @author yangch
 * @since 2019-06-18 21:27:03
 */
@RestController
@RequestMapping("api/v2/lockSlpReleaseTask")
@Api(description = "推荐人奖励基金释放任务表控制层")
public class LockSlpReleaseTaskController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private LockSlpReleaseTaskService lockSlpReleaseTaskService;

    /**
     * 分页查询所有数据
     *
     * @param size 分页.每页数量
     * @param current 分页.当前页码
     * @param lockSlpReleaseTask 查询实体
     * @return 所有数据
     */
    @ApiOperation(value = "分页查询所有数据接口", notes = "分页查询所有数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "分页.每页数量。eg：10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "分页.当前页码.eg：从1开始", name = "current", defaultValue = "1", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "实体对象", name = "lockSlpReleaseTask", dataTypeClass =LockSlpReleaseTask.class )
    })
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<IPage<LockSlpReleaseTask>> list(Integer size, Integer current, LockSlpReleaseTask lockSlpReleaseTask) {
        return success(this.lockSlpReleaseTaskService.page(new Page<>(current, size), new QueryWrapper<>(lockSlpReleaseTask)));
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
    public MessageRespResult<LockSlpReleaseTask> get(@RequestParam("id") Serializable id) {
        return success(this.lockSlpReleaseTaskService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param lockSlpReleaseTask 实体对象
     * @return 新增结果
     */
    @ApiOperation(value = "新增数据接口", notes = "新增数据接口")
    @ApiImplicitParam(value = "实体对象", name = "lockSlpReleaseTask", dataTypeClass =LockSlpReleaseTask.class )
    @RequestMapping(value = "/add", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult add(LockSlpReleaseTask lockSlpReleaseTask) {
        return success(this.lockSlpReleaseTaskService.save(lockSlpReleaseTask));
    }

//    /**
//     * 修改数据
//     *
//     * @param lockSlpReleaseTask 实体对象
//     * @return 修改结果
//     */
//    @ApiOperation(value = "修改数据接口", notes = "修改数据接口")
//    @ApiImplicitParam(value = "实体对象", name = "lockSlpReleaseTask", dataTypeClass =LockSlpReleaseTask.class )
//    @RequestMapping(value = "/update", method = {RequestMethod.GET, RequestMethod.POST})
//    public MessageRespResult update(LockSlpReleaseTask lockSlpReleaseTask) {
//        return success(this.lockSlpReleaseTaskService.updateById(lockSlpReleaseTask));
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
//        return success(this.lockSlpReleaseTaskService.removeByIds(idList));
//    }
}