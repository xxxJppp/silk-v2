package com.spark.bitrade.job;

import com.spark.bitrade.entity.constants.CywConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;

/**
 * DelayWalSyncJob
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/11 10:26
 */
@Slf4j
@Component
public class DelayWalSyncJobImpl implements DelayWalSyncJob, InitializingBean, DisposableBean {

    private BlockingQueue<SyncItem> syncItems = new DelayQueue<>();
    private KafkaTemplate<String, String> kafkaTemplate;

    private Set<String> duplicate = new HashSet<>();

    private boolean runnable = true;

    @Autowired
    public void setKafkaTemplate(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sync(Long memberId, String coinUnit) {
        String value = memberId + ":" + coinUnit;

        // 加入去重集合
        if (duplicate.add(value)) {
            // 延时3秒
            syncItems.add(new SyncItem(value, 3));
        }
    }

    @Override
    public void destroy() throws Exception {
        // nothing ...
        runnable = false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Thread thread = new Thread(this::execute);
        thread.setName("MsgSender");
        thread.setDaemon(true);
        thread.start();
    }

    private void execute() {
        while (runnable) {
            try {
                SyncItem take = syncItems.take();
                // 发送
                kafkaTemplate.send(CywConstants.KAFKA_MSG_WALLET_WAL_SYNC_TASK, take.getValue());
                // 移除去重
                duplicate.remove(take.getValue());
            } catch (InterruptedException e) {
                log.error("发送同步任务失败", e);
            }
        }
    }
}
