package com.spark.bitrade.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.spark.bitrade.config.AliyunConfig;
import com.spark.bitrade.constant.*;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.constants.LockMsgCode;
import com.spark.bitrade.entity.*;
import com.spark.bitrade.form.SuperPartnerCommunityForm;
import com.spark.bitrade.service.*;
import com.spark.bitrade.util.*;
import com.spark.bitrade.vo.MemberCommunityCaseVo;
import com.spark.bitrade.vo.SuperApplyRecordVo;
import com.spark.bitrade.vo.SuperPartnerCommunityVo;
import com.spark.bitrade.vo.UpCoinCommunityVo;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 合伙人用户关联表 前端控制器
 * </p>
 *
 * @author qiliao
 * @since 2019-07-16
 */
@RestController
@RequestMapping("api/v2/superPartner")
@Api(description = "超级合伙人接口")
@Slf4j
public class SuperPartnerCommunityController {

//    private static final String PREFIX = "http://silktraderpriv.oss-cn-hongkong.aliyuncs.com/";

    private static final String REG="^[\\u4e00-\\u9fa5_a-zA-Z0-9]+$";

    @Autowired
    private SuperMemberCommunityService superMemberCommunityService;

    @Autowired
    private SuperPartnerCommunityService superPartnerCommunityService;

    @Autowired
    private SuperPartnerApplyRecordService superPartnerApplyRecordService;

    @Autowired
    private IMemberWalletApiService memberWalletApiService;

    @Autowired
    private MemberAccountService memberAccountService;

    @Autowired
    private ISilkDataDistApiService silkDataDistApiService;

    @Autowired
    private LockCoinDetailService lockCoinDetailService;

//    @Autowired
//    private ICoinExchange coinExchange;

    @Autowired
    private AliyunConfig aliyunConfig;

    @Autowired
    private IncubatorsBasicInformationService incubatorsBasicInformationService;

