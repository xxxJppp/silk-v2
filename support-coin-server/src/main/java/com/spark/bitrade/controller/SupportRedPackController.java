package com.spark.bitrade.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.biz.SupportRedPackBizService;
import com.spark.bitrade.constant.SupportCoinMsgCode;
import com.spark.bitrade.entity.*;
import com.spark.bitrade.form.RedPackForm;
import com.spark.bitrade.param.PageParam;
import com.spark.bitrade.param.RedPackParam;
import com.spark.bitrade.param.RedRecieveParam;
import com.spark.bitrade.service.ICoinExchange;
import com.spark.bitrade.service.ISilkDataDistApiService;
import com.spark.bitrade.service.SupportOpenRedPackService;
import com.spark.bitrade.service.SupportUpCoinApplyService;
import com.spark.bitrade.util.*;
import com.spark.bitrade.vo.ApplyRedPackAuditRecordVo;
import com.spark.bitrade.vo.ApplyRedPackListVo;
import com.spark.bitrade.vo.RedPackRecieveDetailVo;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import com.spark.bitrade.web.resubmit.annotation.ForbidResubmit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 红包申请表 前端控制器
 * </p>
 *
 * @author qhliao
 * @since 2020-02-19
 */
@Api(description = "红包相关接口")
@RestController
@RequestMapping("api/v2/redPack")
@Slf4j
public class SupportRedPackController {


    @Autowired
    private SupportRedPackBizService supportRedPackBizService;
    @Resource
    private ISilkDataDistApiService dataDistApiService;
    @Resource
    private ICoinExchange coinExchange;

    /**
     * 红包开通情况接口
     *
     * @return
     */
    @PostMapping("openRedPackStatus")
    @ApiOperation(value = "红包开通状态接口", notes = "红包开通状态接口", tags = "红包接口")
    public MessageRespResult openRedPackStatus(@ApiIgnore @MemberAccount Member member) {

        SupportOpenRedPack pack = supportRedPackBizService.openRedPackStatus(member.getId());
        if (pack != null) {
            JSONObject o = new JSONObject();
            o.put("auditStatus", pack.getAuditStatus().getValue());
            o.put("auditOpinion", pack.getAuditOpinion());
            SupportRedAuditRecord openAuditRecord = supportRedPackBizService.findOpenAuditRecord(pack.getId());
            if (openAuditRecord != null) {
                o.put("payAmount", openAuditRecord.getPayAmount());
                o.put("payCoin", openAuditRecord.getPayCoin());
            }
            return MessageRespResult.success4Data(o);
        }


        return MessageRespResult.success();
    }

    @PostMapping("applyOpenRedPack")
    @ApiOperation(value = "红包开通申请", notes = "红包开通申请", tags = "红包接口")
    @ForbidResubmit
    public MessageRespResult applyOpenRedPack(@ApiIgnore @MemberAccount Member member,
                                              @RequestParam @ApiParam("支付币种") String payCoin,
                                              @RequestParam String moneyPassword) {
        SupportUtil.validatePassword(moneyPassword, member.getJyPassword(), member.getSalt());
        //验证是否能申请
        SupportOpenRedPack openRedPack = supportRedPackBizService.canApplyOpenRed(member.getId());
        //执行申请
        supportRedPackBizService.doOpenApplyRed(member.getId(), payCoin, openRedPack);

        return MessageRespResult.success();
    }

    @PostMapping("applyRedPack")
    @ApiOperation(value = "红包申请", notes = "红包申请", tags = "红包接口")
    @ForbidResubmit
    public MessageRespResult applyRedPack(@ApiIgnore @MemberAccount Member member,
                                          @Valid RedPackForm form) {
        SupportUtil.validatePassword(form.getMoneyPassword(), member.getJyPassword(), member.getSalt());
        //新增验证
        if(form.getReceiveType()==1){
            AssertUtil.isTrue(BigDecimalUtils.compare(form.getRedTotalAmount(),form.getMaxAmount()),SupportCoinMsgCode.MAX_AMOUNT_MUST_BE_LOWER_TOTAL);
        }
        supportRedPackBizService.applyRedPack(member.getId(), form);
        return MessageRespResult.success();
    }

    @PostMapping("applyRedPackList")
    @ApiOperation(value = "红包申请列表", notes = "红包申请列表", tags = "红包接口")
    public MessageRespResult<IPage<ApplyRedPackListVo>> applyRedPackList(@ApiIgnore @MemberAccount Member member, RedPackParam param) {
        IPage<ApplyRedPackListVo> page = supportRedPackBizService.applyRedPackList(member.getId(), param);
        return MessageRespResult.success4Data(page);
    }

    @PostMapping("applyRedPackAuditHistory")
    @ApiOperation(value = "红包申请审批历史", notes = "红包申请审批历史", tags = "红包接口")
    public MessageRespResult<List<ApplyRedPackAuditRecordVo>>
    applyRedPackAuditHistory(@ApiIgnore @MemberAccount Member member,
                             @RequestParam @ApiParam(required = true, value = "红包申请ID(红包申请列表返回的ID)") Long applyRedPackId) {
        //红包申请历史
        Integer type = 1;
        List<ApplyRedPackAuditRecordVo> list = supportRedPackBizService.applyAuditHistory(applyRedPackId, type);
        return MessageRespResult.success4Data(list);
    }


