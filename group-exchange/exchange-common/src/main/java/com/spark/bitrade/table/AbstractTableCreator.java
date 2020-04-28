package com.spark.bitrade.table;

import io.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * AbstractTableCreator
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/10/23 10:22
 */
@Slf4j
public abstract class AbstractTableCreator {

    private DataSource dataSource;

    @Autowired
    public void setDataSource(ShardingDataSource shardingDataSource) {
        String defaultDataSourceName = shardingDataSource.getShardingContext().getShardingRule().getShardingRuleConfig().getDefaultDataSourceName();
        this.dataSource = shardingDataSource.getDataSourceMap().get(defaultDataSourceName);
    }

    /**
     * 创建表
     *
     * @param table 表名称
     * @param sql   ddl
     */
    protected void doCreate(String table, String sql) {
        try (Connection connection = dataSource.getConnection()) {

            ResultSet tables = connection.getMetaData().getTables(null, null, table, null);

            if (tables.next()) {
                log.warn("Table '{}' already exist !!!", table);
                tables.close();
                return;
            }

            tables.close();

            // 创建表
            String ddl = String.format(sql, table);
            PreparedStatement statement = connection.prepareStatement(ddl);
            int execute = statement.executeUpdate();

            if (execute == 0) {
                log.info("Table '{}' create successful.", table);
            } else {
                log.error("Table '{}' create failed.", table);
            }

            statement.close();
        } catch (SQLException ex) {
            log.error("Table creator job execute error.", ex);
        }
    }

    /**
     * 获取明天的日期
     *
     * @return date
     */
    protected String getDatetimeStringOfTomorrow() {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DATE, 1);

        return new SimpleDateFormat("yyyyMMdd").format(instance.getTime());
    }

    /**
     * 获取当月的日期
     *
     * @return date
     */
    protected String getCurrentMonth() {
        Calendar instance = Calendar.getInstance();
        return new SimpleDateFormat("yyyyMM").format(instance.getTime());
    }
}
