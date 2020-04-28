package com.spark.bitrade.controller;

import com.spark.bitrade.constant.LockStatus;
import com.spark.bitrade.constant.LockType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.constants.LockMsgCode;
import com.spark.bitrade.entity.LockCoinActivitieSetting;
import com.spark.bitrade.entity.LockCoinDetail;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.service.ICoinExchange;
import com.spark.bitrade.service.LockCoinActivitieSettingService;
import com.spark.bitrade.service.LockCoinDetailService;
import com.spark.bitrade.service.LockSlpCoinDetailService;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("v2/lockSlp")
@Api(description = "锁仓活动控制层")
public class LockSlpController extends ApiController {
    @Autowired
    private ICoinExchange coinExchange;
    @Autowired
    private LockSlpCoinDetailService lockSlpCoinDetailService;
    @Resource
    private LockCoinActivitieSettingService lockCoinActivitieSettingService;
    @Autowired
    private LockCoinDetailService lockCoinDetailService;

    /**
     * 参加SLP布朗计划活动
     *
     * @param id                   活动配置id
     * @param payCoinUnit          支付币种
     * @param payCoinUnitUsdtPrice 支付币种USDT汇率
     * @param jyPassword           可选，资金密码
     * @param limitCountValid      可选，当前有效的活动数量限制
     * @param limitCountInDay      可选，当日参与活动的次数限制（不考虑是否有效）
     * @return 锁仓信息
     * @author yangch
     * @time 2019-06-20 17:04:49
     */
    @ApiOperation(value = "参加SLP布朗计划活动接口", notes = "参加SLP布朗计划活动接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "活动配置id", name = "id", dataType = "int", required = true),
            @ApiImplicitParam(value = "支付币种", name = "payCoinUnit", dataType = "String"),
            @ApiImplicitParam(value = "支付币种USDT汇率", name = "payCoinUnitUsdtPrice", dataType = "double"),
            @ApiImplicitParam(value = "资金密码", name = "jyPassword", dataType = "String"),
            @ApiImplicitParam(value = "当前有效的活动数量限制", name = "limitCountValid", dataType = "int"),
            @ApiImplicitParam(value = "当日参与活动的次数限制", name = "limitCountInDay", dataType = "int")
    })
    @PostMapping("/lock")
    public MessageRespResult<LockCoinDetail> lock(@MemberAccount Member member,
                                                  Long id,
                                                  String payCoinUnit,
                                                  BigDecimal payCoinUnitUsdtPrice,
                                                  String jyPassword,
                                                  Integer limitCountValid,
                                                  Integer limitCountInDay) {
        log.info("【锁仓开始】------------------------------->memberId={}, 活动配置id={}",
                member.getId(), id);
        AssertUtil.notNull(id, CommonMsgCode.INVALID_PARAMETER);
        LockCoinActivitieSetting lockCoinActivitieSetting = lockCoinActivitieSettingService.findOneByTime(id);
        AssertUtil.notNull(lockCoinActivitieSetting, LockMsgCode.NOT_HAVE_SET);
        //使用配置的购买数量
        BigDecimal amount = lockCoinActivitieSetting.getMinBuyAmount();

        //USDT对CNY的价格 测试数据
//        BigDecimal usdt2CnyPrice = new BigDecimal("10");
        BigDecimal usdt2CnyPrice = coinExchange.getUsdCnyRate().getData();

        LockCoinDetail lockCoinDetail = lockSlpCoinDetailService.lockSlpCoin(member,
                lockCoinActivitieSetting, amount, payCoinUnit, payCoinUnitUsdtPrice,
                usdt2CnyPrice, jyPassword, limitCountValid, limitCountInDay);

        log.info("【锁仓结束】------------------------------->memberId={}, 活动配置id={},lockId={}",
                member.getId(), id, lockCoinDetail.getId());

        return MessageRespResult.success4Data(lockCoinDetail);
    }

    /**
     * 升级SLP布朗计划套餐活动
     *
     * @param id                   活动配置id
     * @param payCoinUnit          支付币种
     * @param payCoinUnitUsdtPrice 支付币种USDT汇率
     * @param jyPassword           可选，资金密码
     * @return 锁仓信息
     * @author yangch
     * @time 2019-07-25 16:11:18
     */
    @ApiOperation(value = "升级SLP布朗计划套餐活动接口", notes = "升级SLP布朗计划套餐活动接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "活动配置id", name = "id", dataType = "int", required = true),
            @ApiImplicitParam(value = "支付币种", name = "payCoinUnit", dataType = "String"),
            @ApiImplicitParam(value = "支付币种USDT汇率", name = "payCoinUnitUsdtPrice", dataType = "double"),
            @ApiImplicitParam(value = "资金密码", name = "jyPassword", dataType = "String")
    })
    @PostMapping("/upgradeSlpPackage")
    public MessageRespResult<List<LockCoinDetail>> upgradeSlpPackage(@MemberAccount Member member,
                                                                     Long id,
                                                                     String payCoinUnit,
                                                                     BigDecimal payCoinUnitUsdtPrice,
                                                                     String jyPassword) {
        log.info("【升仓开始】------------------------------->memberId={}, 活动配置id={}",
                member.getId(), id);
        AssertUtil.notNull(id, CommonMsgCode.INVALID_PARAMETER);
        LockCoinActivitieSetting lockCoinActivitieSetting = lockCoinActivitieSettingService.findOneByTime(id);
        AssertUtil.notNull(lockCoinActivitieSetting, LockMsgCode.NOT_HAVE_SET);

        //获取待升仓的记录
        List<LockCoinDetail> lst = lockCoinDetailService.list(member.getId(), LockType.LOCK_SLP, LockStatus.LOCKED);
        AssertUtil.notNull(lst, LockMsgCode.NOT_SLP_RECORD);
        AssertUtil.isTrue(lst.size() != 0, LockMsgCode.NOT_SLP_RECORD);
        AssertUtil.isTrue(lst.size() == 1, LockMsgCode.MULTIPLE_SLP_RECORD);

        //使用配置的购买数量
        BigDecimal amount = lockCoinActivitieSetting.getMinBuyAmount();

        //USDT对CNY的价格 测试数据
        //BigDecimal usdt2CnyPrice = new BigDecimal("5");
        BigDecimal usdt2CnyPrice = coinExchange.getUsdCnyRate().getData();

        LockCoinDetail lockCoinDetail = lockSlpCoinDetailService.upgradeSlpPackage(member,
                lockCoinActivitieSetting, amount, payCoinUnit, payCoinUnitUsdtPrice,
                usdt2CnyPrice, jyPassword, lst.get(0));

        log.info("【升仓结束】------------------------------->memberId={}, 活动配置id={},lockId={}",
                member.getId(), id, lockCoinDetail.getId());
        lst.add(lockCoinDetail);

        return MessageRespResult.success4Data(lst);
    }

}
