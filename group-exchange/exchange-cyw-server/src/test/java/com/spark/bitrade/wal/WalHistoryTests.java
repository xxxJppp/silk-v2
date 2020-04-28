package com.spark.bitrade.wal;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.spark.bitrade.entity.CywWalletWalRecord;
import com.spark.bitrade.mapper.CywWalletWalExtMapper;
import com.spark.bitrade.service.CywWalletWalRecordService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * WalHistoryTests
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/26 18:07
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// 让 JUnit 运行 Spring 的测试环境， 获得 Spring 环境的上下文的支持
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class WalHistoryTests {

    @Autowired
    @Qualifier("dataSource")
    private
    DataSource dataSource;

    @Autowired
    private CywWalletWalExtMapper extMapper;

    @Autowired
    private CywWalletWalRecordService recordService;

    private List<CywWalletWalRecord> list;

    @Test
    public void testCreateTable() throws SQLException {
        String sql = "CREATE TABLE `cyw_wallet_wal_record_his_%s` (\n" +
                "  `id` bigint(20) NOT NULL COMMENT 'ID',\n" +
                "  `member_id` bigint(20) NOT NULL COMMENT '用户ID',\n" +
                "  `coin_unit` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '币种单位',\n" +
                "  `trade_balance` decimal(24,8) NOT NULL DEFAULT '0.00000000' COMMENT '变动的可用余额',\n" +
                "  `trade_frozen` decimal(24,8) DEFAULT '0.00000000' COMMENT '变动的冻结余额',\n" +
                "  `fee` decimal(24,8) DEFAULT '0.00000000' COMMENT '交易手续费',\n" +
                "  `fee_discount` decimal(24,8) DEFAULT '0.00000000' COMMENT '优惠手续费',\n" +
                "  `trade_type` int(11) NOT NULL COMMENT '交易类型',\n" +
                "  `ref_id` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '关联的业务ID',\n" +
                "  `sync_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '同步ID',\n" +
                "  `signature` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '签名',\n" +
                "  `status` int(11) NOT NULL DEFAULT '0' COMMENT '状态：0=未处理，1=已处理',\n" +
                "  `tcc_status` int(11) NOT NULL DEFAULT '0' COMMENT 'tcc状态：0=none，1=try，2=confirm，3=cancel',\n" +
                "  `trade_status` int(11) NOT NULL DEFAULT '0' COMMENT '交易状态：0=trading, 1=partial, 2=complete, 3=cancel',\n" +
                "  `remark` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '备注',\n" +
                "  `create_time` datetime NOT NULL COMMENT '创建时间',\n" +
                "  `update_time` datetime DEFAULT NULL COMMENT '更新时间',\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  KEY `index_cyw_wallet_wal_record_smc` (`sync_id`,`member_id`,`coin_unit`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='机器人账户WAL流水记录表'\n";

        String schema = String.format(sql, "20190926");

        Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(schema);
        int execute = statement.executeUpdate();

        statement.close();
        connection.close();
        System.out.println("execute -> " + execute);
    }

    @Test
    public void testTableExists() throws SQLException {
        String table = "cyw_wallet_sync_record_his_201909261";

        Connection connection = dataSource.getConnection();
        ResultSet tables = connection.getMetaData().getTables(null, null, table, null);

        while (tables.next()) {
            System.out.println(tables.getString(1));
        }
    }

    @Before
    public void testQuery() {
        QueryWrapper<CywWalletWalRecord> query = new QueryWrapper<>();
        query.eq("ref_id","S232796030972723200_BTCUSDT");
        this.list = recordService.list(query);
    }

    @Test
    public void testSaveBatch() {
        extMapper.saveBatch("cyw_wallet_wal_record_his_20190926", this.list);
    }

    @Test
    public void testTransfer() {
        recordService.transfer("S232796030972723202_BTCUSDT");
    }
}
