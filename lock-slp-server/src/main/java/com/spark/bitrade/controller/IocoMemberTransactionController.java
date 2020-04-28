package com.spark.bitrade.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.constants.LSMsgCode;
import com.spark.bitrade.entity.IocoMemberTransaction;
import com.spark.bitrade.entity.IocoMemberWallet;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.service.IocoMemberTransactionService;
import com.spark.bitrade.vo.IocoPurchaseTransactionVo;
import com.spark.bitrade.vo.IocoPurchaseVo;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.spark.bitrade.util.*;
import com.spark.bitrade.controller.ApiController;
import io.swagger.annotations.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * ioco钱包交易记录(IocoMemberTransaction)表控制层
 *
 * @author daring5920
 * @since 2019-07-03 14:38:58
 */
@RestController
@RequestMapping("iocoMemberTransaction")
@Api(description = "ioco钱包交易记录表控制层",tags = "IOCO-V1.0")
public class IocoMemberTransactionController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private IocoMemberTransactionService iocoMemberTransactionService;

    /**
     * 分页查询所有数据
     *
     * @param size 分页.每页数量
     * @param current 分页.当前页码
     * @param iocoMemberTransaction 查询实体
     * @return 所有数据
     *//*
    @ApiOperation(value = "分页查询所有数据接口", notes = "分页查询所有数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "分页.每页数量。eg：10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "分页.当前页码.eg：从1开始", name = "current", defaultValue = "1", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "实体对象", name = "iocoMemberTransaction", dataTypeClass =IocoMemberTransaction.class )
    })
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<IPage<IocoMemberTransaction>> list(Integer size, Integer current, IocoMemberTransaction iocoMemberTransaction) {
        return success(this.iocoMemberTransactionService.page(new Page<>(current, size), new QueryWrapper<>(iocoMemberTransaction)));
    }*/

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @ApiOperation(value = "通过主键查询单条数据接口", notes = "通过主键查询单条数据接口")
    @ApiImplicitParam(value = "主键", name = "id", dataTypeClass = Serializable.class, required = true)
    @RequestMapping(value = "/get", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<IocoMemberTransaction> get(@RequestParam("id") Serializable id) {
        return success(this.iocoMemberTransactionService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param iocoMemberTransaction 实体对象
     * @return 新增结果
     */
    @ApiOperation(value = "新增数据接口", notes = "新增数据接口")
    @ApiImplicitParam(value = "实体对象", name = "iocoMemberTransaction", dataTypeClass =IocoMemberTransaction.class )
    @RequestMapping(value = "/add", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult add(IocoMemberTransaction iocoMemberTransaction) {
        return success(this.iocoMemberTransactionService.save(iocoMemberTransaction));
    }

    /**
     * 修改数据
     *
     * @param iocoMemberTransaction 实体对象
     * @return 修改结果
     */
    @ApiOperation(value = "修改数据接口", notes = "修改数据接口")
    @ApiImplicitParam(value = "实体对象", name = "iocoMemberTransaction", dataTypeClass =IocoMemberTransaction.class )
    @RequestMapping(value = "/update", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult update(IocoMemberTransaction iocoMemberTransaction) {
        return success(this.iocoMemberTransactionService.updateById(iocoMemberTransaction));
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
        return success(this.iocoMemberTransactionService.removeByIds(idList));
    }


    /**
     * 获取ioco申购页面所需数据
     * @author shenzucai
     * @time 2019.07.04 8:21
     * @param memberId
     * @return true
     */
    @ApiOperation(value = "获取ioco申购页面所需数据", notes = "获取ioco申购页面所需数据")
    @PostMapping(value = "/purchaseIndex")
    public MessageRespResult<IocoPurchaseVo> getPurchaseData(@RequestParam("memberId") Long memberId) {
        // 调用service获取具体数据
        return success(iocoMemberTransactionService.getIocoIndexData(memberId));
    }


    /**
     * 获取ioco转赠页面所需数据
     * @author shenzucai
     * @time 2019.07.04 8:21
     * @param memberId
     * @return true
     */
    @ApiOperation(value = "获取ioco转赠页面所需数据", notes = "获取ioco转赠页面所需数据")
    @PostMapping(value = "/giftIndex")
    public MessageRespResult<IocoMemberWallet> giftIndex(@RequestParam("memberId") Long memberId) {
        // 调用service获取具体数据
        return success(iocoMemberTransactionService.giftIndex(memberId));
    }

    /**
     * ioco申购slp
     * @author shenzucai
     * @time 2019.07.04 8:21
     * @param memberId
     * @return true
     */
    @ApiOperation(value = "ioco申购slp", notes = "ioco申购slp")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "支付币种", name = "purchasetUnit", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(value = "支付数量", name = "purchaseAmount", dataTypeClass = BigDecimal.class, required = true),
            @ApiImplicitParam(value = "申购数量", name = "slpAmount", dataTypeClass = BigDecimal.class, required = true),
            @ApiImplicitParam(value = "申购份数", name = "share", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "当前活动id", name = "activityId", dataTypeClass = Long.class, required = true)
    })
    @PostMapping(value = "/purchaseSLP")
    public MessageRespResult<Boolean> purchaseSLP(@RequestParam("memberId") Long memberId
            ,@RequestParam("purchasetUnit") String purchasetUnit
            ,@RequestParam("purchaseAmount") BigDecimal purchaseAmount
            ,@RequestParam("slpAmount") BigDecimal slpAmount
            ,@RequestParam("share") Integer share
            ,@RequestParam("activityId") Long activityId) {
        // 调用service获取具体数据
        AssertUtil.isTrue(StringUtils.isNotEmpty(purchasetUnit), CommonMsgCode.REQUIRED_PARAMETER);
        AssertUtil.isTrue(!Objects.isNull(purchaseAmount), CommonMsgCode.REQUIRED_PARAMETER);
        AssertUtil.isTrue(!Objects.isNull(slpAmount), CommonMsgCode.REQUIRED_PARAMETER);
        AssertUtil.isTrue(!Objects.isNull(share), CommonMsgCode.REQUIRED_PARAMETER);
        AssertUtil.isTrue(!Objects.isNull(activityId), CommonMsgCode.REQUIRED_PARAMETER);
        AssertUtil.isTrue(share >= 1, LSMsgCode.IOCO_AMOUNT_SAMLL);
        return success(iocoMemberTransactionService.purchaseSLP(memberId ,purchasetUnit,purchaseAmount,slpAmount,share,activityId));
    }


    /**
     * ioco转赠slp
     * @author shenzucai
     * @time 2019.07.04 8:21
     * @param memberId
     * @return true
     */
    @ApiOperation(value = "ioco转赠slp", notes = "ioco转赠slp")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "转赠币种", name = "giftUnit", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(value = "转赠数量", name = "giftAmount", dataTypeClass = BigDecimal.class, required = true),
            @ApiImplicitParam(value = "转赠对象（手机号或邮箱）", name = "giftTo", dataTypeClass = String.class, required = true)
    })
    @PostMapping(value = "/giftSLP")
    public MessageRespResult<Boolean> giftSLP(@RequestParam("memberId") Long memberId
            ,@RequestParam("giftUnit") String giftUnit
            ,@RequestParam("giftAmount") BigDecimal giftAmount
            ,@RequestParam("giftTo") String giftTo) {
        // 调用service获取具体数据
        AssertUtil.isTrue(StringUtils.isNotEmpty(giftUnit), CommonMsgCode.REQUIRED_PARAMETER);
        AssertUtil.isTrue(!Objects.isNull(giftAmount), CommonMsgCode.REQUIRED_PARAMETER);
        AssertUtil.isTrue(StringUtils.isNotEmpty(giftTo), CommonMsgCode.REQUIRED_PARAMETER);
        AssertUtil.isTrue(giftAmount.doubleValue() > 0, LSMsgCode.IOCO_AMOUNT_SAMLL);
        return success(iocoMemberTransactionService.giftSLP(memberId ,giftUnit,giftAmount,giftTo));
    }

    /**
     * 分页查询所有转账数据
     *
     * @param size 分页.每页数量
     * @param current 分页.当前页码
     * @param type 转账类型0 申购，1是转赠
     * @return 所有数据
     */
    @ApiOperation(value = "分页查询ioco所有数据接口", notes = "分页查询ioco所有数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "分页.每页数量。eg：10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "分页.当前页码.eg：从1开始", name = "current", defaultValue = "1", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "转账类型0 申购，1是转赠", name = "type", dataTypeClass =Integer.class)
    })
    @PostMapping(value = "/listByType")
    public MessageRespResult<IPage<IocoPurchaseTransactionVo>> listByType(@RequestParam("memberId") Long memberId, @RequestParam("size") Integer size, @RequestParam("current") Integer current, @RequestParam("type") Integer type) {
        return success(iocoMemberTransactionService.listByType(memberId ,size,current,type));
    }
}