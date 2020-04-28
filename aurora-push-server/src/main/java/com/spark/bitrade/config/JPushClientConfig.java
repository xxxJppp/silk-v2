package com.spark.bitrade.config;

import cn.jiguang.common.ClientConfig;
import cn.jpush.api.JPushClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author daring5920
 * @time 2019.05.22 17:19
 */
@Component
public class JPushClientConfig {

    @Bean
    public JPushClient jPushClient(@Value("${jiguang.AppKey}") String appKey,@Value("${jiguang.MasterSecret}") String masterSecret){

        ClientConfig config = ClientConfig.getInstance();
        config.setMaxRetryTimes(5);
        config.setConnectionTimeout(10 * 1000);	// 10 seconds
        // config.setSSLVersion("TLSv1.1");		// JPush server supports SSLv3, TLSv1, TLSv1.1, TLSv1.2

        return new JPushClient(masterSecret, appKey, null, config);

    }
}
