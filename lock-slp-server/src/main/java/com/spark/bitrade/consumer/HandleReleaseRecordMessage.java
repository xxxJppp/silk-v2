package com.spark.bitrade.consumer;

import com.alibaba.fastjson.JSON;
import com.spark.bitrade.constant.SlpReleaseType;
import com.spark.bitrade.constant.SlpStatus;
import com.spark.bitrade.constants.LockSlpConstant;
import com.spark.bitrade.entity.LockSlpReleasePlanRecord;
import com.spark.bitrade.entity.LockSlpReleaseTaskRecord;
import com.spark.bitrade.job.ReleaseHandleJob;
import com.spark.bitrade.mq.ReleaseRecordMessage;
import com.spark.bitrade.service.LockSlpReleasePlanRecordService;
import com.spark.bitrade.service.LockSlpReleaseTaskRecordService;
import io.shardingsphere.api.HintManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * HandleReleaseRecordMessage
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/7/16 10:30
 */
@Slf4j
@Component
public class HandleReleaseRecordMessage implements InitializingBean, DisposableBean {

    private ThreadPoolTaskExecutor executor;

    private LockSlpReleaseTaskRecordService slpReleaseTaskRecordService;
    private LockSlpReleasePlanRecordService slpReleasePlanRecordService;

    private ReleaseHandleJob releaseHandleJob;

    @KafkaListener(topics = LockSlpConstant.KAFKA_MSG_RELEASE_RECORD_TASK, group = "group-handle")
    public void handle(ConsumerRecord<String, String> record) {
        log.info("接收释放记录任务消息：{}", record);
        ReleaseRecordMessage message = JSON.parseObject(record.value(), ReleaseRecordMessage.class);

        if (message != null) {
            if (message.getType() == SlpReleaseType.RELEASE_DAILY) {
                executor.execute(() -> {
                    try {
                        HintManager.getInstance().setMasterRouteOnly();
                        LockSlpReleasePlanRecord target = slpReleasePlanRecordService.getById(NumberUtils.toLong(message.getRefId(), 0));
                                // FuncWrapUtil.retryFunc(() -> slpReleasePlanRecordService.getById(NumberUtils.toLong(message.getRefId(), 0)), 3);
                        if (target != null && target.getStatus() == SlpStatus.NOT_PROCESSED) {
                            releaseHandleJob.execute(target);
                        }
                        log.warn("未找到每日释放记录 [ plan_record_id = {} ]", message.getRefId());
                    }catch (Exception ex) {
                        log.error("每日释放任务处理失败", ex);
                    }
                });
            } else {
                executor.execute(() -> {
                    try {
                        HintManager.getInstance().setMasterRouteOnly();
                        LockSlpReleaseTaskRecord target = slpReleaseTaskRecordService.getById(NumberUtils.toLong(message.getRefId(), 0));
                                // FuncWrapUtil.retryFunc(() -> slpReleaseTaskRecordService.getById(NumberUtils.toLong(message.getRefId(), 0)), 3);

                        if (target != null && target.getStatus() == SlpStatus.NOT_PROCESSED) {
                            releaseHandleJob.execute(target);
                        }
                        log.warn("未找到加速释放记录 [ task_record_id = {} ]", message.getRefId());
                    } catch (Exception ex) {
                        log.error("加速释放任务处理失败", ex);
                    }
                });
            }
        } else {
            log.error("无效的的消息格式 value = {}", record.value());
        }
    }

    @Override
    public void destroy() throws Exception {
        if (executor != null) {
            executor.shutdown();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(64);
        executor.setWaitForTasksToCompleteOnShutdown(true);

        // todo executor.setRejectedExecutionHandler();
        executor.afterPropertiesSet();
    }

    @Autowired
    public void setSlpReleaseTaskRecordService(LockSlpReleaseTaskRecordService slpReleaseTaskRecordService) {
        this.slpReleaseTaskRecordService = slpReleaseTaskRecordService;
    }

    @Autowired
    public void setSlpReleasePlanRecordService(LockSlpReleasePlanRecordService slpReleasePlanRecordService) {
        this.slpReleasePlanRecordService = slpReleasePlanRecordService;
    }

    @Autowired
    public void setReleaseHandleJob(ReleaseHandleJob releaseHandleJob) {
        this.releaseHandleJob = releaseHandleJob;
    }

    public void setExecutor(ThreadPoolTaskExecutor executor) {
        this.executor = executor;
    }
}