    @ApiOperation(value = "根据社区id查询社区", notes = "根据社区id查询社区")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "社区id", name = "communityId", dataTypeClass = Long.class)
    })
    @PostMapping(value = {"findCommunityById", "no-auth/findCommunityById"})
    public MessageRespResult<SuperPartnerCommunityVo> findCommunityById(@RequestParam Long communityId, Long memberId) {
        AssertUtil.notNull(communityId, CommonMsgCode.REQUIRED_PARAMETER);
        SuperPartnerCommunity community =
                superPartnerCommunityService.findById(communityId, BooleanEnum.IS_TRUE);
        SuperPartnerCommunityVo vo = new SuperPartnerCommunityVo();
        AssertUtil.notNull(community, LockMsgCode.COMMUNITY_IS_NOT_FIND);

        BeanUtils.copyProperties(community, vo);

        Member member = memberAccountService.findMemberByMemberId(vo.getMemberId());
        vo.setRealName(member.getRealName());
        vo.setUserName(member.getUsername());
        vo.setWechatCode(generateImageUrl(LockUtil.decodeUrl(vo.getWechatCode())));
        if (memberId != null) {
            SuperMemberCommunity memberCommunity = superMemberCommunityService.findMemberCommunity(memberId);
            if (memberCommunity != null) {
                if (memberCommunity.getCommunityId().equals(community.getId())) {
                    vo.setCurrentJoinStatus(MemberCurrentJoinStatus.IS_CURRENT_MEMBER);
                } else {
                    vo.setCurrentJoinStatus(MemberCurrentJoinStatus.OTHER_COMMUNITY_MEMBER);
                }
            } else {
                vo.setCurrentJoinStatus(MemberCurrentJoinStatus.NO_JOIN);
            }
        }

        return MessageRespResult.success4Data(vo);
    }


    @ApiOperation(value = "加入社区", notes = "加入社区")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "社区id", name = "communityId", dataTypeClass = Long.class)
    })
    @PostMapping("joinCommunity")
    public MessageRespResult joinCommunity(@ApiIgnore @MemberAccount Member member, @RequestParam Long communityId) {
        Long memberId = member.getId();
        //查询当前用户是否已经加入社区 如果加入则抛出异常
        SuperMemberCommunity memberCommunity = superMemberCommunityService.findMemberCommunity(memberId);
        AssertUtil.isTrue(memberCommunity == null, LockMsgCode.YOU_HAS_BEEN_IN_COMMUNITY);

        //判断完成 加入社区
        superMemberCommunityService.joinCommunity(memberId, communityId);

        return MessageRespResult.success("加入社区成功!");
    }


    @ApiOperation(value = "退出社区", notes = "退出社区")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "资金密码", name = "moneyPassword", dataTypeClass = String.class)
    })
    @PostMapping("exitCommunity")
    public MessageRespResult exitCommunity(@ApiIgnore @MemberAccount Member member, String moneyPassword) {
        //验证资金密码
        validatePassword(moneyPassword, member.getJyPassword(), member.getSalt());

        Long memberId = member.getId();
        //判断用户是否存在社区   存在才允许退出
        SuperMemberCommunity memberCommunity = superMemberCommunityService.findMemberCommunity(memberId);
        AssertUtil.notNull(memberCommunity, LockMsgCode.YOU_HAS_NOT_JOIN_COMMUNITY);

        //加入时间在30天内 扣除5SLU给合伙人
        Date createTime = memberCommunity.getCreateTime();
        Date currentTime = new Date();
        long diffDays = DateUtil.diffDays(createTime, currentTime);
        //判断加入社区的时间未超过30天需要支付SLU  如果SLU没有5个则不能退出
        //查询配置
        MessageRespResult<SilkDataDist> respResult1 = silkDataDistApiService.findOne("SUPER_PARTNER_CONFIG", "BASE_SYMBOL");
        SilkDataDist data1 = respResult1.getData();
        AssertUtil.notNull(data1, LockMsgCode.SUPER_CONFIG_NOT_FIND);

        boolean isReduce = false;
        if (diffDays < 30) {
            //资金处理
            MessageRespResult<MemberWallet> sluWallet = memberWalletApiService.getWallet(memberId, data1.getDictVal());
            MemberWallet memberWallet = sluWallet.getData();
            AssertUtil.isTrue(sluWallet.isSuccess(), CommonMsgCode.SERVICE_UNAVAILABLE);
            AssertUtil.isTrue(memberWallet != null, LockMsgCode.SLU_WALLET_NOT_FIND_BLANCE_BUZU);
            BigDecimal balance = memberWallet.getBalance();
            AssertUtil.isTrue(balance.compareTo(new BigDecimal(5)) >= 0, LockMsgCode.SLU_WALLET_NOT_FIND_BLANCE_BUZU);
            //验证完毕创建资金交易流水
            isReduce = true;
        }
        //执行退出社区
        superMemberCommunityService.exitCommunity(memberCommunity, isReduce, data1.getDictVal());


        return MessageRespResult.success("退出社区成功!");
    }


    /**
     * 查询当前用户 加入社区 或者拥有社区的情况
     */
    @ApiOperation(value = "查询当前用户 加入社区 或者拥有社区的情况", notes = "查询当前用户 加入社区 或者拥有社区的情况")
    @PostMapping("findMemberInfo")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "查询类型0:我拥有的社区 ，1:我加入的社区", name = "type", dataTypeClass = Integer.class)
    })
    public MessageRespResult<UpCoinCommunityVo> findMemberInfo(@ApiIgnore @MemberAccount Member member, @RequestParam Integer type) {
        UpCoinCommunityVo upCoinCommunityVo=new UpCoinCommunityVo();
        MemberCommunityCaseVo result = MemberCommunityCaseVo.builder().build();
        Long memberId = member.getId();
        //我加入的社区
        if (type == 1) {
            SuperPartnerCommunityVo vo = new SuperPartnerCommunityVo();
            //查询当前用户是否加入社区 如果加入了 则直接返回
            SuperMemberCommunity memberCommunity = superMemberCommunityService.findMemberCommunity(memberId);
            if (memberCommunity != null) {
                //当前加入的社区
                SuperPartnerCommunity community = superPartnerCommunityService.findById(memberCommunity.getCommunityId(),
                        BooleanEnum.IS_TRUE);
                if (community != null) {
                    BeanUtils.copyProperties(community, vo);
                    long diffDays = DateUtil.diffDays(memberCommunity.getCreateTime(), new Date());
                    vo.setIfMorethan30(diffDays >= 30 ? BooleanEnum.IS_TRUE : BooleanEnum.IS_FALSE);
                    vo.setWechatCode(generateImageUrl(LockUtil.decodeUrl(vo.getWechatCode())));
                    result.setCommunityVo(vo);
                }
            }
        }

        if (type == 0) {
            //查询锁仓详情
            QueryWrapper<LockCoinDetail> lcdw = new QueryWrapper<>();
            lcdw.select("total_amount,coin_unit")
                    .eq("member_id", memberId).eq("type", LockType.SUPER_PARTNER.getOrdinal())
                    .eq("status", LockStatus.LOCKED.getOrdinal());
            List<LockCoinDetail> list = lockCoinDetailService.list(lcdw);

            //查询用户当前是否拥有社区 有则返回社区实例
            SuperPartnerCommunity partnerCommunity = superPartnerCommunityService.findByMemberId(memberId);
            if (partnerCommunity != null) {
                //查询社区成员
                SuperPartnerCommunityVo ownVo = new SuperPartnerCommunityVo();
                BeanUtils.copyProperties(partnerCommunity, ownVo);
                ownVo.setWechatCode(generateImageUrl(LockUtil.decodeUrl(ownVo.getWechatCode())));
                result.setCommunityVo(ownVo);
                List<SuperPartnerApplyRecord> exitRecord =
                        superPartnerApplyRecordService.findByMemberId(memberId);
                if (!CollectionUtils.isEmpty(exitRecord)) {
                    SuperApplyRecordVo exitVo = new SuperApplyRecordVo();
                    BeanUtils.copyProperties(exitRecord.get(0), exitVo);
                    if (!CollectionUtils.isEmpty(list)) {
                        LockCoinDetail detail = list.get(0);
                        exitVo.setCoinUnit(detail.getCoinUnit());
                        exitVo.setLockAmount(detail.getTotalAmount());
                    }
                    result.setRecord(exitVo);
                }
            }


        }

        SuperPartnerCommunityVo ownCommunity = result.getCommunityVo();
        if (ownCommunity != null) {
            Member memberByMemberId = memberAccountService.findMemberByMemberId(ownCommunity.getMemberId());
            ownCommunity.setUserName(memberByMemberId.getUsername());
            ownCommunity.setRealName(memberByMemberId.getRealName());
        }

        //判断是否存在上币申请 或已上币
        QueryWrapper<IncubatorsBasicInformation> qw=new QueryWrapper<>();
        qw.select(IncubatorsBasicInformation.ID).eq(IncubatorsBasicInformation.MEMBER_ID,member.getId())
                .eq(IncubatorsBasicInformation.STATUS, IncubatorsBasicStatus.UP_COIN_PENDING);
        IncubatorsBasicInformation information = incubatorsBasicInformationService.getOne(qw);
        upCoinCommunityVo.setHasUpCoin(information!=null);
        upCoinCommunityVo.setMemberCommunityCaseVo(result);


        return MessageRespResult.success4Data(upCoinCommunityVo);

    }


    /**
     * 查询当前用户 加入社区 或者拥有社区的情况
     */
    @ApiOperation(value = "查询社区成员")
    @PostMapping("findMembersByCommunity")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "社区id", name = "communityId", dataTypeClass = Long.class),
            @ApiImplicitParam(value = "页码", name = "pageNo", dataTypeClass = Integer.class),
            @ApiImplicitParam(value = "size", name = "pageSize", dataTypeClass = Integer.class)
    })
    public MessageRespResult findMembersByCommunity(@RequestParam Long communityId,
                                                    @RequestParam(defaultValue = "1") int pageNo,
                                                    @RequestParam(defaultValue = "10") int pageSize) {
        return MessageRespResult.success4Data(superMemberCommunityService.findCommunityMembers(communityId, pageNo, pageSize));
    }


    @ApiOperation(value = "申请成为合伙人", notes = "申请成为合伙人")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "参数form", name = "form", dataTypeClass = SuperPartnerCommunityForm.class)
    })
    @PostMapping("applyPartner")
    public MessageRespResult applyPartner(@ApiIgnore @MemberAccount Member member,
                                          SuperPartnerCommunityForm form) {
        Pattern pattern = Pattern.compile(REG);
        Matcher m = pattern.matcher(form.getCommunityName());
        AssertUtil.isTrue(m.find(), LockMsgCode.COMMUNITY_NAME_IS_INCORRECT);
        //验证资金密码//modify by qhliao 会员体系 取消锁仓
//        validatePassword(form.getMoneyPassword(), member.getJyPassword(), member.getSalt());

        //验证当前是否已经合伙人
        SuperPartnerCommunity partnerCommunity = superPartnerCommunityService.findByMemberId(member.getId(), BooleanEnum.IS_TRUE);
        AssertUtil.isTrue(partnerCommunity == null, LockMsgCode.YOU_HAS_OR_APPLY_COMMUNITY);

        //验证是否正在申请合伙人
        List<SuperPartnerApplyRecord> records = superPartnerApplyRecordService.findByMemberId(member.getId(), CommunityApplyType.APPLYING_SUPER_PARTNER, SuperAuditStatus.PENDING);
        AssertUtil.isTrue(CollectionUtils.isEmpty(records), LockMsgCode.YOU_HAS_OR_APPLY_COMMUNITY);

        //判断是否存在上币申请 或已上币
        QueryWrapper<IncubatorsBasicInformation> qw=new QueryWrapper<>();
        qw.select(IncubatorsBasicInformation.ID).eq(IncubatorsBasicInformation.MEMBER_ID,member.getId())
                .notIn(IncubatorsBasicInformation.STATUS, IncubatorsBasicStatus.CLOSED
                        ,IncubatorsBasicStatus.UP_COIN_REJECTED,IncubatorsBasicStatus.EXIT_COIN_APPROVED);
        IncubatorsBasicInformation information = incubatorsBasicInformationService.getOne(qw);
        AssertUtil.isTrue(information==null, LockMsgCode.YOU_HAS_UP_COIN);

        //查询配置 modify by qhliao 会员体系 取消锁仓
//        MessageRespResult<SilkDataDist> respResult1 = silkDataDistApiService.findOne("SUPER_PARTNER_CONFIG", "BASE_SYMBOL");
//        SilkDataDist data1 = respResult1.getData();
//        AssertUtil.notNull(data1, LockMsgCode.SUPER_CONFIG_NOT_FIND);
//        MessageRespResult<SilkDataDist> respResult2 = silkDataDistApiService.findOne("SUPER_PARTNER_CONFIG", "LOCK_AMOUNT");
//        SilkDataDist data2 = respResult2.getData();
//        AssertUtil.notNull(data2, LockMsgCode.SUPER_CONFIG_NOT_FIND);
//        String coinUnit = data1.getDictVal();
//        BigDecimal amount = new BigDecimal(data2.getDictVal());
//        //资金处理
//        MessageRespResult<MemberWallet> sluWallet = memberWalletApiService.getWallet(member.getId(), coinUnit);
//        MemberWallet memberWallet = sluWallet.getData();
//        AssertUtil.isTrue(sluWallet.isSuccess(), CommonMsgCode.SERVICE_UNAVAILABLE);
//        AssertUtil.isTrue(memberWallet != null, LockMsgCode.SLU_WALLET_NOT_FIND_BLANCE_BUZU);
//        BigDecimal balance = memberWallet.getBalance();
//        AssertUtil.isTrue(balance.compareTo(amount) >= 0, LockMsgCode.SLU_WALLET_NOT_FIND_BLANCE_BUZU);
//
//        form.setCoinUnit(coinUnit);
//        form.setAmount(amount);
        form.setWechatCodeUrl(LockUtil.decodeUrl(form.getWechatCodeUrl()));
        //查询汇率
//        MessageRespResult<BigDecimal> usdtRate = coinExchange.getUsdExchangeRate(coinUnit);
//        if (usdtRate.isSuccess()) {
//            form.setUsdtRate(usdtRate.getData());
//        }
//        //USDT 相当于 CNY
//        MessageRespResult<BigDecimal> cnyRate = coinExchange.getCnyExchangeRate("USDT");
//        if (cnyRate.isSuccess()) {
//            form.setCnytRate(cnyRate.getData());
//        }

        //创建申请合伙人记录 创建社区
        superPartnerCommunityService.createSuperPartner(member.getId(), form);

        return MessageRespResult.success("提交成功!");
    }


    @ApiOperation(value = "退出合伙人", notes = "退出合伙人")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "资金密码", name = "moneyPassword", dataTypeClass = String.class),
    })
    @PostMapping("exitSuperPartner")
    public MessageRespResult exitSuperPartner(@ApiIgnore @MemberAccount Member member, String moneyPassword) {
        //验证资金密码
//        validatePassword(moneyPassword, member.getJyPassword(), member.getSalt());
        Long memberId = member.getId();
        //查询当前用户是否是合伙人
        SuperPartnerCommunity partnerCommunity = superPartnerCommunityService.findByMemberId(memberId, BooleanEnum.IS_TRUE);
        AssertUtil.notNull(partnerCommunity, LockMsgCode.YOU_ARE_NOT_PARTNER);
        AssertUtil.isTrue(partnerCommunity.getSourceChannel()==SuperRegistration.INITIATIVE,LockMsgCode.YOU_CANT_EXIT_SUPERTNER);
        //查询当前用户是否正在退出申请合伙人
        List<SuperPartnerApplyRecord> records = superPartnerApplyRecordService.findByMemberId(memberId, CommunityApplyType.EXITING_APPLY_SUPER_PARTNER, SuperAuditStatus.PENDING);
        AssertUtil.isTrue(CollectionUtils.isEmpty(records), LockMsgCode.YOU_ARE_EXTING_PARTNER_PLEASE_WATING);


        //执行退出 创建退出合伙人记录 等待审核
        superPartnerCommunityService.exitSuperPartner(memberId, partnerCommunity);


        return MessageRespResult.success("提交成功!");

    }


    /**
     * 验证资金密码
     *
     * @param moneyPassword
     * @param jyPassword
     */
    private void validatePassword(String moneyPassword, String jyPassword, String salt) {
        AssertUtil.hasText(moneyPassword, CommonMsgCode.MISSING_JYPASSWORD);
        AssertUtil.hasText(jyPassword, CommonMsgCode.NO_SET_JYPASSWORD);
        String jyPass = new SimpleHash("md5", moneyPassword, salt, 2).toHex().toLowerCase();
        AssertUtil.isTrue(jyPassword.equals(jyPass), CommonMsgCode.ERROR_JYPASSWORD);
    }

    /**
     * 获取阿里云私有地址
     * @param url
     * @return
     */
    private String generateImageUrl(String url) {
        String urlEnd=null;
        try {
            urlEnd=AliyunUtil.getPrivateUrl(aliyunConfig,url);
        } catch (Exception e) {
            log.error("转化图片地址失败!");
        }
        return urlEnd;
    }

    /**
     * 会员体系
     * 查询当前用户社区的人数
     */
    @PostMapping("/countCommunityNumber")
    public MessageRespResult<SuperPartnerCommunity> findPartnerCommunityNumber(@MemberAccount Member member) {
        SuperPartnerCommunity partnerCommunity = superPartnerCommunityService.findByMemberId(member.getId());
        return MessageRespResult.success4Data(partnerCommunity);
    }

}














