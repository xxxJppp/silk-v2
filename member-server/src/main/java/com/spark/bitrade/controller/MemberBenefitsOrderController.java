package com.spark.bitrade.controller;


import java.text.ParseException;

import javax.validation.Valid;

import com.spark.bitrade.constant.MemberMsgCode;
import com.spark.bitrade.constant.OperateTypeEnum;
import com.spark.bitrade.constant.PayTypeEnum;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.util.HttpRequestUtil;
import com.spark.bitrade.vo.MemberBenefitsExtendsVo;
import com.spark.bitrade.web.resubmit.annotation.ForbidResubmit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.biz.IBenefitsOrderService;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.MemberBenefitsExtends;
import com.spark.bitrade.entity.MemberBenefitsOrder;
import com.spark.bitrade.form.BenefitsOrderForm;
import com.spark.bitrade.param.PageParam;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.utils.MemberUtil;
import com.spark.bitrade.vo.CurrentAmountVo;
import com.spark.bitrade.web.bind.annotation.MemberAccount;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * <p>
 * 会员申请订单 前端控制器
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-18
 */
@RestController
@RequestMapping("api/v2/member/memberBenefitsOrder")
@Api(tags = "会员开通申请前端接口")
@Slf4j
public class MemberBenefitsOrderController extends ApiController{

    @Autowired
    private IBenefitsOrderService benefitsOrderService;


    @ApiOperation(value = "会员开通申请接口", notes = "会员开通申请接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "资金密码", name = "moneyPassword", dataTypeClass = String.class, required = true)
    })
    @PostMapping(value = "/add")
    @ForbidResubmit
    public MessageRespResult openingMemberVip(@MemberAccount Member member, @RequestParam(required = false) String moneyPassword, @Valid BenefitsOrderForm fitsOrderForm) {
        log.info("=========>>>>>==========开通会员申请表单 ========================= {}", fitsOrderForm);
        // 验证资金密码
        if (!(fitsOrderForm.getOperateType() == OperateTypeEnum.RENEW.getCode() && fitsOrderForm.getPayType() == PayTypeEnum.LOCK.getCode())) {
            MemberUtil.validatePassword(moneyPassword,member.getJyPassword(),member.getSalt());
        }
        if (fitsOrderForm.getAppId() == null) {
            String appId = HttpRequestUtil.getAppId();
            fitsOrderForm.setAppId(Integer.valueOf(appId));
        }
        try {
            benefitsOrderService.saveMemberVip(member.getId(), fitsOrderForm);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return success();
    }

    @PostMapping("/getCurrentVip")
    @ApiOperation(value = "获取当前会员vip 等级", notes = "获取当前会员vip 等级")
    public MessageRespResult<MemberBenefitsExtendsVo> findCurrentMemberVip(@MemberAccount Member member) {
        MemberBenefitsExtendsVo vip = benefitsOrderService.getCurrentMemberVip(member.getId());
        return success(vip);
    }


    @PostMapping("/getHistory")
    @ApiOperation(value = "个人中心，历史订单查询", notes = "获取当前会员vip 等级")
    public MessageRespResult<IPage<MemberBenefitsOrder>> findMemberBenefitsOrderHistory(@MemberAccount Member member, @ApiParam(value = "分页参数") @Valid PageParam param) {
        IPage<MemberBenefitsOrder> page = null;
        try {
            page = benefitsOrderService.getMemberBenefitsOrderHistory(member.getId(), param);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return success(page);
    }


    @GetMapping("/give")
    public MessageRespResult<Integer> giveMemberVip1(@RequestParam("memberId") Long memberId, @RequestParam("appId") Integer appId) {
        Integer result = null;
        try {
            result = benefitsOrderService.giveMemberVip(memberId, appId);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return success(result);
    }
}
