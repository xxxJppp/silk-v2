package com.spark.bitrade.job.archive;

import com.mongodb.BulkWriteResult;
import com.spark.bitrade.dao.ExchangeOrderDetailRepository;
import com.spark.bitrade.entity.ExchangeOrderDetail;
import com.spark.bitrade.service.CywWalletWalRecordService;
import com.spark.bitrade.service.ExchangeCywOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * OrderArchiveConsumerJobImpl
 *
 * @author Archx[archx@foxmail.com]
 * at 2019-09-30 11:32
 */
@Slf4j
@Component
public class OrderArchiveConsumerJobImpl extends AbstractArchiveJob<String> implements ArchiveJobDef {

    private ExchangeCywOrderService       exchangeCywOrderService;
    private CywWalletWalRecordService     walletWalRecordService;
    private ExchangeOrderDetailRepository orderDetailRepository;
    @Autowired
    private MongoTemplate                 mongoTemplate;


    public OrderArchiveConsumerJobImpl(RedisConnectionFactory redisConnectionFactory) {
        super(new StringRedisTemplate(redisConnectionFactory));
    }

    @Override
    public String getName() {
        return "OrderArchiveConsumer";
    }

    @Override
    protected String getTaskListKey() {
        return ARCHIVE_TASK_PREFIX_KEY + ":order";
    }

    // 此处不需要事务，操作是幂等的
    @Override
    protected void handle(String orderId) {

        // 迁移订单记录
        exchangeCywOrderService.transfer(orderId);

        // 迁移流水记录
        walletWalRecordService.transfer(orderId);

        // 迁移mongodb记录
        List<ExchangeOrderDetail> details = orderDetailRepository.findAllByOrderId(orderId);
        if (details != null && details.size() > 0) {
            // BulkMode.UNORDERED:表示并行处理，遇到错误时能继续执行不影响其他操作；BulkMode.ORDERED：表示顺序执行，遇到错误时会停止所有执行
            BulkWriteResult execute = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, getTransferTableName()).insert(details).execute();

            // 移除
            if (execute.getInsertedCount() == details.size()) {
                Query query = Query.query(Criteria.where("orderId").is(orderId));
                mongoTemplate.remove(query, "exchange_order_detail");
            }

        }
    }

    @Override
    public String fetch() {
        // 直接从缓存中 pop 任务
        SetOperations<String, String> operations = redisTemplate.opsForSet();
        return operations.pop(getTaskListKey());
    }

    /**
     * 获取表名, 该处理都是归档到当月
     *
     * @return table
     */
    private String getTransferTableName() {
        String prefix = "exchange_order_detail_his_";

        Calendar instance = Calendar.getInstance();
        return prefix + new SimpleDateFormat("yyyyMM").format(instance.getTime());
    }

    @Autowired
    public void setExchangeCywOrderService(ExchangeCywOrderService exchangeCywOrderService) {
        this.exchangeCywOrderService = exchangeCywOrderService;
    }

    @Autowired
    public void setWalletWalRecordService(CywWalletWalRecordService walletWalRecordService) {
        this.walletWalRecordService = walletWalRecordService;
    }

    @Autowired
    public void setOrderDetailRepository(ExchangeOrderDetailRepository orderDetailRepository) {
        this.orderDetailRepository = orderDetailRepository;
    }
}
