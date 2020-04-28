package com.spark.bitrade.wallet;

import com.spark.bitrade.entity.CywWalletSyncRecord;
import com.spark.bitrade.service.CywWalletOperations;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

/**
 * WalletOperationsTests
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/11 9:50
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// 让 JUnit 运行 Spring 的测试环境， 获得 Spring 环境的上下文的支持
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class WalletOperationsTests {

    @Autowired
    private CywWalletOperations walletOperations;

    @Test
    public void testSync() {
        Optional<CywWalletSyncRecord> slp = walletOperations.sync(1L, "SLP");
        System.out.println(slp.get());
    }
}
