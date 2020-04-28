package com.spark.bitrade.job.archive;

import com.spark.bitrade.job.util.DateUtils;
import com.spark.bitrade.service.ExchangeOrderService;
import com.spark.bitrade.trans.Tuple2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Date;
import java.util.List;

/**
 * OrderArchiveProducerJobImpl
 *
 * @author Archx[archx@foxmail.com]
 * at 2019-09-30 11:09
 */
@Slf4j
//@Component
@Deprecated
public class OrderArchiveProducerJobImpl extends AbstractArchiveJob<List<String>> implements ArchiveJobDef {

    private ExchangeOrderService orderService;

    public OrderArchiveProducerJobImpl(RedisConnectionFactory redisConnectionFactory) {
        super(new StringRedisTemplate(redisConnectionFactory));
    }

    @Override
    public String getName() {
        return "OrderArchiveProducer";
    }

    @Override
    protected String getTaskListKey() {
        return ARCHIVE_TASK_PREFIX_KEY + ":order";
    }

    @Override
    protected void handle(List<String> refIds) {
        // 加入缓存
        final String key = getTaskListKey();
        SetOperations<String, String> operations = redisTemplate.opsForSet();

        refIds.forEach(value -> operations.add(key, value));
    }

    @Override
    public List<String> fetch() {
        // 存在进行中的任务 或 未取得全局锁则中断任务执行
        if (countTaskSize() > 0 || !getGlobalLock(ARCHIVE_LOCK_PREFIX_KEY + ":order", 3600)) {
            return null;
        }
        // 昨日之前
        Tuple2<Date, Date> yesterday = DateUtils.getHeadAndTailOfYesterday();
        // 前天
        List<String> records = orderService.findOrderIdByValidatedAndLessThanTime(yesterday.getFirst().getTime());

        if (records.size() > 0) {
            log.info("{} >> 归并待处理订单任务 size = {}", getName(), records.size());
            return records;
        }

        log.warn("{} >> 没有需要归并的处理的订单记录", getName());
        return null;
    }


    @Autowired
    public void setOrderService(ExchangeOrderService orderService) {
        this.orderService = orderService;
    }
}
