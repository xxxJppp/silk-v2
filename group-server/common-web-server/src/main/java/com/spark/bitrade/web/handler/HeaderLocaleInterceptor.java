package com.spark.bitrade.web.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author wsy
 * @since 2019/8/28 11:23
 */
@Slf4j
public class HeaderLocaleInterceptor extends LocaleChangeInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String accept = request.getHeader("Accept-Language");
        String locale = accept != null && accept.matches("en[-_]US") ? "en_US" : "zh_CN";
        log.debug("====> locale: {}", locale);
        try {
            LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
            localeResolver.setLocale(request, response, parseLocaleValue(locale));
        } catch (IllegalArgumentException ex) {
            logger.debug("Ignoring invalid locale value [" + locale + "]: " + ex.getMessage());
        }
        return true;
    }
}
