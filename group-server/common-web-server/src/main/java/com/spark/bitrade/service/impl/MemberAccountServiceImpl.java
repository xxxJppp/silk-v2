package com.spark.bitrade.service.impl;

import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.service.IMemberApiService;
import com.spark.bitrade.service.MemberAccountService;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


/**
 * MemberAccountServiceImpl
 *
 * @author archx
 * @since 2019/5/8 18:31
 */
@Service
public class MemberAccountServiceImpl implements MemberAccountService {

    private IMemberApiService memberApiService;

    @Autowired
    public void setMemberApiService(IMemberApiService memberApiService) {
        this.memberApiService = memberApiService;
    }

    @Cacheable(cacheNames = "member", key = "'entity:member:'+#apiKey")
    @Override
    public Member findMemberByApiKey(String apiKey) {
        MessageRespResult<Member> resp = memberApiService.findMemberByApiKey(apiKey);
        if (resp.isSuccess() && resp.getData() != null) {
            return resp.getData();
        }
        throw new MessageCodeException(CommonMsgCode.UNAUTHORIZED);
    }

    @Override
    @Cacheable(cacheNames = "member", key = "'entity:member:uid-'+#memberId")
    public Member findMemberByMemberId(Long memberId) {
        MessageRespResult<Member> resp = memberApiService.getMember(memberId);
        if (resp.isSuccess() && resp.getData() != null) {
            return resp.getData();
        }
        throw new MessageCodeException(CommonMsgCode.UNAUTHORIZED);
    }
}
