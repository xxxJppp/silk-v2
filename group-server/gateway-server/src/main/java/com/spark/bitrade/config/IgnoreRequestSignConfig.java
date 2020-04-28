package com.spark.bitrade.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/***
 * 忽略签名校验的uri配置
 *
 * @author young
 * @time 2019-05-08 11:06:34
 */

@RefreshScope
@Component
@ConfigurationProperties(prefix="sign.requestUri")
@Data
public class IgnoreRequestSignConfig {
    //忽略签名的配置
    private List<String> ignoreList = new ArrayList<>();

}
