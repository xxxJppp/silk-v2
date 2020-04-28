package com.spark.bitrade.validate;

import com.spark.bitrade.service.ExchangeOrderValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * ExchangeOrderValidatorTests
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/29 16:20
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// 让 JUnit 运行 Spring 的测试环境， 获得 Spring 环境的上下文的支持
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class ExchangeOrderValidatorTests {

    @Autowired
    private ExchangeOrderValidator orderValidator;

    @Test
    public void testBuyOrder() {
        String orderId = "S232835589093720064_BTCUSDT";
        orderValidator.validate(orderId);
    }

    @Test
    public void testSellOrder() {
        String orderId = "S232835760372318208_BTCUSDT";
        orderValidator.validate(orderId);
    }

    @Test
    public void testCancelOrder() {
        String orderId = "S232796031492816898_BTCUSDT";
        orderValidator.validate(orderId);
    }
}
