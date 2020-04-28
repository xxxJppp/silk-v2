package com.spark.bitrade.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author wsy
 * @since 2019/7/18 10:17
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.mqtt")
public class MQTTProperties {

    // 节点
    private String node;
    // 登录用户名
    private String username;
    // 登录密码
    private String password;
    private String dashboardPassword;
    private String serverUri;
    private String producerClientId;
    private String producerDefaultTopic;
    private String consumerClientId;
    private String consumerDefaultTopic;
    // 客户端订阅规则
    private String[] clientSubAcl;
    // 客户端发布规则
    private String[] clientPubAcl;
    // API地址
    private String restApi;
    // AppID
    private String restAppId;
    // App秘钥
    private String restAppKey;

}
