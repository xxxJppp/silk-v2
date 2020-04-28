package com.spark.bitrade.controller;

import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.constants.MsgCode;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.ExceptionUitl;
import com.spark.bitrade.util.HttpRequestUtil;
import com.spark.bitrade.util.MessageRespResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class CommonController {
    public <T> MessageRespResult<T> success(T data) {
        if (data instanceof Boolean && Boolean.FALSE.equals(data)) {
            return failed("操作失败");
        }
        return MessageRespResult.success4Data(data);
    }

    public <T> MessageRespResult<T> success() {
        return of(CommonMsgCode.SUCCESS);
    }

    public <T> MessageRespResult<T> failed() {
        return of(CommonMsgCode.FAILURE);
    }

    protected <T> MessageRespResult<T> failed(String msg) {
        return new MessageRespResult<>(500, msg);
    }

    public <T> MessageRespResult<T> failed(MsgCode code) {
        return of(code);
    }

    public <T> MessageRespResult<T> of(MsgCode code) {
        return new MessageRespResult<>(code.getCode(), code.getMessage());
    }

    public <T> MessageRespResult<T> of(MsgCode code, T data) {
        return new MessageRespResult<>(code.getCode(), code.getMessage(), data);
    }

    /**
     * 抛出 MessageCodeException异常
     *
     * @param msgCode 错误码
     */
    public void throwsMessageCodeException(MsgCode msgCode) {
        ExceptionUitl.throwsMessageCodeException(msgCode);
    }

    /**
     * 获取HttpServletRequest
     *
     * @return
     */
    public HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (null != requestAttributes) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        } else {
            this.throwsMessageCodeException(CommonMsgCode.GET_REQUEST_FAILED);
        }
        return null;
    }

    /**
     * 从请求头里获取apiKey
     *
     * @return
     */
    public String getApiKey() {
        String apiKey = HttpRequestUtil.getApiKey();
        AssertUtil.notNull(apiKey, CommonMsgCode.API_KEY_IS_NULL);
        return apiKey;
    }

    /**
     * 从请求头里获取appId
     *
     * @return
     */
    public String getAppId() {
        String appId = HttpRequestUtil.getAppId();
        AssertUtil.notNull(appId, CommonMsgCode.APP_ID_IS_NULL);
        return appId;
    }

    /**
     * 从请求头里获取apiTime
     *
     * @return
     */
    public String getApiTime() {
        return HttpRequestUtil.getApiTime();
    }

    /**
     * 从请求头里获取apiSign
     *
     * @return
     */
    public String getApiSign() {
        return HttpRequestUtil.getApiSign();
    }
}
