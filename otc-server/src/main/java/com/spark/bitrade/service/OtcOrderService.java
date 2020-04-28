package com.spark.bitrade.service;

import com.spark.bitrade.entity.OtcOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.enums.AdvertiseType;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * (OtcOrder)表服务接口
 *
 * @author ss
 * @date 2020-03-19 10:23:50
 */
public interface OtcOrderService extends IService<OtcOrder>{

    /**
     *
     * @param toArray
     * @param type
     * @return
     */
    List<Map<String, Long>> selectCountByMembers(Long[] toArray, AdvertiseType type);

    /**
     *
     * @param toArray
     * @param type
     * @param time
     * @return
     */
    List<Map<String, Long>> selectCountByMembersAnd48(Long[] toArray, AdvertiseType type, Date time);

    /**
     * 检查未完成订单
     * @param memberId
     * @param orderNum
     * @return
     */
    boolean isAllowTrade(Long memberId, Integer orderNum);

    /**
     * 允许小数最后一位的精度有“正负1”的误差
     * @param v1
     * @param v2
     * @return
     */
    boolean isEqualIgnoreTailPrecision(BigDecimal v1, BigDecimal v2);
}
