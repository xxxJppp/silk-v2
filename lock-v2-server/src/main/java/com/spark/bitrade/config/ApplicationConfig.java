//package com.spark.bitrade.config;
//
//import com.spark.bitrade.ext.OrdinalToEnumConverterFactory;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.format.FormatterRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
//
///**
// * @author Administrator
// */
//@Configuration
//public class ApplicationConfig extends WebMvcConfigurerAdapter {
//    //edit by yangch 时间： 2018.07.06 原因：api网关层处理，重复处理会报跨域问题
//    /*
//    @Bean
//    public FilterRegistrationBean corsFilter() {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration config = new CorsConfiguration();
//        config.addAllowedOrigin("*");
//        config.setAllowCredentials(true);
//        config.addAllowedHeader("*");
//        config.addAllowedMethod("*");
//        source.registerCorsConfiguration("/**", config);
//        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
//        bean.setOrder(0);
//        return bean;
//    }*/
//
//
//    @Override
//    public void addFormatters(FormatterRegistry registry) {
//        registry.addConverterFactory(new OrdinalToEnumConverterFactory());
//        super.addFormatters(registry);
//    }
//
//}
