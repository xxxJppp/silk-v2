package com.spark.bitrade.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spark.bitrade.constant.LockStatus;
import com.spark.bitrade.constant.LockType;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.LockCoinActivitieSetting;
import com.spark.bitrade.entity.LockCoinDetail;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.service.ICoinExchange;
import com.spark.bitrade.service.LockCoinDetailService;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.BigDecimalUtil;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("v2/lock")
@Api(description = "锁仓活动控制层")
public class LockController extends ApiController {
    @Autowired
    private ICoinExchange coinExchange;
    @Autowired
    private LockCoinDetailService lockCoinDetailService;


    /**
     * 用户参加锁仓活动
     *
     * @param lockType        活动类型
     * @param id              活动配置id
     * @param amount          购买数量
     * @param jyPassword      可选，资金密码
     * @param limitCountValid 可选，当前有效的活动数量限制
     * @param limitCountInDay 可选，当日参与活动的次数限制（不考虑是否有效）
     * @return 锁仓信息
     * @author yangch
     * @time 2019-06-20 17:04:49
     */
    @ApiOperation(value = "用户参加锁仓活动接口", notes = "用户参加锁仓活动接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "锁仓类型", name = "lockType", allowableValues = "STO", required = true),
            @ApiImplicitParam(value = "活动配置id", name = "id", dataType = "String", required = true),
            @ApiImplicitParam(value = "购买数量", name = "amount", dataType = "String", required = true),
            @ApiImplicitParam(value = "资金密码", name = "jyPassword", dataType = "String"),
            @ApiImplicitParam(value = "当前有效的活动数量限制", name = "limitCountValid", dataType = "int"),
            @ApiImplicitParam(value = "当日参与活动的次数限制", name = "limitCountInDay", dataType = "int")
    })
    @PostMapping("/lockVerify")
    @Deprecated
    public MessageRespResult<LockCoinActivitieSetting> lockVerify(@MemberAccount Member member,
                                                                  LockType lockType,
                                                                  Long id, BigDecimal amount,
                                                                  String jyPassword,
                                                                  Integer limitCountValid,
                                                                  Integer limitCountInDay) {
        return success(lockCoinDetailService.lockVerify(member, lockType, id, amount, amount,
                jyPassword, limitCountValid, limitCountInDay));
    }

    /**
     * 用户参加锁仓活动
     *
     * @param lockType        活动类型
     * @param id              活动配置id
     * @param amount          活动参与份数
     * @param boughtAmount    可选，购买数量（一般和amount是一样的，如活动币种和参与活动币种不一样时，可能就不一样）
     * @param payCoinPrice    可选，支付币种USDT汇率
     * @param jyPassword      可选，资金密码
     * @param limitCountValid 可选，当前有效的活动数量限制
     * @param limitCountInDay 可选，当日参与活动的次数限制（不考虑是否有效）
     * @return 锁仓信息
     * @author yangch
     * @time 2019-06-20 17:04:49
     */
    @ApiOperation(value = "用户参加锁仓活动接口", notes = "用户参加锁仓活动接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "锁仓类型", name = "lockType", allowableValues = "STO", required = true),
            @ApiImplicitParam(value = "活动配置id", name = "id", dataType = "String", required = true),
            @ApiImplicitParam(value = "活动参与份数", name = "amount", dataType = "String", required = true),
            @ApiImplicitParam(value = "购买数量", name = "boughtAmount", dataType = "String"),
            @ApiImplicitParam(value = "支付币种USDT汇率", name = "payCoinPrice", dataType = "String"),
            @ApiImplicitParam(value = "资金密码", name = "jyPassword", dataType = "String"),
            @ApiImplicitParam(value = "当前有效的活动数量限制", name = "limitCountValid", dataType = "int"),
            @ApiImplicitParam(value = "当日参与活动的次数限制", name = "limitCountInDay", dataType = "int")
    })
    @PostMapping("/lockWithPassword")
    @Deprecated
    public MessageRespResult<LockCoinDetail> lockWithPassword(@MemberAccount Member member,
                                                              LockType lockType,
                                                              Long id, BigDecimal amount,
                                                              BigDecimal boughtAmount,
                                                              BigDecimal payCoinPrice,
                                                              String jyPassword,
                                                              Integer limitCountValid,
                                                              Integer limitCountInDay) {
        log.info("【锁仓开始】------------------------------->memberId={}, lockType={}, amount={},boughtAmount={}",
                member.getId(), lockType, amount, boughtAmount);
        if (BigDecimalUtil.lte0(boughtAmount)) {
            boughtAmount = amount;
        }
        AssertUtil.notNull(id, CommonMsgCode.INVALID_PARAMETER);
        LockCoinActivitieSetting lockCoinActivitieSetting =
                lockCoinDetailService.lockVerify(member, lockType, id, amount, boughtAmount,
                        jyPassword, limitCountValid, limitCountInDay);

        //锁仓的USDT价格（注意：如IEO场景，可能没有汇率）
        BigDecimal activityCoinLockPrice = payCoinPrice;
        if (BigDecimalUtil.lte0(activityCoinLockPrice)) {
            activityCoinLockPrice = coinExchange.getUsdExchangeRate(lockCoinActivitieSetting.getCoinSymbol()).getData();
        }
        //USDT对CNY的价格
        BigDecimal usdt2CnyPrice = coinExchange.getUsdCnyRate().getData();


        //分布式事务 处理锁仓记录及用户的账
        LockCoinDetail lockCoinDetail = lockCoinDetailService.lockCoin(member,
                lockCoinActivitieSetting, amount, boughtAmount,
                activityCoinLockPrice, usdt2CnyPrice, lockType);

        log.info("【锁仓结束】------------------------------->memberId={}, lockType={}, amount={},lockId={}",
                member.getId(), lockType, amount, lockCoinDetail.getId());

        return MessageRespResult.success4Data(lockCoinDetail);
    }

    /**
     * 获取参与记录
     *
     * @param member
     * @param lockType   活动类型
     * @param lockStatus 锁仓状态
     * @return
     */
    @ApiOperation(value = "获取参与记录接口", notes = "获取参与记录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "锁仓类型", name = "lockType", allowableValues = "STO", required = true),
            @ApiImplicitParam(value = "锁仓状态", name = "lockStatus", allowableValues = "LOCKED", required = true)
    })
    @PostMapping("/listAll")
    public MessageRespResult<List<LockCoinDetail>> listAll(@MemberAccount Member member, LockType lockType, LockStatus lockStatus) {
        return MessageRespResult.success4Data(lockCoinDetailService.list(member.getId(), lockType, lockStatus));
    }
    
    
    
    
    
    
    

    @ApiOperation(value = "无收益返还型简单锁仓", notes = "无收益返还型简单锁仓接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "锁仓类型", name = "lockType", required = true),
            @ApiImplicitParam(value = "交易类型", name = "txsType", required = true),
            @ApiImplicitParam(value = "锁仓数量", name = "amount", dataType = "String", required=true),
            @ApiImplicitParam(value = "锁仓币种", name = "unit", dataType = "String", required=true),
            @ApiImplicitParam(value = "锁仓期限(天)", name = "lockDay", dataType = "int", required=true)
    })
    @PostMapping("/simplelock")
    public MessageRespResult<LockCoinDetail> simplelock(@MemberAccount Member member,
                                                              LockType lockType,
                                                              TransactionType txsType,
                                                              BigDecimal amount,
                                                              String unit,
                                                              int lockDay,
                                                              Long lockId,
                                                              Long operType) {
        
        LockCoinDetail lockCoinDetail = lockCoinDetailService.simplelock(member, lockType, txsType,amount, unit,lockDay, lockId, operType);


        return MessageRespResult.success4Data(lockCoinDetail);
    }

}
