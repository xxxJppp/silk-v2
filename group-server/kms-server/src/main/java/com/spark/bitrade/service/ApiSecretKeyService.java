package com.spark.bitrade.service;

/**
 *  Api密钥服务
 *
 * @author young
 * @time 2019.05.06 19:24
 */
public interface ApiSecretKeyService {
    /**
     * 获取apiSecretSalt
     * @return
     */
    Integer apiSecretSalt();

    /**
     * 根据钱包地址获取用户的apiSecret
     *
     * @param apiKey
     * @return apiSecret密钥
     */
    String apiSecretByApiKey(String apiKey);
}
