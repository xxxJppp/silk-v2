package com.spark.bitrade.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.entity.Coin;
import com.spark.bitrade.service.CoinService;
import org.springframework.web.bind.annotation.*;
import com.spark.bitrade.util.*;
import com.spark.bitrade.controller.ApiController;
import io.swagger.annotations.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * (Coin)控制层
 *
 * @author zhangYanjun
 * @since 2019-06-20 17:48:00
 */
@RestController
@RequestMapping("v2/coin")
@Api(description = "控制层")
public class CoinController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private CoinService coinService;

    /**
     * 分页查询所有数据
     *
     * @param size 分页.每页数量
     * @param current 分页.当前页码
     * @param coin 查询实体
     * @return 所有数据
     */
    @ApiOperation(value = "分页查询所有数据接口", notes = "分页查询所有数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "分页.每页数量。eg：10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "分页.当前页码.eg：从1开始", name = "current", defaultValue = "1", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "实体对象", name = "coin", dataTypeClass =Coin.class )
    })
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<IPage<Coin>> list(Integer size, Integer current, Coin coin) {
        return success(this.coinService.page(new Page<>(current, size), new QueryWrapper<>(coin)));
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
    public MessageRespResult<Coin> get(@RequestParam("id") Serializable id) {
        return success(this.coinService.getById(id));
    }


    /**
     * 通过unit获取coin_id
     *
     * @param unit 币种单位
     * @return 单条数据
     */
    @ApiOperation(value = "通过unit获取coin_id", notes = "通过unit获取coin_id")
    @ApiImplicitParam(value = "币种单位", name = "unit", dataTypeClass = String.class, required = true)
    @RequestMapping(value = "/getCoinNameByUnit", method = {RequestMethod.GET})
    public MessageRespResult<String> getCoinNameByUnit(@RequestParam("unit") String unit) {
        return success(this.coinService.findByUnit(unit).getName());
    }

//    /**
//     * 新增数据
//     *
//     * @param coin 实体对象
//     * @return 新增结果
//     */
//    @ApiOperation(value = "新增数据接口", notes = "新增数据接口")
//    @ApiImplicitParam(value = "实体对象", name = "coin", dataTypeClass =Coin.class )
//    @RequestMapping(value = "/add", method = {RequestMethod.GET, RequestMethod.POST})
//    public MessageRespResult add(Coin coin) {
//        return success(this.coinService.save(coin));
//    }
//
//    /**
//     * 修改数据
//     *
//     * @param coin 实体对象
//     * @return 修改结果
//     */
//    @ApiOperation(value = "修改数据接口", notes = "修改数据接口")
//    @ApiImplicitParam(value = "实体对象", name = "coin", dataTypeClass =Coin.class )
//    @RequestMapping(value = "/update", method = {RequestMethod.GET, RequestMethod.POST})
//    public MessageRespResult update(Coin coin) {
//        return success(this.coinService.updateById(coin));
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
//        return success(this.coinService.removeByIds(idList));
//    }
}