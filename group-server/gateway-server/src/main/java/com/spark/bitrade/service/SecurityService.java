package com.spark.bitrade.service;

import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.jwt.MemberClaim;

/**
 *  安全服务
 *
 * @author young
 * @time 2019.05.07 14:37
 */
public interface SecurityService {
    /**
     * 加密服务接口
     *
     * @param apiKey      apiKey
     * @param encryptData 待加密的数据
     * @return
     * @throws MessageCodeException
     */
    String encryptData(String apiKey, String encryptData) throws MessageCodeException;

    /**
     * 解密服务接口
     *
     * @param apiKey      apiKey
     * @param decryptData 待解密的数据
     * @return
     * @throws MessageCodeException
     */
    String decryptData(String apiKey, String decryptData) throws MessageCodeException;

    /**
     * 签名验证服务
     *
     * @param appId       应用ID
     * @param apiKey      apiKey
     * @param memberClaim
     * @param time        有效时间
     * @param requestData 请求数据
     * @return
     * @throws MessageCodeException
     */
    String generateSign(String appId, String apiKey, MemberClaim memberClaim, Long time, String requestData) throws MessageCodeException;

    /**
     * 校验请求时间戳是否有效
     *
     * @param time 请求时间戳
     * @return true=通过/false=不通过
     */
    boolean checkRequestAllowDiffTime(Long time);

    /**
     * 校验是否需要签名验证
     *
     * @param uri 请求的服务端地址，如/otc/healthy
     * @return
     */
    boolean isCheckSign(String uri);

    /**
     * 校验是否为对外暴露的接口
     *
     * @param uri 请求的服务端地址，如/otc/healthy
     * @return
     */
    boolean isExposedApi(String uri);

    /**
     * 校验是否需要加密
     *
     * @param uri 请求的服务端地址，如/otc/healthy
     * @return
     */
    boolean requiredEncrypt(String uri);
}
