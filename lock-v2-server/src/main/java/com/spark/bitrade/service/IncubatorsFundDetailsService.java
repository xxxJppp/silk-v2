package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.constant.IncubatorsDetailStatus;
import com.spark.bitrade.entity.IncubatorsFundDetails;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.form.WarehouseUpgradeForm;
import com.spark.bitrade.util.MessageRespResult;

/**
 * <p>
 * 孵化区-解锁仓明细表 服务类
 * </p>
 *
 * @author qiliao
 * @since 2019-08-30
 */
public interface IncubatorsFundDetailsService extends IService<IncubatorsFundDetails> {
    /**
     * 获取升仓数量
     *
     * @param incubatorsDetailStatus 状态
     * @param incubatorsId           关联id
     * @return
     */
    IncubatorsFundDetails getIncubatorsFundDetailsById(IncubatorsDetailStatus incubatorsDetailStatus, Long incubatorsId);

    /**
     * 04升仓
     *
     * @param member 会员信息
     * @param form   提交表单
     * @return
     */
    MessageRespResult warehouseUpgrade(Member member, WarehouseUpgradeForm form);
}
