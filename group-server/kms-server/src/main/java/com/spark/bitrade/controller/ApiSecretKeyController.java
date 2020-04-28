package com.spark.bitrade.controller;

import com.spark.bitrade.enums.MessageCode;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.service.ApiSecretKeyService;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.MessageRespResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/****
  * 密钥接口
 *
  * @author yangch
  * @time 2019.01.15 14:30
  */
@Slf4j
@RestController
@RequestMapping("/apiSecretKey")
public class ApiSecretKeyController {
    @Autowired
    private ApiSecretKeyService apiSecretKeyService;

    /**
     * 获取apiSecretSalt
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/apiSecretSalt")
    public MessageRespResult<Integer> apiSecretSalt() throws MessageCodeException {
        try {
            Integer apiSecretSalt = apiSecretKeyService.apiSecretSalt();
            if (apiSecretSalt == null) {
                return MessageRespResult.error("未能获取到apiSecretSalt");
            } else {
                return MessageRespResult.success4Data(apiSecretSalt);
            }
        } catch (Exception ex) {
            log.error("解析Partner对象信息出错", ex);
        }

        return MessageRespResult.error("未能获取到apiSecretSalt");
    }

    /**
     * 获取指定apiKey的密钥
     *  备注：密钥已按约定规则处理
     *
     * @param apiKey
     * @return
     * @throws Exception
     */
    @RequestMapping("/apiSecret")
    public MessageRespResult<String> apiSecretByApiKey(String apiKey) throws MessageCodeException{
        String apiSecret = apiSecretKeyService.apiSecretByApiKey(apiKey);
        AssertUtil.notNull(apiSecret, MessageCode.INVALID_AUTH_TOKEN);
        return MessageRespResult.success4Data(apiSecret);
    }

}
