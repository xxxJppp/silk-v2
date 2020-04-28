package com.spark.bitrade.job;

import com.spark.bitrade.table.AbstractTableCreator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * ArchiveTableCreatorJob
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/26 17:59
 */

@Component
public class ArchiveTableCreatorJob extends AbstractTableCreator {

    /*
     * 每天凌晨创建第二天的表
     */
    @Scheduled(cron = "0 0 1 * * *")
    public void schedule() {
        // 创建WAL日志记录
        createWalRecordHistory();
        // 创建订单记录
        createExchangeCywOrderHistory();
    }

    /**
     * 创建明天的WAL记录表
     */
    private void createWalRecordHistory() {
        String table = "cyw_wallet_wal_record_his_" + getDatetimeStringOfTomorrow();
        String sql = "CREATE TABLE `%s` like cyw_wallet_wal_record";
        doCreate(table, sql);
    }

    /**
     * 创建当月的订单记录表
     */
    private void createExchangeCywOrderHistory() {
        String table = "exchange_cyw_order_his_" + getCurrentMonth();
        String sql = "CREATE TABLE `%s` like exchange_cyw_order";
        doCreate(table, sql);
    }
}
