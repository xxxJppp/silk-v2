package com.spark.bitrade.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.MemberWallet;
import com.spark.bitrade.entity.WalletChangeRecord;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.service.CoinService;
import com.spark.bitrade.service.IWalletExchangeService;
import com.spark.bitrade.service.IWalletTradeService;
import com.spark.bitrade.service.MemberWalletService;
import com.spark.bitrade.trans.WalletExchangeEntity;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.vo.MemberWalletVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.spark.bitrade.util.*;
import com.spark.bitrade.controller.ApiController;
import io.swagger.annotations.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * 会员钱包账户操作控制层
 * 备注：仅为内部服务
 *
 * @author yangch
 * @since 2019-06-15 16:14:18
 */
@RestController
@RequestMapping("v2/memberWallet")
@Api(description = "会员钱包账户控制层")
public class MemberWalletController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private MemberWalletService memberWalletService;
    @Autowired
    private IWalletTradeService walletTradeService;
    @Resource
    private CoinService coinService;
    @Autowired
    private IWalletExchangeService walletExchangeService;

    /**
     * 个人钱包账户资金变动接口，如个人账户的平账、冻结、解冻等
     * 注意：提供分布式事务
     *
     * @param tradeEntity 交易信息
     * @return
     */
    @PostMapping("/trade")
    public MessageRespResult<Boolean> trade(@RequestBody WalletTradeEntity tradeEntity) {
        AssertUtil.notNull(tradeEntity, CommonMsgCode.INVALID_PARAMETER);
        AssertUtil.notNull(tradeEntity.getCoinUnit(), CommonMsgCode.INVALID_PARAMETER);
        if (StringUtils.isEmpty(tradeEntity.getCoinId())) {
            tradeEntity.setCoinId(coinService.findByUnit(tradeEntity.getCoinUnit()).getName());
        }
        return MessageRespResult.success4Data(this.walletTradeService.trade(tradeEntity));
    }

    /**
     * tcc try预留业务资源接口
     * 备注：预处理账的可用数量（try处理时需要将可用资金放到到冻结资金里，冻结和锁仓资金不做特殊处理），记录资产变更流水记录
     * 1）交易可用金额
     * 1.1）交易的可用金额大于0，预处理时，放到 冻结资金里
     * 1.2）交易的可用金额小于0，预处理时，先从 可用资金 中减去交易的可用金额，同时 放到 冻结资金里
     * 2）资产变更流水记录
     * 2.1）tcc状态，默认为 1=try
     *
     * @param tradeEntity 交易实体信息
     * @return
     * @throws MessageCodeException
     */
    @PostMapping("/tradeTccTry")
    public MessageRespResult<WalletChangeRecord> tradeTccTry(@RequestBody WalletTradeEntity tradeEntity) {
        AssertUtil.notNull(tradeEntity, CommonMsgCode.INVALID_PARAMETER);
        AssertUtil.notNull(tradeEntity.getCoinUnit(), CommonMsgCode.INVALID_PARAMETER);
        if (StringUtils.isEmpty(tradeEntity.getCoinId())) {
            tradeEntity.setCoinId(coinService.findByUnit(tradeEntity.getCoinUnit()).getName());
        }
        return MessageRespResult.success4Data(this.walletTradeService.tradeTccTry(tradeEntity));
    }

    /**
     * tcc Confirm 确认执行业务操作
     * 备注：处理冻结资金，修改流水记录状态，新增用户资产流水记录
     * 1）冻结资金
     * 1.1）交易的可用金额大于0，确认执行业务时，减少 冻结资产 同时 添加 可用资产
     * 1.2）交易的可用金额小于0，确认执行业务时，减少 冻结资产
     * 2）修改 资产变更流水记录 tcc状态 为 2=confirm
     * 3）保存 用户资产流水记录
     *
     * @param memberId             用户ID
     * @param walletChangeRecordId 资产变更流水记录ID
     * @return
     * @throws MessageCodeException
     */
    @PostMapping("/tradeTccConfirm")
    public MessageRespResult<Boolean> tradeTccConfirm(@RequestParam("memberId") Long memberId,
                                             @RequestParam("walletChangeRecordId") Long walletChangeRecordId) {
        return MessageRespResult.success4Data(this.walletTradeService.tradeTccConfirm(memberId, walletChangeRecordId));
    }

    /**
     * tcc 取消执行业务操作
     * 备注：数据还原，修改资金变更流水记录状态
     * 1）交易的 可用资金、冻结资金、锁仓资金正常还回
     * 2）修改 资产变更流水记录 tcc状态 为 3=cancel
     *
     * @param memberId             用户ID
     * @param walletChangeRecordId 资产变更流水记录ID
     * @return
     * @throws MessageCodeException
     */
    @PostMapping("/tradeTccCancel")
    public MessageRespResult<Boolean> tradeTccCancel(@RequestParam("memberId") Long memberId,
                                            @RequestParam("walletChangeRecordId") Long walletChangeRecordId) {
        return MessageRespResult.success4Data(this.walletTradeService.tradeTccCancel(memberId, walletChangeRecordId));
    }


    /**
     * 个人币种兑换接口，如币币交易、闪兑等
     * 注意：提供分布式事务
     *
     * @param exchangeEntity 兑换信息
     * @return
     */
    @PostMapping("/exchange")
    public MessageRespResult<Boolean> exchange(@RequestBody WalletExchangeEntity exchangeEntity) {
        AssertUtil.notNull(exchangeEntity, CommonMsgCode.INVALID_PARAMETER);
        AssertUtil.notNull(exchangeEntity.getSource().getCoinUnit(), CommonMsgCode.INVALID_PARAMETER);
        AssertUtil.notNull(exchangeEntity.getTarget().getCoinUnit(), CommonMsgCode.INVALID_PARAMETER);

        if (StringUtils.isEmpty(exchangeEntity.getSource().getCoinId())) {
            exchangeEntity.getSource().setCoinId(coinService.findByUnit(exchangeEntity.getSource().getCoinUnit()).getName());
        }

        if (StringUtils.isEmpty(exchangeEntity.getTarget().getCoinId())) {
            exchangeEntity.getTarget().setCoinId(coinService.findByUnit(exchangeEntity.getTarget().getCoinUnit()).getName());
        }

        return MessageRespResult.success4Data(this.walletExchangeService.exchange(exchangeEntity));
    }


    /**
     * 分页查询所有数据
     *
     * @param size         分页.每页数量
     * @param current      分页.当前页码
     * @param memberWallet 查询实体
     * @return 所有数据
     */
    @ApiOperation(value = "分页查询所有数据接口", notes = "分页查询所有数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "分页.每页数量。eg：10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "分页.当前页码.eg：从1开始", name = "current", defaultValue = "1", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "实体对象", name = "memberWallet", dataTypeClass = MemberWallet.class)
    })
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<IPage<MemberWallet>> list(Integer size, Integer current, MemberWallet memberWallet) {
        return success(this.memberWalletService.page(new Page<>(current, size), new QueryWrapper<>(memberWallet)));
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
    public MessageRespResult<MemberWallet> get(@RequestParam("id") Serializable id) {
        return success(this.memberWalletService.getById(id));
    }


    /**
     * 通过用户id和币种id进行查询
     *
     * @param memberId
     * @param coinId
     * @return true
     * @author shenzucai
     * @time 2019.07.03 20:40
     */
    @ApiOperation(value = "通过用户id和币种id进行查询", notes = "通过用户id和币种id进行查询")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户id", name = "memberId", dataTypeClass = Long.class, required = true),
            @ApiImplicitParam(value = "币种id列表", name = "coinId", dataTypeClass = String.class, required = true)
    })
    @RequestMapping(value = "/getWallet", method = {RequestMethod.GET})
    public MessageRespResult<MemberWallet> getWalletByCoinId(@RequestParam("memberId") Long memberId, @RequestParam("coinId") String coinId) {
        return success(memberWalletService.findByCoinAndMemberId(coinId, memberId));
    }

    /**
     * 通过用户id和币种id进行查询
     *
     * @param memberId
     * @param unit
     * @return true
     * @author shenzucai
     * @time 2019.07.03 20:40
     */
    @ApiOperation(value = "通过用户id和币种id进行查询", notes = "通过用户id和币种id进行查询")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户id", name = "memberId", dataTypeClass = Long.class, required = true),
            @ApiImplicitParam(value = "币种", name = "unit", dataTypeClass = String.class, required = true)
    })
    @RequestMapping(value = "/getWalletByUnit", method = {RequestMethod.GET})
    public MessageRespResult<MemberWallet> getWalletByUnit(@RequestParam("memberId") Long memberId, @RequestParam("unit") String unit) {
        return success(memberWalletService.findByUnitAndMemberId(unit, memberId));
    }
