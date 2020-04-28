package com.spark.bitrade.controller;


import com.spark.bitrade.entity.SilkDataDist;
import com.spark.bitrade.service.SilkDataDistService;
import com.spark.bitrade.util.MessageRespResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 系统配置(SilkDataDist)控制层
 *
 * @author yangch
 * @since 2019-06-22 15:11:16
 */
@RestController
@RequestMapping("api/v2/silkDataDist")
@Api(description = "系统配置控制层")
public class SilkDataDistController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private SilkDataDistService silkDataDistService;

    /**
     * 查询指定配置ID下所有配置数据接口
     *
     * @param id 配置编号
     * @return 所有数据
     */
    @ApiOperation(value = "查询指定配置ID下所有配置数据接口", notes = "查询指定配置ID下所有配置数据接口")
    @ApiImplicitParam(value = "配置编号", name = "id", required = true)
    @RequestMapping(value = {"/list", "/no-auth/list"}, method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<List<SilkDataDist>> list(@RequestParam("id") String id) {
        return success(this.silkDataDistService.findListById(id));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id  配置编号
     * @param key 配置KEY
     * @return 单条数据
     */
    @ApiOperation(value = "通过主键查询单条数据接口", notes = "通过主键查询单条数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "配置编号", name = "id", required = true),
            @ApiImplicitParam(value = "配置KEY", name = "key", required = true),
    })
    @RequestMapping(value = {"/findOne", "/no-auth/findOne"}, method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<SilkDataDist> findOne(@RequestParam("id") String id, @RequestParam("key") String key) {
        return success(this.silkDataDistService.findByIdAndKey(id, key));
    }

//    /**
//     * 新增数据
//     *
//     * @param silkDataDist 实体对象
//     * @return 新增结果
//     */
//    @ApiOperation(value = "新增数据接口", notes = "新增数据接口")
//    @ApiImplicitParam(value = "实体对象", name = "silkDataDist", dataTypeClass =SilkDataDist.class )
//    @RequestMapping(value = "/add", method = {RequestMethod.GET, RequestMethod.POST})
//    public MessageRespResult add(SilkDataDist silkDataDist) {
//        return success(this.silkDataDistService.save(silkDataDist));
//    }
//
//    /**
//     * 修改数据
//     *
//     * @param silkDataDist 实体对象
//     * @return 修改结果
//     */
//    @ApiOperation(value = "修改数据接口", notes = "修改数据接口")
//    @ApiImplicitParam(value = "实体对象", name = "silkDataDist", dataTypeClass =SilkDataDist.class )
//    @RequestMapping(value = "/update", method = {RequestMethod.GET, RequestMethod.POST})
//    public MessageRespResult update(SilkDataDist silkDataDist) {
//        return success(this.silkDataDistService.updateById(silkDataDist));
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
//        return success(this.silkDataDistService.removeByIds(idList));
//    }
}