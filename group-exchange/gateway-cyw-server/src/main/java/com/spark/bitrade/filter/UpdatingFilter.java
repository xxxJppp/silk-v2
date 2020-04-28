package com.spark.bitrade.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/***
  * 升级过滤器
  * @author yangch
  * @time 2019-10-30 16:45:10
  */
@Component
@Slf4j
public class UpdatingFilter extends ZuulFilter {
    // 系统是否正在升级
    private static Boolean isUpdating = false;

    public void setIsUpdating(boolean flag) {
        isUpdating = flag;
    }


    @Override
    public String filterType() {
        // 前置过滤器
        return "pre";
    }

    @Override
    public int filterOrder() {
        // 优先级，数字越大，优先级越低
        return -5;
    }

    @Override
    public boolean shouldFilter() {
        // 是否执行该过滤器，true代表需要过滤
        return isUpdating;
    }

    @Override
    public Object run() {
        log.info("正在升级，禁止请求");
        RequestContext ctx = RequestContext.getCurrentContext();

        ctx.setSendZuulResponse(false);
        //请求被禁止
        ctx.setResponseStatusCode(HttpStatus.FORBIDDEN.value());
        ctx.setResponseBody("{\"data\":\"\",\"code\":999,\"message\":\"SYSTEM_IS_UPDATING\"}");
        ctx.getResponse().setContentType("application/json; charset=utf-8");

        return null;
    }

}
