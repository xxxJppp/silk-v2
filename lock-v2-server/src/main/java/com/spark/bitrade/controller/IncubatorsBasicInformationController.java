package com.spark.bitrade.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constant.IncubatorsBasicStatus;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.constants.LockMsgCode;
import com.spark.bitrade.dto.IncubatorsBasicInformationDto;
import com.spark.bitrade.dto.IncubatorsEntranceDto;
import com.spark.bitrade.entity.*;
import com.spark.bitrade.form.UpCoinForm;
import com.spark.bitrade.service.IMemberWalletApiService;
import com.spark.bitrade.service.ISilkDataDistApiService;
import com.spark.bitrade.service.IncubatorsBasicInformationService;
import com.spark.bitrade.service.SuperPartnerCommunityService;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.util.LockUtil;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 孵化区-上币申请表 前端控制器
 * </p>
 *
 * @author qiliao
 * @since 2019-08-30
 */
@RestController
@RequestMapping("api/v2/incubatorsBasicInformation")
@Api(description = "孵化区")
public class IncubatorsBasicInformationController {

    @Autowired
    private IncubatorsBasicInformationService incubatorsBasicInformationService;
    @Autowired
    private SuperPartnerCommunityService superPartnerCommunityService;
    @Autowired
    private IMemberWalletApiService memberWalletApiService;
    @Autowired
    private ISilkDataDistApiService silkDataDistApiService;

    /**
     * 03提交上币申请
     */
    @ApiOperation(value = "03提交上币申请", notes = "03提交上币申请")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "上币申请form", name = "form", dataTypeClass = UpCoinForm.class)
    })
    @PostMapping(value = "upCoinApply")
    public MessageRespResult upCoinApply(@ApiIgnore @MemberAccount Member member, @Valid UpCoinForm form) {
        String coinUnit = null;
        BigDecimal lockAmount = BigDecimal.ZERO;
        //验证全局配置
        MessageRespResult<List<SilkDataDist>> sk = silkDataDistApiService.list("INCUBATORS_CONFIG");
        AssertUtil.isTrue(sk.isSuccess(), CommonMsgCode.SERVICE_UNAVAILABLE);
        for (SilkDataDist d : sk.getData()) {
            if ("BASE_SYMBOL".equals(d.getDictKey())) {
                coinUnit = d.getDictVal();
            }
            if ("LOCK_AMOUNT".equals(d.getDictKey())) {
                lockAmount = new BigDecimal(d.getDictVal());
            }
        }
        AssertUtil.isTrue(StringUtils.hasText(coinUnit) && lockAmount != null, LockMsgCode.INCUBATORS_CONFIG_NOT_FIND);
        //验证资金密码
        LockUtil.validateJyPassword(form.getJyPassword(), member.getJyPassword(), member.getSalt());
        Long memberId = member.getId();
        //判断是否存在上币申请 或已上币
        QueryWrapper<IncubatorsBasicInformation> qw = new QueryWrapper<>();
        qw.select(IncubatorsBasicInformation.ID).eq(IncubatorsBasicInformation.MEMBER_ID, memberId)
                .notIn(IncubatorsBasicInformation.STATUS, IncubatorsBasicStatus.CLOSED
                        , IncubatorsBasicStatus.UP_COIN_REJECTED, IncubatorsBasicStatus.EXIT_COIN_APPROVED);
        IncubatorsBasicInformation information = incubatorsBasicInformationService.getOne(qw);
        AssertUtil.isTrue(information == null, LockMsgCode.YOU_HAS_UP_COIN);
        //判断是否是超级合伙人 如果是则需退出合伙人 才能上币
        SuperPartnerCommunity partner = superPartnerCommunityService.findByMemberId(memberId, BooleanEnum.IS_TRUE);
        AssertUtil.isTrue(partner == null, LockMsgCode.YOU_MUST_EXIT_SUPER_PARTNER);
        //判断钱包SLU是否满足条件
        MessageRespResult<MemberWallet> wallet = memberWalletApiService.getWallet(memberId, coinUnit);
        AssertUtil.isTrue(wallet.isSuccess(), CommonMsgCode.SERVICE_UNAVAILABLE);
        MemberWallet walletData = wallet.getData();
        AssertUtil.isTrue(walletData.getBalance().compareTo(lockAmount) >= 0,
                LockMsgCode.SLU_WALLET_NOT_FIND_BLANCE_BUZU);
        //创建申请上币
        incubatorsBasicInformationService.upCoinApply(member, form, coinUnit, lockAmount);
        return MessageRespResult.success();
    }

    /**
     * 05退出上币申请（需求已取消，接口废弃）
     */
    @ApiOperation(value = "05退出上币申请（需求已取消，接口废弃）", notes = "05退出上币申请（需求已取消，接口废弃）")
    @PostMapping(value = "exitCoinApply")
    public MessageRespResult exitCoinApply(@ApiIgnore @MemberAccount Member member, @RequestParam String reason) {
        //需求变更 不能退出上币
//        Long memberId = member.getId();
//        //判断未上币的不能申请
//        QueryWrapper<IncubatorsBasicInformation> qw=new QueryWrapper<>();
//        qw.eq(IncubatorsBasicInformation.MEMBER_ID,memberId)
//                .eq(IncubatorsBasicInformation.STATUS, IncubatorsBasicStatus.UP_COIN_APPROVED);
//        IncubatorsBasicInformation information = incubatorsBasicInformationService.getOne(qw);
//        AssertUtil.isTrue(information!=null, LockMsgCode.YOU_NOT_UP_COIN);
//        //执行退出上币
//        incubatorsBasicInformationService.exitCoinApply(member,reason,information );
        return MessageRespResult.success();
    }

    /**
     * 01入口
     *
     * @param member 会员信息
     * @return
     */
    @ApiOperation(value = "01入口", notes = "01入口")
    @PostMapping(value = "entrance")
    public MessageRespResult<IncubatorsEntranceDto> entrance(@ApiIgnore @MemberAccount Member member) {
        return MessageRespResult.success4Data(incubatorsBasicInformationService.getMemberStatus(member.getId()));
    }

    /**
     * 02审核详情
     *
     * @param member 会员信息
     * @return
     */
    @ApiOperation(value = "02审核详情", notes = "02审核详情")
    @PostMapping(value = "details")
    public MessageRespResult<IncubatorsBasicInformationDto> details(@ApiIgnore @MemberAccount Member member) {
        return MessageRespResult.success4Data(incubatorsBasicInformationService.getIncubatorsBasicInformationByMemberId(member.getId()));
    }

}














