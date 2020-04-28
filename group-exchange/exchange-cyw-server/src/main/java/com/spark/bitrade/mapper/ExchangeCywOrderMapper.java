package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.entity.ExchangeCywOrder;
import com.spark.bitrade.entity.ExchangeOrder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * (ExchangeCywOrder)表数据库访问层
 *
 * @author yangch
 * @since 2019-09-02 11:23:46
 */
public interface ExchangeCywOrderMapper extends BaseMapper<ExchangeCywOrder> {
    /**
     * 查询订单
     *
     * @param memberId 用户ID
     * @param orderId  订单ID
     * @return
     */
    ExchangeCywOrder queryOrder(@Param("memberId") Long memberId, @Param("orderId") String orderId);

    /**
     * 查询历史订单
     *
     * @param page
     * @param memberId
     * @param symbol
     * @return
     */
    IPage<ExchangeOrder> historyOrders(Page page, @Param("memberId") Long memberId, @Param("symbol") String symbol);

    /**
     * 查询指定时间以内的已校验订单id
     *
     * @param time 指定时间戳
     * @return list
     */
    @Select("select order_id from exchange_cyw_order where validated = 1 and ( completed_time < #{time} or canceled_time < #{time} )")
    List<String> findOrderIdByValidatedAndLessThanTime(@Param("time") Long time);

    /**
     * 传输到指定表
     *
     * @param table 表名
     * @param order 订单
     * @return affected
     */
    int transferTo(@Param("table") String table, @Param("order") ExchangeCywOrder order);
}