package com.spark.bitrade.service.impl;

import cn.hutool.core.util.StrUtil;
import com.spark.bitrade.config.IgnoreRequestSignConfig;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.jwt.MemberClaim;
import com.spark.bitrade.service.SecurityService;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.DesECBUtil;
import com.spark.bitrade.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 安全服务实现类
 *
 * @author yyangch
 * @time 2019.05.07 15:02
 */
@Service
@Slf4j
public class SecurityServiceImpl implements SecurityService {
    @Autowired
    private IgnoreRequestSignConfig ignoreRequestSignConfig;

    /**
     * api请求有效的时间差：单位为秒，默认600秒
     */
    @Value("${request.allow.diff.time:600}")
    private Long requestAllowDiffTime;

    @Override
    public String encryptData(String apiKey, String encryptData) throws MessageCodeException {
        AssertUtil.notNull(apiKey, CommonMsgCode.REQUIRED_PARAMETER);
        if (StringUtils.isEmpty(encryptData)) {
            return encryptData;
        }

        String encryptionKey = getEncryptionKey(apiKey);
        AssertUtil.notNull(encryptionKey, CommonMsgCode.UNKNOWN_ERROR);
        try {
            return DesECBUtil.encryptDES(encryptData, encryptionKey);
        } catch (Exception e) {
            log.error("加密失败！encryptionKey=" + encryptionKey + " , encryptData=" + encryptData, e);
            throw new MessageCodeException(CommonMsgCode.UNKNOWN_ERROR);
        }
    }

    @Override
    public String decryptData(String apiKey, String decryptData) throws MessageCodeException {
        AssertUtil.notNull(apiKey, CommonMsgCode.REQUIRED_PARAMETER);
        if (StringUtils.isEmpty(decryptData)) {
            return decryptData;
        }

        String encryptionKey = getEncryptionKey(apiKey);
        //未能获取到密钥
        AssertUtil.notNull(encryptionKey, CommonMsgCode.UNKNOWN_ERROR);
        try {
            return DesECBUtil.decryptDES(decryptData, encryptionKey);
        } catch (Exception e) {
            log.error("解密失败！encryptionKey=" + encryptionKey + " , decryptData=" + decryptData, e);
            throw new MessageCodeException(CommonMsgCode.UNKNOWN_ERROR);
        }
    }

    @Override
    public String generateSign(String appId, String apiKey, MemberClaim memberClaim, Long time, String requestData) throws MessageCodeException {
        AssertUtil.notNull(appId, CommonMsgCode.REQUIRED_PARAMETER);
        AssertUtil.notNull(apiKey, CommonMsgCode.REQUIRED_PARAMETER);
        AssertUtil.notNull(time, CommonMsgCode.REQUIRED_PARAMETER);
        if (StringUtils.isEmpty(requestData)) {
            requestData = "";
        }

        String apiSecret = getApiSecret(apiKey, memberClaim);
        //未能获取到密钥
        AssertUtil.notNull(apiSecret, CommonMsgCode.UNKNOWN_ERROR);
        //签名 = Md5(apiKey+time+apiSecret2+请求数据)
        return MD5Util.md5Encode(appId.concat(apiKey).concat(time.toString()).concat(apiSecret).concat(requestData));
    }

    @Override
    public boolean checkRequestAllowDiffTime(Long time) {
        long diffTime = Math.abs((System.currentTimeMillis() - time) / 1000);
        if (requestAllowDiffTime - diffTime > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isCheckSign(String uri) {
        if (ignoreRequestSignConfig == null || ignoreRequestSignConfig.getIgnoreList() == null) {
            return true;
        }

        return ignoreRequestSignConfig
                .getIgnoreList()
                .stream()
                .filter(x -> x.equalsIgnoreCase(uri))
                .count() > 0 ? true : false;
    }

    @Override
    public boolean isExposedApi(String uri) {
        //简单的根据 请求路径的特征 判断 是否为对外暴露的API
        if (uri.contains("?")) {
            //防止 通过参数传入特征
            return uri.substring(0, uri.indexOf("?")).contains("/api/v");
        } else {
            return uri.contains("/api/v");
        }
    }

    @Override
    public boolean requiredEncrypt(String uri) {
        //简单的根据 请求路径的特征 判断 是否为对外暴露的API
        if (uri.contains("?")) {
            //防止 通过参数传入特征
            return !uri.substring(0, uri.indexOf("?")).contains("/no-auth/");
        } else {
            return !uri.contains("/no-auth/");
        }
    }

    /**
     * 根据apiKey获取密钥，密钥=Md5(apiSecretSalt+apiSecret)
     *
     * @param apiKey
     * @return
     */
    public String getApiSecret(String apiKey, MemberClaim memberClaim) {
        return MD5Util.md5Encode(memberClaim.userId.toString().concat(this.apiSecretArithmetic(apiKey, memberClaim.userId)));
    }

    /**
     * 获取请求数据的加解密密钥
     *
     * @return reverse(md5 ( apiKey))
     */
    private String getEncryptionKey(String apiKey) {
        return StrUtil.reverse(MD5Util.md5Encode(apiKey));
    }

    /**
     * apiSecret还原算法
     *
     * @param apiKey apiKey
     * @param uid    用户ID
     * @return
     */
    public String apiSecretArithmetic(String apiKey, long uid) {
        StringBuilder originalApiSecret = new StringBuilder();
        char[] chars = String.valueOf(uid).toCharArray();
        AssertUtil.isTrue(chars.length < apiKey.length(), CommonMsgCode.INVALID_PARAMETER);
        for (char c : chars) {
            originalApiSecret.append(apiKey.charAt(Integer.parseInt(String.valueOf(c))));
        }
        return MD5Util.md5Encode(originalApiSecret.toString());
    }

//    public static void main(String[] args) {
//        SecurityServiceImpl securityService = new SecurityServiceImpl();
//        String apiKey = "swaggeruihtmlmembertransactioncontroller";
//        long uid = 245778;
//        System.out.println(securityService.apiSecretArithmetic(apiKey, uid));
//    }
}
