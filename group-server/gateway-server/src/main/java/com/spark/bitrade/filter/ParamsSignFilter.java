package com.spark.bitrade.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.spark.bitrade.service.SecurityService;
import com.spark.bitrade.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
public class ParamsSignFilter extends ZuulFilter {

    @Value("${api.data.encrypt.enable:true}")
    private boolean encrypt;
    @Autowired
    private SecurityService securityService;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.FORM_BODY_WRAPPER_FILTER_ORDER + 1;
    }

    @Override
    public boolean shouldFilter() {
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        if (securityService.isCheckSign(request.getRequestURI())) {
            log.info("请求不需要解密");
            return false;
        }
        if (securityService.requiredEncrypt(request.getRequestURL().toString())) {
            return encrypt;
        } else {
            //不需要加密
            ///log.info("unEncrypt:2 request {} >>> {}", request.getMethod(), request.getRequestURL().toString());
            return false;
        }
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        Object state = request.getAttribute("api-request-auth-state");
        Object message = request.getAttribute("api-request-auth-message");
        log.info("api request auth: state = {}, message = {}", state, message);
        if (state != null && !(Boolean) state) {
            MessageResult result = MessageResult.error(401, String.valueOf(message));
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(401);
            ctx.setResponseBody(JSON.toJSONString(result));
        }
        return null;
    }
}
