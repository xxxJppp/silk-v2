package com.spark.bitrade;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.spark.bitrade.dsc.DscContext;
import com.spark.bitrade.entity.ExchangeWallet;
import com.spark.bitrade.service.ExchangeWalletService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * WalletSignTest
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/11/22 9:21
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// 让 JUnit 运行 Spring 的测试环境， 获得 Spring 环境的上下文的支持
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class WalletSignTest {

    @Autowired
    private DscContext dscContext;

    @Autowired
    private ExchangeWalletService walletService;

    @Test
    public void resign() {
        String pk = "360557:BT"; // 360557:BT
        ExchangeWallet wallet = walletService.getById(pk);
        if (wallet != null) {
            dscContext.getDscEntityResolver(wallet).update();

            UpdateWrapper<ExchangeWallet> update = new UpdateWrapper<>();
            update.eq("id", pk).set("signature", wallet.getSignature());

            walletService.update(update);
        }
    }
}
