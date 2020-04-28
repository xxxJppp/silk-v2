package com.spark.bitrade.controller;

//import com.codingapi.txlcn.tc.annotation.LcnTransaction;

import com.spark.bitrade.constant.LockStatus;
import com.spark.bitrade.constant.LockType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.constants.LSMsgCode;
import com.spark.bitrade.constants.LockSlpConstant;
import com.spark.bitrade.dto.Kline;
import com.spark.bitrade.dto.SlpActivitieSetting;
import com.spark.bitrade.dto.SlpMylockinfo;
import com.spark.bitrade.entity.*;
import com.spark.bitrade.mq.BuildTaskMessage;
import com.spark.bitrade.mq.BuildTaskMessageType;
import com.spark.bitrade.mq.TaskMessageWrapper;
import com.spark.bitrade.service.ILockService;
import com.spark.bitrade.service.IMemberApiService;
import com.spark.bitrade.service.ISilkDataDistApiService;
import com.spark.bitrade.service.SlpExchangeRateService;
import com.spark.bitrade.util.*;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import com.spark.bitrade.web.resubmit.annotation.ForbidResubmit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 *  slp模式锁仓活动
 *
 * @author young
 * @time 2019.06.22 15:50
 */
@RestController
@RequestMapping("api/v2/lockSlp")
@Api(description = "slp模式锁仓活动控制层")
@Slf4j
public class LockSlpController extends ApiController {
    @Autowired
    private ILockService lockService;

    @Autowired
    private ISilkDataDistApiService silkDataDistApiService;

    @Autowired
    private SlpExchangeRateService slpExchangeRateService;

    @Autowired
    private IMemberApiService iMemberApiService;

    @Autowired
    private TaskMessageWrapper taskMessageWrapper;


    /**
     * 套餐列表
     *
     * @return
     */
    @ApiOperation(value = "SLP套餐列表接口", notes = "SLP套餐列表接口")
    @PostMapping({"/listActivities", "/no-auth/listActivities"})
    public MessageRespResult<List<SlpActivitieSetting>> listActivities(@ApiIgnore LockCoinDetail lockCoinDetail) {
        //获取 模式活动-活动方案配置id
        MessageRespResult<SilkDataDist> result1 = silkDataDistApiService.findOne("LOCK_SLP_ACTIVITY", "PROJECT_ID");
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(result1);
        Long projectId = SilkDataDistUtils.getVal(result1.getData(), Long.class, 0L);

        //获取 模式活动-锁仓收益倍数
        MessageRespResult<SilkDataDist> result2 = silkDataDistApiService.findOne("LOCK_SLP_ACTIVITY", "EARNING_SCALE");
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(result2);
        BigDecimal earningScale = SilkDataDistUtils.getVal(result2.getData(), BigDecimal.class, BigDecimal.ZERO);

        //活动支付币种
        String payCoinSymbol = this.getPayCoinUnit();

        //活动参与币种汇率，参考USDT交易区的昨日最高价
        //BigDecimal payCoinSymbolRate = new BigDecimal("4"); //测试数据
        BigDecimal payCoinSymbolRate = slpExchangeRateService.exchangeUsdtRate(payCoinSymbol);

        //按活动方案ID 获取活动列表
        MessageRespResult<List<LockCoinActivitieSetting>> respResult = lockService.findListByTime(projectId);
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(respResult);

        //转换为 slp的活动 信息
        List<SlpActivitieSetting> lst = new ArrayList<>(respResult.getData().size());
        respResult.getData().forEach(e -> {
            SlpActivitieSetting.Type type;
            if (lockCoinDetail == null || lockCoinDetail.getTotalAmount() == null) {
                //可以参与
                type = SlpActivitieSetting.Type.JOIN;
            } else {
                if (BigDecimalUtil.lte0(e.getMinBuyAmount().subtract(lockCoinDetail.getTotalAmount()))) {
                    //不能参与
                    type = SlpActivitieSetting.Type.NONE;
                } else {
                    //可以升仓
                    type = SlpActivitieSetting.Type.UPGRADE;
                }
            }

            lst.add(SlpActivitieSetting.convert(e, earningScale, payCoinSymbol,
                    BigDecimalUtil.div2up(e.getMinBuyAmount(), payCoinSymbolRate, 8), type));
        });

        //按活动ID自然排序
        lst.sort(Comparator.comparing(SlpActivitieSetting::getId));
        return this.success(lst);
    }

