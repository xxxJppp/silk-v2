package com.spark.bitrade.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.entity.ExchangeFastCoin;
import com.spark.bitrade.service.ExchangeFastCoinService;
import org.springframework.web.bind.annotation.*;
import com.spark.bitrade.util.*;
import com.spark.bitrade.controller.ApiController;
import io.swagger.annotations.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * 闪兑币种配置(ExchangeFastCoin)控制层
 *
 * @author yangch
 * @since 2019-06-24 17:06:44
 */
@RestController
@RequestMapping("api/v2/exchangeFastCoin")
@Api(description = "闪兑币种配置控制层")
public class ExchangeFastCoinController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private ExchangeFastCoinService exchangeFastCoinService;

    /**
     * 分页查询所有数据
     *
     * @param size 分页.每页数量
     * @param current 分页.当前页码
     * @param exchangeFastCoin 查询实体
     * @return 所有数据
     */
    @ApiOperation(value = "分页查询所有数据接口", notes = "分页查询所有数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "分页.每页数量。eg：10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "分页.当前页码.eg：从1开始", name = "current", defaultValue = "1", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "实体对象", name = "exchangeFastCoin", dataTypeClass =ExchangeFastCoin.class )
    })
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<IPage<ExchangeFastCoin>> list(Integer size, Integer current, ExchangeFastCoin exchangeFastCoin) {
        return success(this.exchangeFastCoinService.page(new Page<>(current, size), new QueryWrapper<>(exchangeFastCoin)));
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
    public MessageRespResult<ExchangeFastCoin> get(@RequestParam("id") Serializable id) {
        return success(this.exchangeFastCoinService.getById(id));
    }

//    /**
//     * 新增数据
//     *
//     * @param exchangeFastCoin 实体对象
//     * @return 新增结果
//     */
//    @ApiOperation(value = "新增数据接口", notes = "新增数据接口")
//    @ApiImplicitParam(value = "实体对象", name = "exchangeFastCoin", dataTypeClass =ExchangeFastCoin.class )
//    @RequestMapping(value = "/add", method = {RequestMethod.GET, RequestMethod.POST})
//    public MessageRespResult add(ExchangeFastCoin exchangeFastCoin) {
//        return success(this.exchangeFastCoinService.save(exchangeFastCoin));
//    }

//    /**
//     * 修改数据
//     *
//     * @param exchangeFastCoin 实体对象
//     * @return 修改结果
//     */
//    @ApiOperation(value = "修改数据接口", notes = "修改数据接口")
//    @ApiImplicitParam(value = "实体对象", name = "exchangeFastCoin", dataTypeClass =ExchangeFastCoin.class )
//    @RequestMapping(value = "/update", method = {RequestMethod.GET, RequestMethod.POST})
//    public MessageRespResult update(ExchangeFastCoin exchangeFastCoin) {
//        return success(this.exchangeFastCoinService.updateById(exchangeFastCoin));
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
//        return success(this.exchangeFastCoinService.removeByIds(idList));
//    }
}