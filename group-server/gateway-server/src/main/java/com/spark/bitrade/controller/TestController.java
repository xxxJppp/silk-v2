//package com.spark.bitrade.controller;
//
//import com.spark.bitrade.service.ApiSecretKeyApiService;
//import com.spark.bitrade.service.SecurityService;
//import com.spark.bitrade.util.DateUtil;
//import com.spark.bitrade.util.DesECBUtil;
//import com.spark.bitrade.util.MD5Util;
//import com.spark.bitrade.util.MessageResult;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.concurrent.TimeUnit;
//
///***
//  * 
//  * @author yangch
//  * @time 2018.05.26 17:08
//  */
//
//@RestController
//public class TestController {
//    @Autowired
//    private ApiSecretKeyApiService apiSecretKeyApiService;
//    @Autowired
//    private SecurityService securityService;
//
//
//    /***
//      * 负载均衡健康检查接口
//      * @author yangch
//      * @time 2018.05.26 17:04 
//      */
//    @RequestMapping("healthy2")
//    public MessageResult healthy(String s, String m) throws Exception {
////        return MessageResult.success("test",apiSecretKeyApiService.apiSecretSalt().getData().toString());
//        return MessageResult.success("test", securityService.encryptData(s, m));
//    }
//
//    /***
//      * 负载均衡健康检查接口
//      * @author yangch
//      * @time 2018.05.26 17:04 
//      */
//    @RequestMapping("healthy3")
//    public MessageResult healthy3(String s, String m) throws Exception {
////        return MessageResult.success("test",apiSecretKeyApiService.apiSecretSalt().getData().toString());
//        return MessageResult.success("test", securityService.decryptData(s, m));
//    }
//
//    @RequestMapping("healthy4")
//    public MessageResult healthy4(String s, String m, Long t) throws Exception {
////        return MessageResult.success("test",apiSecretKeyApiService.apiSecretSalt().getData().toString());
//        return MessageResult.success("test", securityService.generateSign(s, t, m));
//    }
//
//
//}
