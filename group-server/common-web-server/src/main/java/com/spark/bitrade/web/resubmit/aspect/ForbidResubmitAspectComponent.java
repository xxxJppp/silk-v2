package com.spark.bitrade.web.resubmit.aspect;

import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.HttpRequestUtil;
import com.spark.bitrade.util.MD5Util;
import com.spark.bitrade.web.resubmit.IForbidResubmit;
import com.spark.bitrade.web.resubmit.annotation.ForbidResubmit;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 *  禁止重复提交拦截器
 * 备注：基于请求的apiKey+URI现实的重复提交，重复提交的有效阻断时间默认为5秒
 *
 * @author young
 * @time 2019.07.18 19:08
 */
@Component
@Aspect
@Slf4j
public class ForbidResubmitAspectComponent {

    private IForbidResubmit iforbidResubmit;

    /**
     * 切入点
     */
    @Pointcut("@annotation(com.spark.bitrade.web.resubmit.annotation.ForbidResubmit)")
    public void validPointcut() {
    }

    @Before(value = "validPointcut() && @annotation(forbidResubmit)", argNames = "jp,forbidResubmit")
    public void beforeAdvice(JoinPoint jp, ForbidResubmit forbidResubmit) {
        StringBuilder key = new StringBuilder("resubmit:");
        key.append(MD5Util.md5Encode(Objects.toString(HttpRequestUtil.getApiKey(), "")))
                .append(":").append(HttpRequestUtil.getHttpServletRequest().getRequestURI());

        if (forbidResubmit.value()) {
            log.info("验证重复提交，key >>> {}", key);
            AssertUtil.isTrue(iforbidResubmit.validate(key.toString(), forbidResubmit.interdictTime()),
                    CommonMsgCode.FORBID_RESUBMIT);
        } else {
            log.info("禁用重复提交验证，key >>> {}", key);
        }
    }

    @Autowired
    public void setIforbidResubmit(IForbidResubmit iforbidResubmit) {
        this.iforbidResubmit = iforbidResubmit;
    }
}
