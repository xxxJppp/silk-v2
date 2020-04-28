package com.spark.bitrade.service;

import com.spark.bitrade.constant.FeignServiceConstant;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.MemberSecuritySet;
import com.spark.bitrade.entity.SlpMemberPromotion;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.util.MessageResult;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/***
  * 提供Member API服务
  * @author young
  * @time 2018.11.30 11:10
  */
@FeignClient(FeignServiceConstant.UC_SERVER)
public interface IMemberApiService {

    /**
     * 根据apiKey获取用户信息
     *
     * @param apiKey api请求的key, 登录用户token
     * @return Member对象
     */
    @PostMapping(value = "/uc2/internal/findMemberByApiKey")
    MessageRespResult<Member> findMemberByApiKey(@RequestParam(value = "apiKey") String apiKey);

    /**
     * 获取会员信息
     *
     * @param id 会员ID
     * @return member
     */
    @GetMapping(value = "/uc2/internal/member/{id}")
    MessageRespResult<Member> getMember(@PathVariable("id") Long id);

    /**
     * 获取会员信息
     *
     * @param type
     * @param param
     * @return true
     * @author shenzucai
     * @time 2019.07.04 15:45
     */
    @PostMapping(value = "/uc2/api/v2/member/getMemberByPhoneOrEmail")
    MessageRespResult<Member> getMemberByPhoneOrEmail(@RequestParam("type") Integer type, @RequestParam("param") String param);

    /**
     * 批量获取会员信息
     *
     * @param memberIds 会员ID
     * @return member
     */
    @PostMapping(value = "/uc2/api/v2/member/listMembersByIds")
    MessageRespResult<List<Member>> listMembersByIds(@RequestBody List<Long> memberIds);

    /**
     * 获取会员推荐信息
     *
     * @param memberId 会员id
     * @return promotion
     */
    @GetMapping("/uc2/internal/promotion/{id}")
    MessageRespResult<SlpMemberPromotion> getSlpMemberPromotion(@PathVariable("id") long memberId);

    /**
     * 处理会员的密码
     *
     * @param inputPassword 会员输入的密码
     * @param salt          盐
     * @return
     * @author yangch
     * @since 2019-06-20 14:05:18
     */
    @PostMapping(value = "/uc2/api/v2/member/simpleHashPassword")
    MessageRespResult<String> simpleHashPassword(@RequestParam("inputPassword") String inputPassword,
                                                 @RequestParam("salt") String salt);

    /**
     * 密码确认
     *
     * @param storagePassword 存储的密码
     * @param inputPassword   会员输入的密码
     * @param salt            盐
     * @return true=一样/false=不一样
     * @author yangch
     * @since 2019-06-20 14:05:18
     */
    @PostMapping(value = "/uc2/api/v2/member/confirmPassword")
    MessageRespResult<Boolean> confirmPassword(@RequestParam("storagePassword") String storagePassword,
                                               @RequestParam("inputPassword") String inputPassword,
                                               @RequestParam("salt") String salt);


    @RequestMapping(value = "/uc2/upload/oss/base64", method = RequestMethod.POST)
    MessageResult base64UpLoad(@RequestParam("base64Data") String base64Data,
                               @RequestParam(value = "verify", required = false) Boolean verify);

    /**
     * 获取用户安全控制
     *
     * @param memberId 会员id
     * @return
     */
    @PostMapping(value = "/uc2/internal/getMemberSecuritySet")
    public MessageRespResult<MemberSecuritySet> getMemberSecuritySet(@RequestParam("memberId") long memberId);
}