    @PostMapping("applyRedPackStatics")
    @ApiOperation(value = "红包领取明细", notes = "红包领取明细", tags = "红包接口")
    public MessageRespResult<IPage<RedPackRecieveDetailVo>> applyRedPackStatics(@ApiIgnore @MemberAccount Member member,
                                                                                @RequestParam @ApiParam(required = true, value = "红包申请ID(红包申请列表返回的ID)") Long applyRedPackId,
                                                                                RedRecieveParam pageParam) {
        IPage<RedPackRecieveDetailVo> page = supportRedPackBizService.applyRedPackStatics(applyRedPackId, pageParam);
        return MessageRespResult.success4Data(page);
    }


    @PostMapping("applyPriority")
    @ApiOperation(value = "红包优先级申请", notes = "红包优先级申请", tags = "红包接口")
    public MessageRespResult applyPriority(@ApiIgnore @MemberAccount Member member,
                                           @RequestParam BigDecimal addAmount,
                                           @RequestParam String moneyPassword,
                                           @RequestParam @ApiParam(required = true, value = "红包申请ID(红包申请列表返回的ID)") Long applyRedPackId) {
        SupportUtil.validatePassword(moneyPassword, member.getJyPassword(), member.getSalt());

        MessageRespResult<SilkDataDist> one = dataDistApiService.findOne("SUPPORT_RED_PACK", "PRIORITY_COIN");
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(one);
        String payCoin = one.getData().getDictVal();
        supportRedPackBizService.applyPriority(addAmount, payCoin, member.getId(), applyRedPackId);

        return MessageRespResult.success();
    }


    @PostMapping("applyPriorityAuditList")
    @ApiOperation(value = "红包优先级申请审批历史", notes = "红包优先级申请审批历史", tags = "红包接口")
    public MessageRespResult applyPriorityAuditList(@ApiIgnore @MemberAccount Member member,
                                                    @RequestParam @ApiParam(required = true, value = "红包申请ID(红包申请列表返回的ID)") Long applyRedPackId) {
        //红包优先级
        Integer type = 2;
        List<ApplyRedPackAuditRecordVo> list = supportRedPackBizService.applyAuditHistory(applyRedPackId, type);
        //支付基础金额
        MessageRespResult<SilkDataDist> one = dataDistApiService.findOne("SUPPORT_RED_PACK", "PRIORITY_COST");
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(one);
        BigDecimal baseCost = new BigDecimal(one.getData().getDictVal());
        for (ApplyRedPackAuditRecordVo vo : list) {
            vo.setBaseAmount(baseCost);
            vo.setAddAmount(vo.getPayAmount().subtract(baseCost));
        }

        return MessageRespResult.success4Data(list);
    }


    @PostMapping("no-auth/priorityPayConfig")
    @ApiOperation(value = "红包优先级支付配置", notes = "红包优先级支付配置", tags = "红包接口")
    public MessageRespResult priorityPayConfig() {
        MessageRespResult<SilkDataDist> one = dataDistApiService.findOne("SUPPORT_RED_PACK", "PRIORITY_COST");
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(one);
        BigDecimal baseCost = new BigDecimal(one.getData().getDictVal());

        MessageRespResult<SilkDataDist> two = dataDistApiService.findOne("SUPPORT_RED_PACK", "PRIORITY_COIN");
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(two);
        String payCoin = two.getData().getDictVal();

        JSONObject o = new JSONObject();
        o.put("amount", baseCost);
        o.put("coin", payCoin);
        return MessageRespResult.success4Data(o);
    }

    @PostMapping("no-auth/openRedConfig")
    @ApiOperation(value = "红包开通支付配置", notes = "红包开通支付配置", tags = "红包接口")
    public MessageRespResult openRedConfig() {

        MessageRespResult<SilkDataDist> one = dataDistApiService.findOne("SUPPORT_RED_PACK", "OPEN_COST");
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(one);
        BigDecimal cost = new BigDecimal(one.getData().getDictVal());

        MessageRespResult<SilkDataDist> two = dataDistApiService.findOne("SUPPORT_RED_PACK", "OPEN_PAY_COIN");
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(two);
        String payCoins = two.getData().getDictVal();
        String[] coins = payCoins.split(",");

        Map<String, BigDecimal> res = new HashMap<>();
        for (String coin : coins) {
            MessageRespResult<BigDecimal> usdExchangeRate = coinExchange.getUsdExchangeRate(coin);
            if (usdExchangeRate.getData().compareTo(BigDecimal.ZERO) > 0) {
                res.put(coin, cost.divide(usdExchangeRate.getData(), 8, RoundingMode.HALF_UP));
            }
        }

        return MessageRespResult.success4Data(res);
    }

    @PostMapping("no-auth/serviceChargeRate")
    @ApiOperation(value = "红包申请手续费率", notes = "红包申请手续费率", tags = "红包接口")
    public MessageRespResult serviceChargeRate() {
        MessageRespResult<SilkDataDist> one = dataDistApiService.findOne("SUPPORT_RED_PACK", "SERVICE_CHARGE");
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(one);
        BigDecimal cost = new BigDecimal(one.getData().getDictVal());

        return MessageRespResult.success4Data(cost);
    }
}

