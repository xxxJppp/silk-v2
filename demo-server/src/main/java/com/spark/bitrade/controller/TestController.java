package com.spark.bitrade.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.codingapi.txlcn.tc.annotation.DTXPropagation;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.codingapi.txlcn.tc.annotation.TccTransaction;
import com.spark.bitrade.entity.Test;
import com.spark.bitrade.service.TestService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.spark.bitrade.util.*;
import com.spark.bitrade.controller.ApiController;
import io.swagger.annotations.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * (Test)表控制层
 *
 * @author young
 * @since 2019-06-09 15:56:37
 */
@RestController
@RequestMapping("test")
@Api(description = "表控制层")
public class TestController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private TestService testService;

    /**
     * 分页查询所有数据
     *
     * @param size    分页.每页数量
     * @param current 分页.当前页码
     * @param test    查询实体
     * @return 所有数据
     */
    @ApiOperation(value = "分页查询所有数据接口", notes = "分页查询所有数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "分页.每页数量。eg：10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "分页.当前页码.eg：从1开始", name = "current", defaultValue = "1", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "实体对象", name = "test", dataTypeClass = Test.class)
    })
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<IPage<Test>> list(Integer size, Integer current, Test test) {
        return success(this.testService.page(new Page<>(current, size), new QueryWrapper<>(test)));
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
    public MessageRespResult<Test> get(@RequestParam("id") Serializable id) {
        return success(this.testService.getById(id));
    }

    /**
     * 新增数据(分布式测试)
     *
     * @param test 实体对象
     * @return 新增结果
     */
    @RequestMapping(value = "/add", method = {RequestMethod.GET, RequestMethod.POST})
    //@LcnTransaction
    @TccTransaction(propagation = DTXPropagation.SUPPORTS,confirmMethod = "add", cancelMethod = "canceladd")
    @Transactional
    public MessageRespResult add(@RequestBody Test test) {
        System.out.println("add---"+test);
        return success(this.testService.save(test));
    }
    public MessageRespResult canceladd(@RequestBody Test test) {
        System.out.println("canceladd---"+test);
        return success(this.testService.removeById(test.getCol1()));
    }

    /**
     * 新增数据
     *
     * @param test 实体对象
     * @return 新增结果
     */
    @ApiOperation(value = "新增数据接口", notes = "新增数据接口")
    @ApiImplicitParam(value = "实体对象", name = "test", dataTypeClass = Test.class)
    @RequestMapping(value = "/add2", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult add2(Test test) {
        return success(this.testService.save(test));
    }

    /**
     * 修改数据
     *
     * @param test 实体对象
     * @return 修改结果
     */
    @ApiOperation(value = "修改数据接口", notes = "修改数据接口")
    @ApiImplicitParam(value = "实体对象", name = "test", dataTypeClass = Test.class)
    @RequestMapping(value = "/update", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult update(Test test) {
        return success(this.testService.updateById(test));
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
        return success(this.testService.removeByIds(idList));
    }

    //----------------
    @ApiOperation(value = "事务测试接口", notes = "事务测试,测试插入1000，异常后是否回滚")
    @RequestMapping(value = "/testTransaction", method = {RequestMethod.GET})
    public MessageRespResult testTransaction() {
        return success(this.testService.testTransaction());
    }

    @ApiOperation(value = "事务测试接口2", notes = "事务测试,测试插入2000X，异常后是否回滚（同一类中的多个保存方法）")
    @RequestMapping(value = "/testTransaction2", method = {RequestMethod.GET})
    public MessageRespResult testTransaction2() {
        return success(this.testService.testTransaction2());
    }
}