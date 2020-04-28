package com.spark.bitrade.service;

import com.spark.bitrade.entity.Member;

/**
 * MemberAccountService
 *
 * @author archx
 * @since 2019/5/8 18:01
 */
public interface MemberAccountService {

    Member findMemberByApiKey(String apiKey);


    Member findMemberByMemberId(Long memberId);

}
