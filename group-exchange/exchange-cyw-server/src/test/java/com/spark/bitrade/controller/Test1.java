package com.spark.bitrade.controller;

import com.spark.bitrade.service.CywWalletSnapshootService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 *  
 *
 * @author young
 * @time 2019.09.25 19:13
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// 让 JUnit 运行 Spring 的测试环境， 获得 Spring 环境的上下文的支持
@RunWith(SpringRunner.class)
public class Test1 {
    @Autowired
    private CywWalletSnapshootService cywWalletSnapshootService;

    @Test
    public void snapshootAll() {
        cywWalletSnapshootService.snapshootAll(new Date());
    }
}
