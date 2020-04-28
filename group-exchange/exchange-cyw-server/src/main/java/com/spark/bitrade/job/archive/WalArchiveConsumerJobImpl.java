package com.spark.bitrade.job.archive;

import com.spark.bitrade.mapper.CywWalletWalExtMapper;
import com.spark.bitrade.mapper.dto.WalRecordDto;
import com.spark.bitrade.service.CywWalletWalRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.math.BigDecimal;
import java.util.List;

/**
 * WalArchiveConsumerJobImpl
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/26 16:06
 */
@Slf4j
//@Component
@Deprecated
public class WalArchiveConsumerJobImpl extends AbstractArchiveJob<String> implements ArchiveJobDef {

    public WalArchiveConsumerJobImpl(RedisConnectionFactory redisConnectionFactory) {
        super(new StringRedisTemplate(redisConnectionFactory));
    }

    private CywWalletWalRecordService walRecordService;
    private CywWalletWalExtMapper walExtMapper;

    @Override
    public String getName() {
        return "WalArchiveConsumer";
    }

    @Override
    protected String getTaskListKey() {
        return ARCHIVE_TASK_PREFIX_KEY + ":wal";
    }

    @Override
    protected void handle(String refId) {

        List<WalRecordDto> records = walExtMapper.queryByRefId(refId);
        if (records.size() < 2) {
            // 中断处理， 此单未结束或者异常
            return;
        }

        // 两单
        if (records.size() == 2) {
            // 下单撤单
            WalRecordDto wal1 = records.get(0);
            WalRecordDto wal2 = records.get(1);

            boolean step1 = wal1.getTradeBalance().add(wal2.getTradeBalance()).compareTo(BigDecimal.ZERO) == 0;
            boolean step2 = wal1.getTradeFrozen().add(wal2.getTradeFrozen()).compareTo(BigDecimal.ZERO) == 0;

            if (step1 && step2) {
                walRecordService.transfer(refId);

            }
        }

        // 两单以上??? 有成交的单，等待其他验证

    }

    @Override
    public String fetch() {
        // 直接从缓存中 pop 任务
        SetOperations<String, String> operations = redisTemplate.opsForSet();
        return operations.pop(getTaskListKey());
    }

    @Autowired
    public void setWalRecordService(CywWalletWalRecordService walRecordService) {
        this.walRecordService = walRecordService;
    }

    @Autowired
    public void setWalExtMapper(CywWalletWalExtMapper walExtMapper) {
        this.walExtMapper = walExtMapper;
    }
}
