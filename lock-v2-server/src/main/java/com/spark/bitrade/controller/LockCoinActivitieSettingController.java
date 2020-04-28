package com.spark.bitrade.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.entity.LockCoinActivitieSetting;
import com.spark.bitrade.service.LockCoinActivitieSettingService;
import org.springframework.web.bind.annotation.*;
import com.spark.bitrade.util.*;
import io.swagger.annotations.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * (LockCoinActivitieSetting)控制层
 *
 * @author zhangYanjun
 * @since 2019-06-19 14:33:11
 */
@RestController
@RequestMapping("v2/lockCoinActivitieSetting")
@Api(description = "控制层")
public class LockCoinActivitieSettingController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private LockCoinActivitieSettingService lockCoinActivitieSettingService;

    /**
     * 分页查询所有数据
     *
     * @param size                     分页.每页数量
     * @param current                  分页.当前页码
     * @param lockCoinActivitieSetting 查询实体
     * @return 所有数据
     */
    @ApiOperation(value = "分页查询所有数据接口", notes = "分页查询所有数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "分页.每页数量。eg：10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "分页.当前页码.eg：从1开始", name = "current", defaultValue = "1", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "实体对象", name = "lockCoinActivitieSetting", dataTypeClass = LockCoinActivitieSetting.class)
    })
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<IPage<LockCoinActivitieSetting>> list(Integer size, Integer current, LockCoinActivitieSetting lockCoinActivitieSetting) {
        return success(this.lockCoinActivitieSettingService.page(new Page<>(current, size), new QueryWrapper<>(lockCoinActivitieSetting)));
    }

    /**
     * 通过主键查询单条数据
     * 备注：没有判断活动是否有效
     *
     * @param id 主键
     * @return 单条数据
     */
    @ApiOperation(value = "通过主键查询单条数据接口", notes = "通过主键查询单条数据接口")
    @ApiImplicitParam(value = "主键", name = "id", required = true)
    @RequestMapping(value = "/findOne", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<LockCoinActivitieSetting> findOne(@RequestParam("id") Long id) {
        return success(this.lockCoinActivitieSettingService.findOne(id));
    }

    /**
     * 根据活动方案ID查询子活动列表
     * 备注：没有判断活动是否有效
     *
     * @param projectId 活动方案ID
     * @return 集合数据
     */
    @ApiOperation(value = "根据活动方案ID查询子活动列表接口", notes = "根据活动方案ID查询子活动列表接口")
    @ApiImplicitParam(value = "活动方案ID", name = "projetcId", required = true)
    @RequestMapping(value = "/findList", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<List<LockCoinActivitieSetting>> findList(@RequestParam("projectId") Long projectId) {
        return success(this.lockCoinActivitieSettingService.findList(projectId));
    }

    /**
     * 通过主键查询有效的活动
     *
     * @param id 主键
     * @return 单条数据
     */
    @ApiOperation(value = "通过主键查询有效的活动数据接口", notes = "通过主键查询有效的活动数据接口")
    @ApiImplicitParam(value = "主键", name = "id", required = true)
    @RequestMapping(value = "/findOneByTime", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<LockCoinActivitieSetting> findOneByTime(@RequestParam("id") Long id) {
        return success(this.lockCoinActivitieSettingService.findOneByTime(id));
    }

    /**
     * 根据活动方案ID查询生效中子活动列表
     *
     * @param projectId 活动方案ID
     * @return 集合数据
     */
    @ApiOperation(value = "根据活动方案ID查询生效中子活动列表接口", notes = "根据活动方案ID查询生效中子活动列表接口")
    @ApiImplicitParam(value = "活动方案ID", name = "projectId", required = true)
    @RequestMapping(value = "/findListByTime", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<List<LockCoinActivitieSetting>> findListByTime(@RequestParam("projectId") Long projectId) {
        return success(this.lockCoinActivitieSettingService.findListByTime(projectId));
    }

//    /**
//     * 新增数据
//     *
//     * @param lockCoinActivitieSetting 实体对象
//     * @return 新增结果
//     */
//    @ApiOperation(value = "新增数据接口", notes = "新增数据接口")
//    @ApiImplicitParam(value = "实体对象", name = "lockCoinActivitieSetting", dataTypeClass =LockCoinActivitieSetting.class )
//    @RequestMapping(value = "/add", method = {RequestMethod.GET, RequestMethod.POST})
//    public MessageRespResult add(LockCoinActivitieSetting lockCoinActivitieSetting) {
//        return success(this.lockCoinActivitieSettingService.save(lockCoinActivitieSetting));
//    }

//    /**
//     * 修改数据
//     *
//     * @param lockCoinActivitieSetting 实体对象
//     * @return 修改结果
//     */
//    @ApiOperation(value = "修改数据接口", notes = "修改数据接口")
//    @ApiImplicitParam(value = "实体对象", name = "lockCoinActivitieSetting", dataTypeClass =LockCoinActivitieSetting.class )
//    @RequestMapping(value = "/update", method = {RequestMethod.GET, RequestMethod.POST})
//    public MessageRespResult update(LockCoinActivitieSetting lockCoinActivitieSetting) {
//        return success(this.lockCoinActivitieSettingService.updateById(lockCoinActivitieSetting));
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
//        return success(this.lockCoinActivitieSettingService.removeByIds(idList));
//    }
}