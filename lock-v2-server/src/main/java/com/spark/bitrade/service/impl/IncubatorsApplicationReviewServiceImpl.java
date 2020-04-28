package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constant.IncubatorsOpType;
import com.spark.bitrade.entity.IncubatorsApplicationReview;
import com.spark.bitrade.mapper.IncubatorsApplicationReviewMapper;
import com.spark.bitrade.service.IncubatorsApplicationReviewService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 孵化区-申请审核表 服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2019-08-30
 */
@Service
public class IncubatorsApplicationReviewServiceImpl extends ServiceImpl<IncubatorsApplicationReviewMapper, IncubatorsApplicationReview> implements IncubatorsApplicationReviewService {

    /**
     * 获取审核详情
     *
     * @param incubatorsOpType 0-上币申请；1-退出上币
     * @param incubatorsId     申请项目ID（外键，与incubators_basic_information表ID相关联）
     * @return
     */
    @Override
    public IncubatorsApplicationReview getIncubatorsApplicationReviewById(IncubatorsOpType incubatorsOpType, Long incubatorsId) {
        QueryWrapper<IncubatorsApplicationReview> wrapper = new QueryWrapper<>();
        wrapper.eq(IncubatorsApplicationReview.INCUBATORS_ID, incubatorsId)
                .eq(IncubatorsApplicationReview.OPERATION_TYPE, incubatorsOpType.getOrdinal());
        List<IncubatorsApplicationReview> list = getBaseMapper().selectList(wrapper);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }
}
