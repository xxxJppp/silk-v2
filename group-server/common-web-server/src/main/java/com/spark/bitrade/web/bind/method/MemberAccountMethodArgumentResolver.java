package com.spark.bitrade.web.bind.method;

import com.spark.bitrade.service.MemberAccountService;
import com.spark.bitrade.web.adapter.MemberAccountGetterAdapter;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * MemberAccountMethodArgumentResolver
 *
 * @author archx
 * @since 2019/5/8 17:55
 */
public class MemberAccountMethodArgumentResolver extends MemberAccountGetterAdapter implements HandlerMethodArgumentResolver {

    @Autowired
    public void setMemberAccountService(MemberAccountService memberAccountService) {
        this.memberAccountService = memberAccountService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(MemberAccount.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        MemberAccount mc = parameter.getParameterAnnotation(MemberAccount.class);

        HttpServletRequest req = (HttpServletRequest) webRequest.getNativeRequest();

        String apiKey = req.getHeader(mc.value());

        return getCurrentMember(apiKey);
    }
}
