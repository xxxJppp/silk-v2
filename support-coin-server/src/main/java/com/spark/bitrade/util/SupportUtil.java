package com.spark.bitrade.util;

import com.spark.bitrade.config.AliyunConfig;
import com.spark.bitrade.config.PayConfigProperties;
import com.spark.bitrade.constants.CommonMsgCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.11.05 09:28  
 */
@Slf4j
@Component
public class SupportUtil {

    public static final String PREFIX = "http://silktraderpriv.oss-cn-hongkong.aliyuncs.com/";

    public static AliyunConfig aliyunConfig;

    public static PayConfigProperties payConfigProperties;

    @Autowired
    public void setPayConfigProperties(PayConfigProperties payConfigProperties) {
        SupportUtil.payConfigProperties = payConfigProperties;
    }

    @Autowired
    public void setAliyunConfig(AliyunConfig aliyunConfig) {
        SupportUtil.aliyunConfig = aliyunConfig;
    }

    /**
     * 解码url
     *
     * @param url
     * @return
     */
    public static String decodeUrl(String url) {
        if (StringUtils.hasText(url)) {
            try {
                String decodeUrl = URLDecoder.decode(url, "UTF-8");
                String replace = decodeUrl.replace(PREFIX, "");
                int i = replace.indexOf("?");
                if (i > 0) {
                    String substring = replace.substring(0, i);
                    return substring;
                }
            } catch (UnsupportedEncodingException e) {
                log.error("URL解码失败!");
            }
        }
        return url;
    }


    /**
     * 获取阿里云私有地址
     *
     * @param url
     * @return
     */
    public static String generateImageUrl(String url) {
        String urlEnd = null;
        try {
            urlEnd = AliyunUtil.getPrivateUrl(aliyunConfig, url);
        } catch (Exception e) {
            log.error("转化图片地址失败!");
        }
        return urlEnd;
    }

    /**
     * 验证资金密码
     *
     * @param moneyPassword 前端输入的资金密码
     * @param jyPassword    用户设置的资金密码
     */
    public static void validatePassword(String moneyPassword, String jyPassword, String salt) {
        AssertUtil.hasText(moneyPassword, CommonMsgCode.MISSING_JYPASSWORD);
        AssertUtil.hasText(jyPassword, CommonMsgCode.NO_SET_JYPASSWORD);
        String jyPass = new SimpleHash("md5", moneyPassword, salt, 2).toHex().toLowerCase();
        AssertUtil.isTrue(jyPassword.equals(jyPass), CommonMsgCode.ERROR_JYPASSWORD);
    }


    public static BigDecimal getChangeSectionAmount(Integer validCount) {
        Map<Integer, Integer> sectionPay = new TreeMap<>(payConfigProperties.getChangeSectionPay());
        Set<Map.Entry<Integer, Integer>> entries = sectionPay.entrySet();
        Integer amount = 0;
        for (Map.Entry<Integer,Integer> entry:entries){
            if(validCount<entry.getKey()){
                amount=entry.getValue();
                break;
            }
        }
        return new BigDecimal(amount);
    }

    public static BigDecimal getStreamAmount(Integer validCount) {
        Map<Integer, Integer> streamPay = new TreeMap<>(payConfigProperties.getStreamPay());
        Set<Map.Entry<Integer, Integer>> entries = streamPay.entrySet();
        Integer amount = 0;
        for (Map.Entry<Integer,Integer> entry:entries){
            if(validCount<entry.getKey()){
                amount=entry.getValue();
                break;
            }
        }
        return new BigDecimal(amount);
    }
}
