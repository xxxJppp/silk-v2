package com.spark.bitrade.web.adapter;

import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.jwt.HttpJwtToken;
import com.spark.bitrade.jwt.MemberClaim;
import com.spark.bitrade.service.MemberAccountService;
import com.spark.bitrade.util.AssertUtil;

import java.util.Optional;

/**
 * MemberAccountGetterAdapter
 *
 * @author archx
 * @since 2019/5/30 10:09
 */
public abstract class MemberAccountGetterAdapter {

    protected MemberAccountService memberAccountService;

    public void setMemberAccountService(MemberAccountService memberAccountService) {
        this.memberAccountService = memberAccountService;
    }

    protected final Member getCurrentMember(String apiKey) {
        //edit by young 时间： 2019.06.22 原因：修改为根据UID查询用户信息，不用apiKey作为redis 的key
        //Optional<Member> member = Optional.ofNullable(memberAccountService.findMemberByApiKey(apiKey));
        Optional<Member> member = Optional.ofNullable(memberAccountService.findMemberByMemberId(this.getMemberId(apiKey)));

        if (!member.isPresent()) {
            throw new MessageCodeException(CommonMsgCode.UNKNOWN_ACCOUNT);
        }

        return member.get();
    }

    protected final Long getMemberId(String apiKey) {
        Optional<MemberClaim> memberClaim = Optional.ofNullable(HttpJwtToken.getInstance().verifyToken(apiKey));
        AssertUtil.isTrue(memberClaim.isPresent(), CommonMsgCode.UNKNOWN_ACCOUNT);
        AssertUtil.notNull(memberClaim.get().userId, CommonMsgCode.UNKNOWN_ACCOUNT);
        return memberClaim.get().userId;
    }
}
