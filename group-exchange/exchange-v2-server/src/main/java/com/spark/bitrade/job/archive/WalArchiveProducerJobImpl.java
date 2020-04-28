package com.spark.bitrade.job.archive;

import com.spark.bitrade.job.util.DateUtils;
import com.spark.bitrade.mapper.ExchangeWalletWalExtMapper;
import com.spark.bitrade.mapper.dto.WalRecordDto;
import com.spark.bitrade.trans.Tuple2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * WalArchiveProducerJobImpl
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/26 15:37
 */
@Slf4j
//@Component
@Deprecated
public class WalArchiveProducerJobImpl extends AbstractArchiveJob<Set<String>> implements ArchiveJobDef {

    public WalArchiveProducerJobImpl(RedisConnectionFactory redisConnectionFactory) {
        super(new StringRedisTemplate(redisConnectionFactory));
    }

    private ExchangeWalletWalExtMapper walExtMapper;

    /**
     * 表名
     */
    private String table = "exchange_wallet_wal_record";
    /**
     * 分片数量
     */
    private int sharding = 8;

    @Override
    public String getName() {
        return "WalArchiveProducer";
    }

    @Override
    protected String getTaskListKey() {
        return ARCHIVE_TASK_PREFIX_KEY + ":wal";
    }

    @Override
    protected void handle(Set<String> refIds) {
        // 加入缓存
        final String key = getTaskListKey();
        SetOperations<String, String> operations = redisTemplate.opsForSet();

        refIds.forEach(value -> operations.add(key, value));
    }

    @Override
    public Set<String> fetch() {

        // 存在进行中的任务 或 未取得全局锁则中断任务执行
        if (countTaskSize() > 0 || !getGlobalLock(ARCHIVE_LOCK_PREFIX_KEY + ":wal",60)) {
            return null;
        }
        // 昨日之前
        Tuple2<Date, Date> yesterday = DateUtils.getHeadAndTailOfYesterday();

        // 归并订单编号
        Set<String> refIds = new HashSet<>();
        for (int i = 0; i < sharding; i++) {
            List<WalRecordDto> records = walExtMapper.queryForArchive(table + "_" + i, yesterday.getSecond());
            records.forEach(r -> {
                String refId = r.getRefId();
                // 正常的订单id
                if (StringUtils.hasText(refId) && refId.contains("_")) {
                    refIds.add(refId);
                }
            });
        }

        if (refIds.size() > 0) {
            log.info("{} >> 归并待处理任务 size = {}", getName(), refIds.size());
            return refIds;
        }

        log.warn("{} >> 没有需要归并的处理的流水记录", getName());
        return null;
    }

    // ---------------------------------------
    // > S E T T E R S
    // ---------------------------------------

    @Autowired
    public void setWalExtMapper(ExchangeWalletWalExtMapper walExtMapper) {
        this.walExtMapper = walExtMapper;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public void setSharding(int sharding) {
        this.sharding = sharding;
    }
}
