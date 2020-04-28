package com.spark.bitrade.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.entity.SilkPayMatchRecord;
import com.spark.bitrade.service.SilkPayMatchRecordService;
import com.spark.bitrade.util.MessageRespResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * 付款匹配记录(SilkPayMatchRecord)控制层
 *
 * @author wsy
 * @since 2019-07-18 10:38:51
 */
@RestController
@RequestMapping("v2/silkPayMatchRecord")
@Api(description = "付款匹配记录控制层")
public class SilkPayMatchRecordController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private SilkPayMatchRecordService silkPayMatchRecordService;

    /**
     * 分页查询所有数据
     *
     * @param size               分页.每页数量
     * @param current            分页.当前页码
     * @param silkPayMatchRecord 查询实体
     * @return 所有数据
     */
    @ApiOperation(value = "分页查询所有数据接口", notes = "分页查询所有数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "分页.每页数量。eg：10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "分页.当前页码.eg：从1开始", name = "current", defaultValue = "1", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "实体对象", name = "silkPayMatchRecord", dataTypeClass = SilkPayMatchRecord.class)
    })
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<IPage<SilkPayMatchRecord>> list(Integer size, Integer current, SilkPayMatchRecord silkPayMatchRecord) {
        return success(this.silkPayMatchRecordService.page(new Page<>(current, size), new QueryWrapper<>(silkPayMatchRecord)));
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
    public MessageRespResult<SilkPayMatchRecord> get(@RequestParam("id") Serializable id) {
        return success(this.silkPayMatchRecordService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param silkPayMatchRecord 实体对象
     * @return 新增结果
     */
    @ApiOperation(value = "新增数据接口", notes = "新增数据接口")
    @ApiImplicitParam(value = "实体对象", name = "silkPayMatchRecord", dataTypeClass = SilkPayMatchRecord.class)
    @RequestMapping(value = "/add", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult add(SilkPayMatchRecord silkPayMatchRecord) {
        return success(this.silkPayMatchRecordService.save(silkPayMatchRecord));
    }

    /**
     * 修改数据
     *
     * @param silkPayMatchRecord 实体对象
     * @return 修改结果
     */
    @ApiOperation(value = "修改数据接口", notes = "修改数据接口")
    @ApiImplicitParam(value = "实体对象", name = "silkPayMatchRecord", dataTypeClass = SilkPayMatchRecord.class)
    @RequestMapping(value = "/update", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult update(SilkPayMatchRecord silkPayMatchRecord) {
        return success(this.silkPayMatchRecordService.updateById(silkPayMatchRecord));
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
        return success(this.silkPayMatchRecordService.removeByIds(idList));
    }
}