    /**
     * 登录后获取的套餐列表
     *
     * @return
     */
    @ApiOperation(value = "登录后获取的套餐列表接口", notes = "登录后获取的套餐列表接口")
    @PostMapping({"/listActivities2"})
    public MessageRespResult<List<SlpActivitieSetting>> listActivities2() {
        return this.listActivities(this.myLockCoinDetail());
    }

    /**
     * 获取参与信息
     *
     * @return
     */
    @ApiOperation(value = "登录后获取参与信息接口", notes = "登录后获取参与信息接口")
    @PostMapping({"/slpMylockinfo"})
    public MessageRespResult<SlpMylockinfo> slpMylockinfo() {
        LockCoinDetail lockCoinDetail = this.myLockCoinDetail();
        if (lockCoinDetail != null) {
            MessageRespResult<LockCoinActivitieSetting> respResult =
                    lockService.findLockSettingById(lockCoinDetail.getRefActivitieId());
            if (respResult.isSuccess() && respResult.getData() != null) {
                return MessageRespResult.success4Data(SlpMylockinfo.convert(lockCoinDetail, respResult.getData()));
            }
        }

        return MessageRespResult.success4Data(SlpMylockinfo.builder().build());
    }

    /**
     * 昨日K线
     *
     * @param symbol 交易对
     * @return
     */
    @PostMapping("/yesterdayKline")
    public MessageRespResult<Kline> yesterdayKline(String symbol) {
        return MessageRespResult.success4Data(slpExchangeRateService.yesterdayKline(symbol));
    }

    /**
     * slp昨日最高价格
     *
     * @param symbol 交易对
     * @return
     */
    @ApiOperation(value = "slp昨日最高价格汇率接口", notes = "slp昨日最高价格汇率接口")
    @PostMapping("/yesterdayHighestPrice")
    public MessageRespResult<BigDecimal> exchangeRate4Yesterday(String symbol) {
        return MessageRespResult.success4Data(slpExchangeRateService.exchangeRate4Yesterday(symbol));
    }


