package com.spark.bitrade.util;

import com.spark.bitrade.constants.CommonMsgCode;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 *  HttpRequest工具类
 *
 * @author young
 * @time 2019.07.18 19:26
 */
public class HttpRequestUtil {

    /**
     * 获取HttpServletRequest
     *
     * @return
     */
    public static HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (null != requestAttributes) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        } else {
            ExceptionUitl.throwsMessageCodeException(CommonMsgCode.GET_REQUEST_FAILED);
        }
        return null;
    }

    /**
     * 从请求头里获取apiKey
     *
     * @return
     */
    public static String getApiKey() {
        HttpServletRequest request = getHttpServletRequest();
        return request.getHeader("apiKey");
    }

    /**
     * 从请求头里获取appId
     *
     * @return
     */
    public static String getAppId() {
        HttpServletRequest request = getHttpServletRequest();
        return request.getHeader("appId");
    }

    /**
     * 从请求头里获取apiTime
     *
     * @return
     */
    public static String getApiTime() {
        HttpServletRequest request = getHttpServletRequest();
        return request.getHeader("apiTime");
    }

    /**
     * 从请求头里获取apiSign
     *
     * @return
     */
    public static String getApiSign() {
        HttpServletRequest request = getHttpServletRequest();
        return request.getHeader("apiSign");
    }
}
