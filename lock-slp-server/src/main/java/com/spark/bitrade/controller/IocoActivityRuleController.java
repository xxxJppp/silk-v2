package com.spark.bitrade.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.entity.IocoActivityRule;
import com.spark.bitrade.service.IocoActivityRuleService;
import org.springframework.web.bind.annotation.*;
import com.spark.bitrade.util.*;
import com.spark.bitrade.controller.ApiController;
import io.swagger.annotations.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * ioco对应活动规则(IocoActivityRule)表控制层
 *
 * @author daring5920
 * @since 2019-07-03 14:38:58
 */
@RestController
@RequestMapping("iocoActivityRule")
@Api(description = "ioco对应活动规则表控制层")
public class IocoActivityRuleController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private IocoActivityRuleService iocoActivityRuleService;

    /**
     * 分页查询所有数据
     *
     * @param size 分页.每页数量
     * @param current 分页.当前页码
     * @param iocoActivityRule 查询实体
     * @return 所有数据
     */
    @ApiOperation(value = "分页查询所有数据接口", notes = "分页查询所有数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "分页.每页数量。eg：10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "分页.当前页码.eg：从1开始", name = "current", defaultValue = "1", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "实体对象", name = "iocoActivityRule", dataTypeClass =IocoActivityRule.class )
    })
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<IPage<IocoActivityRule>> list(Integer size, Integer current, IocoActivityRule iocoActivityRule) {
        return success(this.iocoActivityRuleService.page(new Page<>(current, size), new QueryWrapper<>(iocoActivityRule)));
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
    public MessageRespResult<IocoActivityRule> get(@RequestParam("id") Serializable id) {
        return success(this.iocoActivityRuleService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param iocoActivityRule 实体对象
     * @return 新增结果
     */
    @ApiOperation(value = "新增数据接口", notes = "新增数据接口")
    @ApiImplicitParam(value = "实体对象", name = "iocoActivityRule", dataTypeClass =IocoActivityRule.class )
    @RequestMapping(value = "/add", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult add(IocoActivityRule iocoActivityRule) {
        return success(this.iocoActivityRuleService.save(iocoActivityRule));
    }

    /**
     * 修改数据
     *
     * @param iocoActivityRule 实体对象
     * @return 修改结果
     */
    @ApiOperation(value = "修改数据接口", notes = "修改数据接口")
    @ApiImplicitParam(value = "实体对象", name = "iocoActivityRule", dataTypeClass =IocoActivityRule.class )
    @RequestMapping(value = "/update", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult update(IocoActivityRule iocoActivityRule) {
        return success(this.iocoActivityRuleService.updateById(iocoActivityRule));
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
        return success(this.iocoActivityRuleService.removeByIds(idList));
    }
}