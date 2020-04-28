package com.spark.bitrade.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.constant.LockStatus;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.LockCoinDetail;
import com.spark.bitrade.service.LockCoinDetailService;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.MessageRespResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;

/**
 * (LockCoinDetail)控制层
 *
 * @author zhangYanjun
 * @since 2019-06-19 15:57:04
 */
@RestController
@RequestMapping("v2/lockCoinDetail")
@Api(description = "控制层")
public class LockCoinDetailController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private LockCoinDetailService lockCoinDetailService;

    /**
     * 分页查询所有数据
     *
     * @param size           分页.每页数量
     * @param current        分页.当前页码
     * @param lockCoinDetail 查询实体
     * @return 所有数据
     */
    @ApiOperation(value = "分页查询所有数据接口", notes = "分页查询所有数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "分页.每页数量。eg：10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "分页.当前页码.eg：从1开始", name = "current", defaultValue = "1", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "实体对象", name = "lockCoinDetail", dataTypeClass = LockCoinDetail.class)
    })
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<IPage<LockCoinDetail>> list(Integer size, Integer current, LockCoinDetail lockCoinDetail) {
        return success(this.lockCoinDetailService.page(new Page<>(current, size), new QueryWrapper<>(lockCoinDetail)));
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
    public MessageRespResult<LockCoinDetail> get(@RequestParam("id") Serializable id) {
        return success(this.lockCoinDetailService.getById(id));
    }

//    /**
//     * 新增数据
//     *
//     * @param lockCoinDetail 实体对象
//     * @return 新增结果
//     */
//    @ApiOperation(value = "新增数据接口", notes = "新增数据接口")
//    @ApiImplicitParam(value = "实体对象", name = "lockCoinDetail", dataTypeClass =LockCoinDetail.class )
//    @RequestMapping(value = "/add", method = {RequestMethod.GET, RequestMethod.POST})
//    public MessageRespResult add(LockCoinDetail lockCoinDetail) {
//        return success(this.lockCoinDetailService.save(lockCoinDetail));
//    }
//
//    /**
//     * 修改数据
//     *
//     * @param lockCoinDetail 实体对象
//     * @return 修改结果
//     */
//    @ApiOperation(value = "修改数据接口", notes = "修改数据接口")
//    @ApiImplicitParam(value = "实体对象", name = "lockCoinDetail", dataTypeClass =LockCoinDetail.class )
//    @RequestMapping(value = "/update", method = {RequestMethod.GET, RequestMethod.POST})
//    public MessageRespResult update(LockCoinDetail lockCoinDetail) {
//        return success(this.lockCoinDetailService.updateById(lockCoinDetail));
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
//        return success(this.lockCoinDetailService.removeByIds(idList));
//    }

    /**
     * 根据id修改返佣状态(未返佣-》已返佣)
     *
     * @param id
     * @return com.spark.bitrade.util.MessageRespResult<java.lang.Boolean>
     * @author zhangYanjun
     * @time 2019.06.21 16:18
     */
    @ApiOperation(value = "根据id修改返佣状态", notes = "根据id修改返佣状态")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "锁仓详情id", name = "id", dataTypeClass = Long.class, dataType = "Long", required = true)
    })
    @PostMapping("updateRewardStatusToCompleteById")
    public MessageRespResult<Boolean> updateRewardStatusToCompleteById(Long id) {
        AssertUtil.notNull(id, CommonMsgCode.REQUIRED_PARAMETER);
        return success(lockCoinDetailService.updateRewardStatusToCompleteById(id));
    }

    @ApiOperation(value = "根据id修改状态", notes = "根据id修改状态")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "锁仓详情id", name = "id", dataTypeClass = Long.class, dataType = "Long", required = true),
            @ApiImplicitParam(value = "旧的状态", name = "oldStatus", dataTypeClass = LockStatus.class, dataType = "LockStatus", required = true),
            @ApiImplicitParam(value = "新的状态", name = "newStatus", dataTypeClass = LockStatus.class, dataType = "LockStatus", required = true)
    })
    @PostMapping("updateStatusById")
    public MessageRespResult<Boolean> updateStatusById(Long id, LockStatus oldStatus, LockStatus newStatus) {
        AssertUtil.notNull(id, CommonMsgCode.REQUIRED_PARAMETER);
        AssertUtil.notNull(oldStatus, CommonMsgCode.REQUIRED_PARAMETER);
        AssertUtil.notNull(newStatus, CommonMsgCode.REQUIRED_PARAMETER);
        return success(lockCoinDetailService.updateStatusTById(id, oldStatus, newStatus));
    }


}