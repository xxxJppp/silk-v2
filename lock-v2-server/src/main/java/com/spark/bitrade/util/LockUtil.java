package com.spark.bitrade.util;

import com.spark.bitrade.constants.CommonMsgCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.08.30 11:57  
 */
@Slf4j
public class LockUtil {

    private LockUtil(){}
    public static final String PREFIX = "http://silktraderpriv.oss-cn-hongkong.aliyuncs.com/";
    /**
     * 验证资金密码
     * @param moneyPassword  前端传入的资金密码
     * @param jyPassword 资金密码
     * @param salt 盐
     */
    public static void validateJyPassword(String moneyPassword, String jyPassword, String salt){
        AssertUtil.hasText(moneyPassword, CommonMsgCode.MISSING_JYPASSWORD);
        AssertUtil.hasText(jyPassword, CommonMsgCode.NO_SET_JYPASSWORD);
        String jyPass = new SimpleHash("md5", moneyPassword, salt, 2).toHex().toLowerCase();
        AssertUtil.isTrue(jyPassword.equals(jyPass), CommonMsgCode.ERROR_JYPASSWORD);
    }


    /**
     * 解码url
     * @param url
     * @return
     */
    public static String decodeUrl(String url){
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
}