    /**
     * 用户参加SLP锁仓活动
     * 备注：使用USDT 参与活动
     *
     * @param id         活动配置id
     * @param jyPassword 资金密码
     * @author yangch
     * @time 2019-06-20 17:04:49
     */
    @ApiOperation(value = "用户参加SLP锁仓活动接口", notes = "用户参加SLP锁仓活动接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "活动配置id", name = "id", dataType = "int", required = true),
            @ApiImplicitParam(value = "资金密码", name = "jyPassword", dataType = "String", required = true)
    })
    @PostMapping("/lockSlp")
    @ForbidResubmit
    public MessageRespResult<LockCoinDetail> lockSlp1(@MemberAccount Member member, Long id, String jyPassword) {
        log.info("参与Slp布朗活动--开始：memberId={}, 活动id={}",
                member.getId(), id);

        AssertUtil.notNull(id, CommonMsgCode.INVALID_PARAMETER);
        AssertUtil.hasText(jyPassword, CommonMsgCode.MISSING_JYPASSWORD);

        //校验推荐关系，必须有推荐人才能参与活动
        MessageRespResult<SlpMemberPromotion> promotionMessageRespResult = iMemberApiService.getSlpMemberPromotion(member.getId());
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(promotionMessageRespResult);
        AssertUtil.notNull(promotionMessageRespResult.getData(), LSMsgCode.NOT_FIND_SLP_PROMOTION);
        AssertUtil.notNull(promotionMessageRespResult.getData().getInviterId(), LSMsgCode.NOT_FIND_SLP_PROMOTION);
        AssertUtil.isTrue(promotionMessageRespResult.getData().getInviterId() != 0, LSMsgCode.NOT_FIND_SLP_PROMOTION);

        //当前有效的活动数量限制，从系统配置中获取
        MessageRespResult<SilkDataDist> result1 = silkDataDistApiService.findOne("LOCK_SLP_ACTIVITY", "LIMIT_COUNT_VALID");
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(result1);
        int limitCountValid = SilkDataDistUtils.getVal(result1.getData(), Integer.class, 1);

        // 当日参与活动的次数限制，从系统配置中获取
        MessageRespResult<SilkDataDist> result2 = silkDataDistApiService.findOne("LOCK_SLP_ACTIVITY", "LIMIT_COUNT_IN_DAY");
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(result2);
        int limitCountInDay = SilkDataDistUtils.getVal(result2.getData(), Integer.class, 10);

        //活动参与币种
        String payCoinUnit = this.getPayCoinUnit();

        //活动参与币种汇率，参考USDT交易区的昨日最高价
//        BigDecimal payCoinUnitRate = new BigDecimal("10"); //测试数据
        //BigDecimal payCoinUnitRate = this.slpExchangeRateService.exchangeRate4Yesterday(payCoinUnit.concat("/USDT"));
        BigDecimal payCoinUnitRate = slpExchangeRateService.exchangeUsdtRate(payCoinUnit);
        AssertUtil.isTrue(BigDecimalUtil.gt0(payCoinUnitRate), LSMsgCode.INVALID_EXCHANGE_RATE);

        //调用 lock2 模块的 锁仓接口无上级推荐人
        MessageRespResult<LockCoinDetail> resultLockDetail = lockService.lockSlp(getApiKey(), id,
                payCoinUnit, payCoinUnitRate, jyPassword, limitCountValid, limitCountInDay);
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(resultLockDetail);

        //发送kafka消息
        sendMqlockSlp(resultLockDetail.getData().getId());


        log.info("参与Slp布朗活动--结束：memberId={}, id={}, LockCoinDetail={}",
                member.getId(), id, resultLockDetail);
        return resultLockDetail;
    }


    /**
     * 用户升级SLP套餐活动
     * 备注：使用USDT 参与活动
     *
     * @param id         活动配置id
     * @param jyPassword 资金密码
     * @author yangch
     * @time 2019-07-25 11:46:14
     */
    @ApiOperation(value = "用户升级SLP套餐活动接口", notes = "用户升级SLP套餐活动接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "活动配置id", name = "id", dataType = "int", required = true),
            @ApiImplicitParam(value = "资金密码", name = "jyPassword", dataType = "String", required = true)
    })
    @PostMapping("/upgradeSlpPackage")
    @ForbidResubmit
    public MessageRespResult<LockCoinDetail> upgradeSlpPackage(@MemberAccount Member member, Long id, String jyPassword) {
        log.info("升级Slp锁仓套餐活动--开始：memberId={}, 活动id={}",
                member.getId(), id);

        AssertUtil.notNull(id, CommonMsgCode.INVALID_PARAMETER);
        AssertUtil.hasText(jyPassword, CommonMsgCode.MISSING_JYPASSWORD);

        //活动参与币种
        String payCoinUnit = this.getPayCoinUnit();

        //活动参与币种汇率，参考USDT交易区的昨日最高价
        //BigDecimal payCoinUnitRate = new BigDecimal("30"); // 测试数据
        BigDecimal payCoinUnitRate = slpExchangeRateService.exchangeUsdtRate(payCoinUnit);
        AssertUtil.isTrue(BigDecimalUtil.gt0(payCoinUnitRate), LSMsgCode.INVALID_EXCHANGE_RATE);

        //调用 lock2 模块的
        MessageRespResult<List<LockCoinDetail>> resultLockDetail = lockService.upgradeSlpPackage(getApiKey(), id,
                payCoinUnit, payCoinUnitRate, jyPassword);
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(resultLockDetail);

        //发送kafka消息
        sendMqUpgradeSlpPackage(resultLockDetail.getData().get(0).getId(),
                resultLockDetail.getData().get(1).getId());

        log.info("升级Slp锁仓套餐活动--结束：memberId={}, id={}, LockCoinDetail={}",
                member.getId(), id, resultLockDetail.getData().get(1));
        return MessageRespResult.success4Data(resultLockDetail.getData().get(1));
    }

    /**
     * 发送消息
     *
     * @param id 锁仓记录ID
     * @return
     */
    public void sendMqlockSlp(Long id) {
        BuildTaskMessage msg = new BuildTaskMessage();
        msg.setRefId(String.valueOf(id));
        msg.setType(BuildTaskMessageType.LOCK_SLP_TASK);
        //修改为延迟推送任务
        taskMessageWrapper.dispatch(LockSlpConstant.KAFKA_MSG_BUILD_TASK, msg.stringify(), 2);
        //kafkaTemplate.send(LockSlpConstant.KAFKA_MSG_BUILD_TASK, msg.stringify());
    }

    /**
     * 发送套餐升级消息
     *
     * @param oldId 升仓前的锁仓记录ID
     * @param newId 升仓后的锁仓记录ID
     * @return
     */
    public void sendMqUpgradeSlpPackage(Long oldId, Long newId) {
        BuildTaskMessage msg = new BuildTaskMessage();
        msg.setRefId(new StringBuilder("[").append(oldId).append(",").append(newId).append("]").toString());
        msg.setType(BuildTaskMessageType.LOCK_SLP_PRE_APPEND_TASK);
        //修改为延迟推送任务
        taskMessageWrapper.dispatch(LockSlpConstant.KAFKA_MSG_BUILD_TASK, msg.stringify(), 3);
    }

    @ApiOperation(value = "SLP锁仓ID重做接口", notes = "SLP锁仓ID重做接口")
    @ApiImplicitParam(value = "锁仓活动id", name = "id", dataType = "int", required = true)
    @RequestMapping(value = {"sendMqRedo", "/no-auth/sendMqRedo"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ForbidResubmit
    public MessageRespResult<String> sendMqRedo(Long id) {
        AssertUtil.notNull(id, CommonMsgCode.INVALID_PARAMETER);
        MessageRespResult<LockCoinDetail> respResult = lockService.findLockCoinDetailById(id);
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(respResult);
        if (respResult.getData() != null && respResult.getData().getType() == LockType.LOCK_SLP) {
            log.info("SLP锁仓重做:id={},状态={}", id, respResult.getData().getLockRewardSatus());
            this.sendMqlockSlp(id);
        } else {
            log.info("未获取到锁仓的记录，id={}", id);
        }

        return MessageRespResult.success();
    }

    /**
     * 获取锁仓信息
     *
     * @return
     */
    private LockCoinDetail myLockCoinDetail() {
        MessageRespResult<List<LockCoinDetail>> respResult =
                lockService.listAll(getApiKey(), LockType.LOCK_SLP, LockStatus.LOCKED);
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(respResult);
        if (respResult.getData() != null && respResult.getData().size() > 0) {
            //注意,SLP布朗计划每次只有一个活动，如有多个活动需要调整此处
            return respResult.getData().get(0);
        } else {
            return null;
        }
    }

    /**
     * 活动参与币种
     *
     * @return
     */
    private String getPayCoinUnit() {
        MessageRespResult<SilkDataDist> result3 = silkDataDistApiService.findOne("LOCK_SLP_ACTIVITY", "HOLD_COIN_SYMBOL");
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(result3);
        return SilkDataDistUtils.getVal(result3.getData(), String.class, "SLP");
    }
}
