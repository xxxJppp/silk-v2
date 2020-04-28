package com.spark.bitrade.config;

import com.google.common.base.Predicate;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

//http://localhost:ip/swagger-ui.html
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket createRestApi() {
//        Predicate<RequestHandler> predicate = input -> true;
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false)
                .select()
//                .apis(predicate)
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .build()
                .ignoredParameterTypes(MemberAccount.class)
                .pathMapping("/")
                .globalOperationParameters(setHeaderParameter());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                //大标题
                .title("API文档")
                //版本
                .version("1.0")
                .build();
    }

    /**
     * 设置请求头参数
     *
     * @return
     */
    private List<Parameter> setHeaderParameter() {
        List<Parameter> pars = new ArrayList<>();
        ParameterBuilder appIdPar = new ParameterBuilder();
        appIdPar.name("appId")
                .description("请求头：appId（提示：应用ID）")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                //.defaultValue(String.valueOf(System.currentTimeMillis()))
                .required(false)
                .build();
        pars.add(appIdPar.build());

        ParameterBuilder apiKeyPar = new ParameterBuilder();
        apiKeyPar.name("apiKey")
                .description("请求头：apiKey（提示：JWT token值）")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(false)
                .build();
        pars.add(apiKeyPar.build());

//
//        ParameterBuilder apiSignPar = new ParameterBuilder();
//        apiSignPar.name("apiSign")
//                .description("请求头：apiSign（提示：请求签名）")
//                .modelRef(new ModelRef("string"))
//                .parameterType("header")
//                .required(false)
//                .build();
//        pars.add(apiSignPar.build());

        return pars;
    }
}
