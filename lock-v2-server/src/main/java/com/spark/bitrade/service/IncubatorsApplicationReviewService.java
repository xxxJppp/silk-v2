package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.constant.IncubatorsOpType;
import com.spark.bitrade.entity.IncubatorsApplicationReview;

/**
 * <p>
 * 孵化区-申请审核表 服务类
 * </p>
 *
 * @author qiliao
 * @since 2019-08-30
 */
public interface IncubatorsApplicationReviewService extends IService<IncubatorsApplicationReview> {

    /**
     * 获取审核详情
     *
     * @param incubatorsOpType 0-上币申请；1-退出上币
     * @param incubatorsId     申请项目ID（外键，与incubators_basic_information表ID相关联）
     * @return
     */
    IncubatorsApplicationReview getIncubatorsApplicationReviewById(IncubatorsOpType incubatorsOpType, Long incubatorsId);
}
