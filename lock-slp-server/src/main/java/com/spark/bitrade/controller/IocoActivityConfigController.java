package com.spark.bitrade.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.entity.IocoActivityConfig;
import com.spark.bitrade.service.IocoActivityConfigService;
import org.springframework.web.bind.annotation.*;
import com.spark.bitrade.util.*;
import com.spark.bitrade.controller.ApiController;
import io.swagger.annotations.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * ioco活动配置(IocoActivityConfig)表控制层
 *
 * @author daring5920
 * @since 2019-07-03 14:38:58
 */
@RestController
@RequestMapping("iocoActivityConfig")
@Api(description = "ioco活动配置表控制层")
public class IocoActivityConfigController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private IocoActivityConfigService iocoActivityConfigService;

    /**
     * 分页查询所有数据
     *
     * @param size 分页.每页数量
     * @param current 分页.当前页码
     * @param iocoActivityConfig 查询实体
     * @return 所有数据
     */
    @ApiOperation(value = "分页查询所有数据接口", notes = "分页查询所有数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "分页.每页数量。eg：10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "分页.当前页码.eg：从1开始", name = "current", defaultValue = "1", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "实体对象", name = "iocoActivityConfig", dataTypeClass =IocoActivityConfig.class )
    })
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<IPage<IocoActivityConfig>> list(Integer size, Integer current, IocoActivityConfig iocoActivityConfig) {
        return success(this.iocoActivityConfigService.page(new Page<>(current, size), new QueryWrapper<>(iocoActivityConfig)));
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
    public MessageRespResult<IocoActivityConfig> get(@RequestParam("id") Serializable id) {
        return success(this.iocoActivityConfigService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param iocoActivityConfig 实体对象
     * @return 新增结果
     */
    @ApiOperation(value = "新增数据接口", notes = "新增数据接口")
    @ApiImplicitParam(value = "实体对象", name = "iocoActivityConfig", dataTypeClass =IocoActivityConfig.class )
    @RequestMapping(value = "/add", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult add(IocoActivityConfig iocoActivityConfig) {
        return success(this.iocoActivityConfigService.save(iocoActivityConfig));
    }

    /**
     * 修改数据
     *
     * @param iocoActivityConfig 实体对象
     * @return 修改结果
     */
    @ApiOperation(value = "修改数据接口", notes = "修改数据接口")
    @ApiImplicitParam(value = "实体对象", name = "iocoActivityConfig", dataTypeClass =IocoActivityConfig.class )
    @RequestMapping(value = "/update", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult update(IocoActivityConfig iocoActivityConfig) {
        return success(this.iocoActivityConfigService.updateById(iocoActivityConfig));
    }

    /**
     * 删除数据
     *
     * @param idList 主键集合
     * @return 删除结果
     */
    @DeleteMapping
    @ApiOperation(value = "删除数据接口", notes = "删除数据接口")
    @ApiImplicitParam(value = "主键集合", name = "idList", dataTypeClass = List.class, required = true)
    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult delete(@RequestParam("idList") List<Serializable> idList) {
        return success(this.iocoActivityConfigService.removeByIds(idList));
    }
}