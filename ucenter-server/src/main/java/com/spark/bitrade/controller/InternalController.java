package com.spark.bitrade.controller;

import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.MemberSecuritySet;
import com.spark.bitrade.entity.SlpMemberPromotion;
import com.spark.bitrade.jwt.HttpJwtToken;
import com.spark.bitrade.jwt.MemberClaim;
import com.spark.bitrade.service.MemberSecuritySetService;
import com.spark.bitrade.service.MemberService;
import com.spark.bitrade.service.SlpMemberPromotionService;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 内部接口，不暴露到swagger
 *
 * @author archx
 * @since 2019/6/11 17:54
 */
@RestController
@RequestMapping("/internal")
public class InternalController extends ApiController {

    @Resource
    private MemberService memberService;

    @Resource
    private SlpMemberPromotionService promotionService;

    @Resource
    private MemberSecuritySetService memberSecuritySetService;

    /**
     * 通过登录令牌获取会员信息
     *
     * @param apiKey 秘钥，会话token
     * @return resp
     */
    @PostMapping("/findMemberByApiKey")
    public MessageRespResult<Member> findMemberByApiKey(@RequestParam("apiKey") String apiKey) {
        Member member = null;
        MemberClaim claim = HttpJwtToken.getInstance().verifyToken(apiKey);
        if (claim != null) {
            member = memberService.loginWithUserId(claim.userId, claim.username);
        }
        return member != null ? success(member) : failed(CommonMsgCode.FAILURE);
    }

    /**
     * 获取用户信息
     *
     * @param id 会员ID
     * @return resp
     */
    @GetMapping("/member/{id}")
    public MessageRespResult<Member> getMember(@PathVariable("id") Long id) {
        Member member = memberService.getById(id);

        if (member != null) {
            return success(member);
        }

        return failed(CommonMsgCode.UNKNOWN_ACCOUNT);
    }

    /**
     * 获取会员推荐信息
     *
     * @param memberId 会员id
     * @return resp
     */
    @GetMapping("/promotion/{id}")
    public MessageRespResult<SlpMemberPromotion> getSlpMemberPromotion(@PathVariable("id") long memberId) {
        SlpMemberPromotion promotion = promotionService.getById(memberId);
        return success(promotion);
    }

    /**
     * 获取用户安全控制
     *
     * @param memberId 会员id
     * @return
     */
    @RequestMapping(value = "/getMemberSecuritySet", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<MemberSecuritySet> getMemberSecuritySet(@RequestParam("memberId") long memberId) {
        return success(this.memberSecuritySetService.findByMemberId(memberId));
    }

}
