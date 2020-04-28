package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.SupportCoinRecords;

import java.util.List;

/**
 * <p>
 * 上币币种基本信息申请 服务类
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
public interface SupportCoinRecordsService extends IService<SupportCoinRecords> {

    /**
     * 根据币种查询申请
     * @param upCoinId
     * @return
     */
    SupportCoinRecords getOneCoinRecords(Long memberId, Long upCoinId);


    List<SupportCoinRecords> getCoinRecordsList(Long memberId, Long upCoinId);

}
