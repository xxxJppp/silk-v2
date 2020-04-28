package com.spark.bitrade.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.http.HttpServletRequestWrapper;
import com.netflix.zuul.http.ServletInputStreamWrapper;
import com.spark.bitrade.constants.MsgCodeEnum;
import com.spark.bitrade.jwt.HttpJwtToken;
import com.spark.bitrade.jwt.MemberClaim;
import com.spark.bitrade.service.SecurityService;
import com.spark.bitrade.util.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.nio.charset.Charset;


/**
 * 请求参数解密
 * 备注：对请求地址中包含“/no-auth/”关键字，不进行加密和签名处理
 *
 * @author wsy
 * @time 2019-4-30 16:14:36
 */
@Slf4j
@Component
public class RequestFilter extends ZuulFilter {

    @Value("${api.data.encrypt.enable:true}")
    private boolean encrypt;
    private Field requestField;
    private Field servletRequestField;
    @Autowired
    private SecurityService securityService;

    private class RequestWrapper extends HttpServletRequestWrapper {
        private int contentLength;
        private MediaType contentType;
        private HttpServletRequest request;
        private volatile byte[] contentData;

        RequestWrapper(HttpServletRequest request) {
            super(request);
            this.request = request;
        }

        @Override
        public String getContentType() {
            if (this.contentData == null) {
                this.buildContentData();
            }
            return contentType.toString();
        }

        @Override
        public ServletInputStream getInputStream() {
            if (this.contentData == null) {
                this.buildContentData();
            }
            return new ServletInputStreamWrapper(this.contentData);
        }

        @Override
        public int getContentLength() {
            if (super.getContentLength() <= 0) {
                return super.getContentLength();
            }
            if (this.contentData == null) {
                this.buildContentData();
            }
            return contentLength;
        }

        @Override
        public long getContentLengthLong() {
            return getContentLength();
        }

        private synchronized void buildContentData() {
            try {
                this.contentType = MediaType.valueOf(request.getContentType());
                String appId = request.getHeader("appId");
                String apiKey = request.getHeader("apiKey");
                String apiSign = request.getHeader("apiSign");
                String apiTime = request.getHeader("apiTime");
                long time = NumberUtils.toLong(apiTime, 0);
                Assert.notNull(appId, "appId is null");
                Assert.notNull(apiKey, "apiKey is null");
                Assert.isTrue(time > 0 && securityService.checkRequestAllowDiffTime(time), "apiTime invalid");

                InputStream in = request.getInputStream();
                String content = StreamUtils.copyToString(in, Charset.forName("UTF-8"));

                // application/x-www-form-urlencoded 解析键值对
                if (this.contentType.includes(MediaType.APPLICATION_FORM_URLENCODED) && content.contains("=")) {
                    content = content.substring(content.indexOf("=") + 1);
                }

                content = URLDecoder.decode(content, "UTF-8");
                log.info("request apiKey={}, data >>> {}", apiKey, content);


                //校验请求签名
                if (!securityService.isCheckSign(request.getRequestURI())) {
                    //解析JwtToken
                    MemberClaim memberClaim = HttpJwtToken.getInstance().verifyToken(apiKey);
                    AssertUtil.notNull(memberClaim, MsgCodeEnum.EXPIRED);

                    // 验签校验
                    Assert.isTrue(securityService.generateSign(appId, apiKey, memberClaim, time, content).equals(apiSign), "apiSign invalid");
                }

                // 请求数据解密
                String body = securityService.decryptData(apiKey, content);
                log.info("request uri={} , decryptData >>> {}", request.getRequestURI(), body);
                this.contentData = body.getBytes(Charset.forName("UTF-8"));
            } catch (Exception e) {
                request.setAttribute("api-request-auth-state", false);
                request.setAttribute("api-request-auth-message", e.getMessage());
                this.contentData = e.getMessage().getBytes(Charset.forName("UTF-8"));
                log.error("handle filter request params exception", e);
            } finally {
                this.contentLength = this.contentData.length;
            }
        }
    }

    public RequestFilter() {
        this.requestField = ReflectionUtils.findField(HttpServletRequestWrapper.class, "req", HttpServletRequest.class);
        this.servletRequestField = ReflectionUtils.findField(ServletRequestWrapper.class, "request", ServletRequest.class);
        Assert.notNull(this.requestField, "HttpServletRequestWrapper.req field not found");
        Assert.notNull(this.servletRequestField, "ServletRequestWrapper.request field not found");
        this.requestField.setAccessible(true);
        this.servletRequestField.setAccessible(true);
    }

    @Override
    public boolean shouldFilter() {
        // GET 方法不过滤
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        String contentType = request.getContentType();

        if (securityService.isCheckSign(request.getRequestURI())) {
            log.info("不加解密");
            return false;
        }

        if (securityService.requiredEncrypt(request.getRequestURL().toString())) {
            if (encrypt && contentType != null) {
                MediaType mediaType = MediaType.valueOf(contentType);
                return MediaType.APPLICATION_FORM_URLENCODED.includes(mediaType)
                        || (isDispatcherServletRequest(request)
                        && MediaType.APPLICATION_JSON.includes(mediaType));
            } else {
                return false;
            }
        } else {
            //不需要加密
            log.info("unEncrypt: request {} >>> {}", request.getMethod(), request.getRequestURL().toString());
            return false;
        }
    }

    private boolean isDispatcherServletRequest(HttpServletRequest request) {
        return request.getAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE) != null;
    }

    @Override
    public int filterOrder() {
        // 优先级为0，数字越大，优先级越低
        return FilterConstants.FORM_BODY_WRAPPER_FILTER_ORDER;
    }

    @Override
    public String filterType() {
        // 请求被路由之前调用
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String method = request.getMethod();
        log.info("request {} >>> {}", method, request.getRequestURL().toString());
        RequestWrapper wrapper;
        if (request instanceof HttpServletRequestWrapper) {
            HttpServletRequest wrapped = (HttpServletRequest) ReflectionUtils.getField(this.requestField, request);
            wrapper = new RequestWrapper(wrapped);
            ReflectionUtils.setField(this.requestField, request, wrapper);
            ReflectionUtils.setField(this.servletRequestField, request, wrapper);
        } else {
            wrapper = new RequestWrapper(request);
            ctx.setRequest(wrapper);
        }
        ctx.getZuulRequestHeaders().put("content-type", wrapper.getContentType());
        return null;
    }
}
