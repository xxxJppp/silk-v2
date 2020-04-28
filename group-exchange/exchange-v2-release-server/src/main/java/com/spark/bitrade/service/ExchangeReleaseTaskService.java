package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.ExchangeReleaseTask;
import com.spark.bitrade.entity.ExchangeWalletWalRecord;
import com.spark.bitrade.trans.TradeSettleDelta;
import com.spark.bitrade.util.MessageRespResult;

import java.util.List;

/**
 * 币币交易释放-释放任务表(ExchangeReleaseTask)表服务接口
 *
 * @author yangch
 * @since 2019-12-16 14:52:16
 */
public interface ExchangeReleaseTaskService extends IService<ExchangeReleaseTask> {

    /**
     * 保存释放任务
     *
     * @param delta
     * @param freezeRecord
     */
    void saveReleaseTask(TradeSettleDelta delta, ExchangeWalletWalRecord freezeRecord);

    /**
     * 获取待释放记录
     *
     * @return
     * @parm
     */
    MessageRespResult<List<ExchangeReleaseTask>> taskReleaseRecord();


    /**
     * 释放任务
     *
     * @param exchangeReleaseRecord
     * @return
     * @parm
     */
    void releaseTasks(List<Object> exchangeReleaseRecord);


    /**
     * 释放任务
     *
     * @param releaseTask
     * @return
     */
    boolean releaseTask(ExchangeReleaseTask releaseTask);
}