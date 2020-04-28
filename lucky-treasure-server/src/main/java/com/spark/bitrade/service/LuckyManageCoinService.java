package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.LuckyManageCoin;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 幸运宝-对应币种表 服务类
 * </p>
 *
 * @author qiliao
 * @since 2019-12-16
 */
public interface LuckyManageCoinService extends IService<LuckyManageCoin> {

    List<LuckyManageCoin> bullsRank(Long actId);

    Optional<LuckyManageCoin> findByActIdAndCoin(Long actId, String coinUnit);
}
