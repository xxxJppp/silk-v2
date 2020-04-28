package com.spark.bitrade.wal;

import com.spark.bitrade.mapper.ExchangeWalletWalExtMapper;
import com.spark.bitrade.mapper.dto.WalRecordDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * WalRecordTests
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/26 13:45
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// 让 JUnit 运行 Spring 的测试环境， 获得 Spring 环境的上下文的支持
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class WalRecordTests {

    @Autowired
    private ExchangeWalletWalExtMapper walExtMapper;

    @Test
    public void testSumQuery() throws SQLException {

    }

    @Test
    public void testQuery0() {
        Date date = from("2019-09-26 00:00:00");
        long now = System.currentTimeMillis();
        for (int i = 0; i < 8; i++) {
            List<WalRecordDto> dtos = walExtMapper.queryForArchive("exchange_wallet_wal_record_" + i, date);
            System.out.println(i + " -> " + dtos.size());
        }

        System.out.println("execute seconds -> " + ((System.currentTimeMillis() - now) / 1000));
    }

    @Test
    public void testQuery() {
//        Date date = from("2019-09-25 00:00:00");
        long now = System.currentTimeMillis();
        List<WalRecordDto> query = walExtMapper.queryByRefId("S232796031484428288_BTCUSDT");
        System.out.println("size -> " + query.size());
        System.out.println("execute seconds -> " + ((System.currentTimeMillis() - now) / 1000));
        query.forEach(System.out::println);
    }

    private Date from(String string) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

}
