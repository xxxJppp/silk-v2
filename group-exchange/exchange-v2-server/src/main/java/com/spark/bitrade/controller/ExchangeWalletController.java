package com.spark.bitrade.controller;


import com.spark.bitrade.entity.ExchangeWallet;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.service.ExchangeWalletOperations;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * 币币交易钱包(ExchangeWallet)控制层
 *
 * @author archx
 * @since 2019-09-02 14:42:41
 */
@RestController
@RequestMapping("api/v2/wallet")
@Api(description = "机器人钱包控制层")
public class ExchangeWalletController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private ExchangeWalletOperations exchangeWalletOperations;

    /**
     * 获取指定币种的可用余额
     *
     * @param coinUnit 币种
     * @return 单条数据
     */
    @ApiOperation(value = "获取指定币种的可用余额数据接口", notes = "获取指定币种的可用余额数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户ID", name = "memberId", dataType = "int", required = true),
            @ApiImplicitParam(value = "币种", name = "coinUnit", dataTypeClass = String.class, required = true)
    })
    @RequestMapping(value = "/asset", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<ExchangeWallet> asset(@MemberAccount Member member, @RequestParam("coinUnit") String coinUnit) {
        Optional<ExchangeWallet> optional = this.exchangeWalletOperations.balance(member.getId(), coinUnit);
        if (optional.isPresent()) {
            return success(optional.get());
        }
        return failed();
    }


//    /**
//     * 分页查询所有数据
//     *
//     * @param size      分页.每页数量
//     * @param current   分页.当前页码
//     * @param exchangeWallet 查询实体
//     * @return 所有数据
//     */
//    @ApiOperation(value = "分页查询所有数据接口", notes = "分页查询所有数据接口")
//    @ApiImplicitParams({
//            @ApiImplicitParam(value = "分页.每页数量。eg：10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true),
//            @ApiImplicitParam(value = "分页.当前页码.eg：从1开始", name = "current", defaultValue = "1", dataTypeClass = Integer.class, required = true),
//            @ApiImplicitParam(value = "实体对象", name = "exchangeWallet", dataTypeClass = ExchangeWallet.class)
//    })
//    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
//    public MessageRespResult<IPage<ExchangeWallet>> list(Integer size, Integer current, ExchangeWallet exchangeWallet) {
//        return success(this.exchangeWalletService.page(new Page<>(current, size), new QueryWrapper<>(exchangeWallet)));
//    }
//
//    /**
//     * 通过主键查询单条数据
//     *
//     * @param id 主键
//     * @return 单条数据
//     */
//    @ApiOperation(value = "通过主键查询单条数据接口", notes = "通过主键查询单条数据接口")
//    @ApiImplicitParam(value = "主键", name = "id", dataTypeClass = Serializable.class, required = true)
//    @RequestMapping(value = "/get", method = {RequestMethod.GET, RequestMethod.POST})
//    public MessageRespResult<ExchangeWallet> get(@RequestParam("id") Serializable id) {
//        return success(this.exchangeWalletService.getById(id));
//    }
//
//    /**
//     * 新增数据
//     *
//     * @param exchangeWallet 实体对象
//     * @return 新增结果
//     */
//    @ApiOperation(value = "新增数据接口", notes = "新增数据接口")
//    @ApiImplicitParam(value = "实体对象", name = "exchangeWallet", dataTypeClass = ExchangeWallet.class)
//    @RequestMapping(value = "/add", method = {RequestMethod.GET, RequestMethod.POST})
//    public MessageRespResult add(ExchangeWallet exchangeWallet) {
//        return success(this.exchangeWalletService.save(exchangeWallet));
//    }
//
//    /**
//     * 修改数据
//     *
//     * @param exchangeWallet 实体对象
//     * @return 修改结果
//     */
//    @ApiOperation(value = "修改数据接口", notes = "修改数据接口")
//    @ApiImplicitParam(value = "实体对象", name = "exchangeWallet", dataTypeClass = ExchangeWallet.class)
//    @RequestMapping(value = "/update", method = {RequestMethod.GET, RequestMethod.POST})
//    public MessageRespResult update(ExchangeWallet exchangeWallet) {
//        return success(this.exchangeWalletService.updateById(exchangeWallet));
//    }

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
//        return success(this.exchangeWalletService.removeByIds(idList));
//    }
}