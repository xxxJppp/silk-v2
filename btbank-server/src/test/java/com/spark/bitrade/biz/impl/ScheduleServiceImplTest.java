package com.spark.bitrade.biz.impl;

import com.spark.bitrade.biz.ScheduleService;
import com.spark.bitrade.config.TestDbConfig;
import com.spark.bitrade.config.TestMyBatisPlusConfig;
import com.spark.bitrade.constant.BtBankSystemConfig;
import com.spark.bitrade.service.BtBankConfigService;
import com.spark.bitrade.service.MemberWalletService;
import com.spark.bitrade.util.IdWorkByTwitter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestDbConfig.class, TestMyBatisPlusConfig.class})
public class ScheduleServiceImplTest {
    @Autowired
    ScheduleService scheduleService;

    @MockBean
    IdWorkByTwitter idWorkByTwitter;

    @MockBean
    BtBankConfigService configService;

    @MockBean
    RedisTemplate redisTemplate;

    @MockBean
    MemberWalletService memberWalletService;

//    @Test
//    public void testDispatchOrder() {
//        scheduleService.autoDispatch();
//    }

    @Test
    public void testUnlockAsset() {
        // given
        BDDMockito.given(configService.getConfig(BtBankSystemConfig.UNLOCK_TIME)).willReturn("2880");
        // when
        scheduleService.unLockAssert();

        // then
    }
}