//    /**
//     * 新增数据
//     *
//     * @param memberWallet 实体对象
//     * @return 新增结果
//     */
//    @ApiOperation(value = "新增数据接口", notes = "新增数据接口")
//    @ApiImplicitParam(value = "实体对象", name = "memberWallet", dataTypeClass = MemberWallet.class)
//    @RequestMapping(value = "/add", method = {RequestMethod.GET, RequestMethod.POST})
//    public MessageRespResult add(MemberWallet memberWallet) {
//        return success(this.memberWalletService.save(memberWallet));

//    }

//    /**
//     * 修改数据
//     *
//     * @param memberWallet 实体对象
//     * @return 修改结果
//     */
//    @ApiOperation(value = "修改数据接口", notes = "修改数据接口")
//    @ApiImplicitParam(value = "实体对象", name = "memberWallet", dataTypeClass =MemberWallet.class )
//    @RequestMapping(value = "/update", method = {RequestMethod.GET, RequestMethod.POST})
//    public MessageRespResult update(MemberWallet memberWallet) {
//        return success(this.memberWalletService.updateById(memberWallet));
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
//        return success(this.memberWalletService.removeByIds(idList));
//    }


    /**
     * 通过用户查询当前账户所有币种余额
     *
     * @param memberId
     * @return true
     * @author huyu
     * @time 2019.08.07 20:40
     */
    @ApiOperation(value = "getSupportCoinByMemberId", notes = "getSupportCoinByMemberId")
    @RequestMapping(value = "/getSupportCoinByMemberId", method = {RequestMethod.GET})
    public MessageRespResult<List<MemberWalletVo>> getSupportCoinByMemberId(@RequestParam("memberId") Long memberId) {
        return success(memberWalletService.findAllBalanceByMemberId(memberId));
    }


}