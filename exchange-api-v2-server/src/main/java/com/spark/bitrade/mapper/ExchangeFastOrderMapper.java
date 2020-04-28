package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.constant.ExchangeOrderStatus;
import com.spark.bitrade.entity.ExchangeFastOrder;
import org.apache.ibatis.annotations.Param;

/**
 * 闪兑订单(ExchangeFastOrder)表数据库访问层
 *
 * @author yangch
 * @since 2019-06-24 17:06:54
 */
public interface ExchangeFastOrderMapper extends BaseMapper<ExchangeFastOrder> {
    /**
     * 修改闪兑订单接收方的状态
     *
     * @param orderId       订单ID
     * @param oldStatus     修改前的状态
     * @param newStatus     修改后的状态
     * @param completedTime 完成时间
     * @return
     */
    int updataReceiverStatus(@Param("orderId") Long orderId,
                             @Param("oldStatus") ExchangeOrderStatus oldStatus,
                             @Param("newStatus") ExchangeOrderStatus newStatus,
                             @Param("completedTime") Long completedTime);
}