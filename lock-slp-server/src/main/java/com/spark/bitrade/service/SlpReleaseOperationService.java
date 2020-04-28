package com.spark.bitrade.service;

import com.spark.bitrade.entity.SlpMemberPromotion;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * SlpReleaseOperationService
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/7/9 19:05
 */
public interface SlpReleaseOperationService {

    /**
     * 获取释放的币种
     *
     * @return unit
     */
    String getCoinInUnit();

    /**
     * 获取昨日目标币种对美元的汇率
     *
     * @param coinUnit 币种
     * @return rate
     */
    BigDecimal getYesterdayExchangeRate2Usdt(String coinUnit);

    /**
     * 获取分配比例
     * <p>
     * 可用余额和奖池的分配比例
     *
     * @return rate
     */
    BigDecimal getAllocProportion();

    /**
     * 获取放大基数
     *
     * @return scale
     */
    BigDecimal getZoomScale();

    /**
     * 获取直推释放比例
     *
     * @return ratio
     */
    BigDecimal getInviteRatio();

    /**
     * 获取推荐关系
     *
     * @param memberId 会员ID
     * @return optional
     */
    Optional<SlpMemberPromotion> getSlpMemberPromotion(Long memberId);

    /**
     * 太阳等级判断
     *
     * @param currentLevelId 当前等级ID
     * @param coinUnit       币种
     * @return bool
     */
    boolean isSunLevel(Long currentLevelId, String coinUnit);
}
