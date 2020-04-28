package com.spark.bitrade.controller;

import com.spark.bitrade.util.MessageResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/***
  * 
  * @author yangch
  * @time 2018.05.26 17:08
  */

@RestController
//@RequestMapping("api/v2")
public class HealthyController {

    @Value("${test.jasypt:}")
    private String testJasypt;

    @Value("${test.config.center:}")
    private String testConfigCenter;

    /***
      * 负载均衡健康检查接口
      * @author yangch
      * @time 2018.05.26 17:04 
      */
    @RequestMapping("healthy")
    public MessageResult healthy() {
        return MessageResult.success();
    }
    @RequestMapping("api/v2/healthy")
    public MessageResult healthy2() {
        return MessageResult.success();
    }

    @RequestMapping("/sleep/{sleepTime}")
    public String sleep(@PathVariable Long sleepTime) throws InterruptedException {
        //超时测试接口
        TimeUnit.SECONDS.sleep(sleepTime);
        return "SUCCESS";
    }

    /***
      * 测试解密和配置中心
      * @time 2018.05.26 17:04 
      */
    @RequestMapping("test")
    public MessageResult test() {
        return MessageResult.success("ok",
                "testJasypt=" + testJasypt + ",testConfigCenter=" + testConfigCenter);
    }
}
