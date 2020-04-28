package com.spark.bitrade.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spark.bitrade.util.OrdinalToEnumConverterFactory;
import com.spark.bitrade.web.bind.method.MemberAccountMethodArgumentResolver;
import com.spark.bitrade.web.handler.RequestLoggingHandlerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * 通用应用配置
 *
 * @author archx
 * @since 2019/5/8 18:05
 */
@Configuration
public class ApplicationConfig extends WebMvcConfigurerAdapter {

    @Bean(name = "memberAccountMethodArgumentResolver")
    public MemberAccountMethodArgumentResolver memberAccountMethodArgumentResolver() {
        return new MemberAccountMethodArgumentResolver();
    }

    // 自定义注解
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(memberAccountMethodArgumentResolver());
    }

    @Bean(name = "requestLoggingHandlerInterceptor")
    public RequestLoggingHandlerInterceptor requestLoggingHandlerInterceptor() {
        return new RequestLoggingHandlerInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration registration = registry.addInterceptor(requestLoggingHandlerInterceptor());
        registration.addPathPatterns("/**");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new OrdinalToEnumConverterFactory());
        super.addFormatters(registry);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //https://blog.csdn.net/L_Sail/article/details/70217393

        //将输出的long转换为string
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = jackson2HttpMessageConverter.getObjectMapper();
        //不显示为null的字段
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);

        objectMapper.registerModule(simpleModule);

        jackson2HttpMessageConverter.setObjectMapper(objectMapper);
        //放到第一个
        converters.add(0, jackson2HttpMessageConverter);
    }
}
