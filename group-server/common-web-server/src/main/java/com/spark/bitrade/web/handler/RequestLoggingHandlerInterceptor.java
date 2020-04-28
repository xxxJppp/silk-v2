package com.spark.bitrade.web.handler;

import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.service.MemberAccountService;
import com.spark.bitrade.web.adapter.MemberAccountGetterAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * RequestLoggingHandlerInterceptor
 *
 * @author archx
 * @since 2019/6/3 20:03
 */
@Slf4j
public class RequestLoggingHandlerInterceptor extends MemberAccountGetterAdapter
        implements HandlerInterceptor, ApplicationContextAware {

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @Autowired
    @Override
    public void setMemberAccountService(MemberAccountService memberAccountService) {
        super.setMemberAccountService(memberAccountService);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod && notProdEnvironment()) {
            HandlerMethod method = (HandlerMethod) handler;

            String requestURI = request.getRequestURI();
            String apiKey = request.getHeader("apiKey");
            String queryString = buildQueryString(request);
            String uid = "unknown";

            try {
                if (StringUtils.hasText(apiKey)) {
                    uid = String.valueOf(getCurrentMember(apiKey).getId());
                }
            } catch (MessageCodeException ex) {
                // ignore
            }
            log.info("\nrequest => '{}', uid = {}, key = '{}'\nmethod  => '{}', query => '{}'\n",
                    requestURI, uid, apiKey, method.getMethod().getName(), queryString);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }

    private boolean notProdEnvironment() {
        return !"prod".equals(context.getEnvironment().getActiveProfiles()[0]);
    }

    private String buildQueryString(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder();
        request.getParameterMap().forEach((k, v) -> {
            builder.append(k).append("=").append(v[0]).append("&");
        });

        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }
}
