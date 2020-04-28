package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.dto.request.ExchangeReleaseLockRequestDTO;
import com.spark.bitrade.entity.ExchangeReleaseLockRecord;
import com.spark.bitrade.util.MessageRespResult;

/**
 * 币币交易释放-锁仓明细表(ExchangeReleaseLockRecord)表服务接口
 *
 * @author yangch
 * @since 2019-12-16 14:52:16
 */
public interface ExchangeReleaseLockRecordService extends IService<ExchangeReleaseLockRecord> {

    /**
     *  充值锁仓
     * @param requestDTO
     * @return
     */
    MessageRespResult rechargeLock(ExchangeReleaseLockRequestDTO requestDTO);
    


}