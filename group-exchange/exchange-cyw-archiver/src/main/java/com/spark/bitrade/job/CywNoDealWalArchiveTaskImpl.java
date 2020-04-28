package com.spark.bitrade.job;

import com.spark.bitrade.entity.CywWalletWalRecord;
import com.spark.bitrade.job.util.ArrayPage;
import com.spark.bitrade.job.util.DateUtils;
import com.spark.bitrade.mapper.CywWalArchiveMapper;
import com.spark.bitrade.trans.Tuple2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CYW未成交流水归档
 *
 * @author Pikachu
 * @since 2019/11/4 15:41
 */
@Slf4j
@Component
public class CywNoDealWalArchiveTaskImpl extends ArchiveTaskAdapter {

    private CywWalArchiveMapper cywWalArchiveMapper;

    /**
     * 表名
     */
    private String table = "cyw_wallet_wal_record";
    /**
     * 分片数量
     */
    private int sharding = 8;

    private volatile boolean delayed = false;

    @Override
    public void execute() {
        // 昨日之前
        Tuple2<Date, Date> yesterday = DateUtils.getHeadAndTailOfYesterday();

        int affected = 0;
        // 归并订单编号
        for (int i = 0; i < sharding; i++) {
            String tableName = table + "_" + i;
            Date date = yesterday.getSecond();

            try {
                affected += doTransfer(tableName, date);
            } catch (RuntimeException ex) {
                log.error(">> 移除数据出错 [ tableName = {}, date = {}, err = {} ]", tableName, date, ex.getMessage());
                ex.printStackTrace();
            }
        }

        // 如果没有待同步数据，则暂停半个小时
        delayed = affected == 0;
    }

    @Override
    public Date nextExecutionTime(Date date) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);

        // 如果没有待同步数据，则暂停半个小时
        if (delayed) {
            instance.add(Calendar.MINUTE, 30);
        } else {
            instance.add(Calendar.SECOND, 5);
        }

        Date next = instance.getTime();
        log.info(">> 下次执行时间 [ next = {} ] ", next);
        return next;
    }

    private int doTransfer(String tableName, Date date) {

        log.info(">> 开始移除数据 [ tableName = {}, date = {} ]", tableName, date);

        // 移除两天前的数据
        List<String> ids = cywWalArchiveMapper.queryRefIdForArchive(tableName, date).stream()
                .filter(s -> s.startsWith("S")).collect(Collectors.toList());

        log.info(">> 匹配数据 [ tableName = {}, refIds = {} ]", tableName, ids.size());

        if (ids.size() == 0) {
            return 0;
        }

        List<CywWalletWalRecord> records = new ArrayList<>();
        // 按一百分页查询
        new ArrayPage<>(ids, 100).forEach(list -> {
            List<CywWalletWalRecord> byRefId = cywWalArchiveMapper.findByRefIds(tableName, list);
            records.addAll(byRefId);
        });

        log.info(">> 查询结果 [ tableName = {}, records = {} ]", tableName, records.size());

        // 迁移处理
        Map<String, List<CywWalletWalRecord>> groupByRefId = records.stream()
                .collect(Collectors.groupingBy(CywWalletWalRecord::getRefId));

        List<String> refIds = new ArrayList<>();
        groupByRefId.forEach((key, value) -> {
            // 中断处理， 此单未结束或者异常
            if (value == null || value.size() != 2) {
                return;
            }

            // 两单
            CywWalletWalRecord wal1 = value.get(0);
            CywWalletWalRecord wal2 = value.get(1);

            boolean step1 = wal1.getTradeBalance().add(wal2.getTradeBalance()).compareTo(BigDecimal.ZERO) == 0;
            boolean step2 = wal1.getTradeFrozen().add(wal2.getTradeFrozen()).compareTo(BigDecimal.ZERO) == 0;

            if (step1 && step2) {
                refIds.add(key);
                log("cyw_wal", CywWalletWalRecord.class, wal1);
                log("cyw_wal", CywWalletWalRecord.class, wal2);
            }
        });

        log.info(">> 迁移结果 tableName = {}, refIds = {} ]", tableName, refIds.size());
        // 批量删除
        new ArrayPage<>(refIds, 100).forEach(list -> {
            cywWalArchiveMapper.deleteByRefIds(tableName, list);
        });

        log.info(">> 移除数据结束 [ tableName = {}, date = {} ]", tableName, date);
        return refIds.size();
    }

    @Autowired
    public void setCywWalArchiveMapper(CywWalArchiveMapper cywWalArchiveMapper) {
        this.cywWalArchiveMapper = cywWalArchiveMapper;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public void setSharding(int sharding) {
        this.sharding = sharding;
    }
}
