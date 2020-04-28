package com.spark.bitrade.biz;

import com.spark.bitrade.form.SupportCoinRecordsForm;
import com.spark.bitrade.vo.CoinApplyVo;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.05 11:39
 */
public interface ICoinRecordService {

    /**
     * 获取币种基本信息
     * @param memberId
     * @return
     */
    CoinApplyVo getCoinApplyVo(Long memberId);

    /**
     * 提交币种基本信息修改申请
     * @param recordsForm 申请表单
     */
    void saveCoinRecordApply(SupportCoinRecordsForm recordsForm);
}
