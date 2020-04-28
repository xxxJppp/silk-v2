package com.spark.bitrade.sql;

import com.spark.bitrade.entity.dto.WalletSyncCountDto;
import com.spark.bitrade.service.ExchangeWalletWalRecordService;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * SQLExecuteTests
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/9 17:32
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// 让 JUnit 运行 Spring 的测试环境， 获得 Spring 环境的上下文的支持
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class SQLExecuteTests {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private ExchangeWalletWalRecordService walRecordService;

    @Test
    public void testQuery() throws SQLException {
        String sql = "SELECT \n" +
                "  SUM(r.`trade_balance`) amount,\n" +
                "  SUM(\n" +
                "    CASE\n" +
                "      WHEN r.`trade_balance` > 0 \n" +
                "      THEN r.`trade_balance` \n" +
                "      ELSE 0 \n" +
                "    END\n" +
                "  ) increment,\n" +
                "  SUM(r.`trade_frozen`) frozen \n" +
                "FROM\n" +
                "  `exchange_wallet_wal_record` r \n" +
                "WHERE r.`sync_id` = 0 ";

        SqlSession sqlSession = sqlSessionFactory.openSession();
        Connection connection = sqlSession.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            Object object = resultSet.getObject(1);
            System.out.println(object);
        }

        sqlSession.close();
    }

    @Test
    public void testQuery2() {
        WalletSyncCountDto dto = walRecordService.sumSyncId(0L);
        System.out.println(dto);
    }
}
