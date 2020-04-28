package com.spark.bitrade.service;

import com.spark.bitrade.entity.AmountOfDiscountRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

/**
 * 经纪人优惠兑币限额记录(AmountOfDiscountRecord)表服务接口
 *
 * @author ss
 * @date 2020-04-08 15:58:56
 */
public interface AmountOfDiscountRecordService extends IService<AmountOfDiscountRecord>{

    /**
     * 根据用户ID获取经纪人优惠兑币剩余额度
     * @param memberId
     * @return
     */
    AmountOfDiscountRecord getByMemberId(Long memberId);

    /**
     * 减经纪人用户优惠额度
     * @param memberId
     * @param discountPart
     * @return
     */
    int updateMemberDiscount(Long memberId, BigDecimal discountPart);
}