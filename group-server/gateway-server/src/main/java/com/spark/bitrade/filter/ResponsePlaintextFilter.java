package com.spark.bitrade.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.context.RequestContext;
import com.spark.bitrade.service.LocaleMessageSourceService;
import com.spark.bitrade.service.SecurityService;
import com.spark.bitrade.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * 对不需要加密和签名的返回结果进行国际化的处理
 *
 * @author yangch
 * @time 2019-07-10 18:43:13
 */
@Slf4j
@Component
public class ResponsePlaintextFilter extends BaseZuulFilter {

    @Value("${api.data.encrypt.enable:true}")
    private boolean encrypt;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private LocaleMessageSourceService msService;

    @Override
    public String filterType() {
        // 在route和error过滤器之后被调用
        return FilterConstants.POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.SEND_RESPONSE_FILTER_ORDER - 1;
    }

    @Override
    public boolean shouldFilter() {
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        if (securityService.isCheckSign(request.getRequestURI())) {
            log.info("响应数据不需要加密");
            return true;
        }
        if (!securityService.requiredEncrypt(request.getRequestURL().toString())) {
            //不需要加密
            log.info("unEncrypt:2 response {} >>> {}", request.getMethod(), request.getRequestURL().toString());
            return encrypt;
        } else {
            return false;
        }
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();
        String responseBody = null;
        try {
            InputStream stream = context.getResponseDataStream();
            context.getResponse().setHeader("ciphertext", "false");
            String oldBody = StreamUtils.copyToString(stream, Charset.forName("UTF-8"));
            log.info("oldBody:{}", oldBody);

            //国际化处理业务编码
            if (StringUtils.hasText(oldBody)) {
                MessageResult result = JSON.parseObject(oldBody, MessageResult.class);
                if (result == null || result.isSuccess()) {
                    responseBody = oldBody;
                } else {
                    //国际化 处理 错误提示
                    String msg = msService.getMessage(String.valueOf(result.getCode()));
                    if (StringUtils.isEmpty(msg)) {
                        responseBody = oldBody;
                    } else {
                        // 错误时 根据返回的data数据进行消息的格式
                        result.setMessage(getMessageFormat(result, msg));
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
