package com.spark.bitrade.consumer;

import com.alibaba.fastjson.JSON;
import com.spark.bitrade.service.IWalletTradeService;
import com.spark.bitrade.trans.TradeTccCancelEntity;
import com.spark.bitrade.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 *  kafka消费
 *
 * @author young
 * @time 2019.09.17 14:02
 */
@Slf4j
@Component
public class AccountConsumer {

    @Autowired
    private IWalletTradeService walletTradeService;

    /**
     * 处理tcc 撤销请求
     *
     * @param record
     */
    @KafkaListener(topics = "acct-trade-tcc-cancel", group = "group-handle")
    public void handleTrade(ConsumerRecord<String, String> record) {
        getService().tradeTccCancel(record);
    }

    @Async
    void tradeTccCancel(ConsumerRecord<String, String> record) {
        log.info(" tcc 撤销请求 >> {}", record.value());
        TradeTccCancelEntity entity = JSON.parseObject(record.value(), TradeTccCancelEntity.class);
        if (this.walletTradeService.tradeTccCancel(entity.getMemberId(), entity.getWalletChangeRecordId())) {
            log.info(" tcc 撤销成功 >> {}", record.value());
        } else {
            log.error(" tcc 撤销失败 >> {}", record.value());
        }
    }

    public AccountConsumer getService() {
        return SpringContextUtil.getBean(AccountConsumer.class);
    }
}
