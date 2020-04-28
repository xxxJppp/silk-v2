package com.spark.bitrade.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.spark.bitrade.service.LocaleMessageSourceService;
import com.spark.bitrade.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * 返回结果加密
 *
 * @author wsy
 * @time 2019-4-30 16:14:36
 */
@Slf4j
@Component
public class ResponseFilter extends ZuulFilter {
    @Autowired
    private LocaleMessageSourceService msService;

    @Override
    public String filterType() {
        // 在route和error过滤器之后被调用
        return FilterConstants.POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.SEND_RESPONSE_FILTER_ORDER - 2;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();
        String responseBody = null;
        try {
            InputStream stream = context.getResponseDataStream();
            responseBody = StreamUtils.copyToString(stream, Charset.forName("UTF-8"));
            log.info("oldBody:{}", responseBody);

            //国际化处理业务编码
            if (StringUtils.hasText(responseBody)) {
                MessageResult result = JSON.parseObject(responseBody, MessageResult.class);
                if (result != null && !result.isSuccess()) {
                    //国际化 处理 错误提示
                    String msg = msService.getMessage(String.valueOf(result.getCode()));
                    if (!StringUtils.isEmpty(msg)) {
                        result.setMessage(msg);
                        responseBody = JSON.toJSONString(result);
                    }
                }
            }
        } catch (Exception e) {
            log.error("handle filter response params exception: {}", e.getMessage());
            MessageResult result = MessageResult.error(500, "gateway error");
            responseBody = JSON.toJSONString(result);
        } finally {
            // 回传
            if (context.getResponseStatusCode() == 401) {
                //解决前端 拦截不到401 的情况
                context.setResponseStatusCode(200);
            }

            context.getResponse().setCharacterEncoding("UTF-8");
            context.setResponseBody(responseBody);
        }
        return null;
    }
}
