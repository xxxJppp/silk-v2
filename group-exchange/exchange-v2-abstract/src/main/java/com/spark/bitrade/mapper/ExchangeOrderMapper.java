package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constant.ExchangeOrderStatus;
import com.spark.bitrade.constant.ExchangeOrderType;
import com.spark.bitrade.dto.ExchangeOrderDto;
import com.spark.bitrade.dto.ExchangeOrderStats;
import com.spark.bitrade.entity.ExchangeOrder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * (ExchangeOrder)表数据库访问层
 *
 * @author yangch
 * @since 2019-09-02 11:23:46
 */
public interface ExchangeOrderMapper extends BaseMapper<ExchangeOrder> {
    /**
     * 查询订单
     *
     * @param memberId 用户ID
     * @param orderId  订单ID
     * @return
     */
    ExchangeOrder queryOrder(@Param("memberId") Long memberId, @Param("orderId") String orderId);

    /**
     * 查询订单
     *
     * @param page
     * @param memberId   用户ID
     * @param symbol     交易对
     * @param coinSymbol 交易币
     * @param baseSymbol 基币
     * @param direction  方向
     * @param status     订单状态，不提供则查询历史订单
     * @param startTime  开始时间戳（包含）
     * @param endTime    截止时间戳
     * @return
     */
    IPage<ExchangeOrderDto> queryOrders(Page page,
                                        @Param("memberId") Long memberId,
                                        @Param("symbol") String symbol,
                                        @Param("coinSymbol") String coinSymbol,
                                        @Param("baseSymbol") String baseSymbol,
                                        @Param("direction") ExchangeOrderDirection direction,
                                        @Param("status") ExchangeOrderStatus status,
                                        @Param("startTime") Long startTime,
                                        @Param("endTime") Long endTime);

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
    @Select("select order_id from exchange_order where validated = 1 and ( completed_time < #{time} or canceled_time < #{time} )")
    List<String> findOrderIdByValidatedAndLessThanTime(@Param("time") Long time);

    /**
     * 查询交易中的数量
     *
     * @param memberId  会员ID
     * @param symbol    交易对
     * @param direction 交易方向
     * @return
     */
    int findCurrentTradingCount(@Param("memberId") Long memberId, @Param("symbol") String symbol,
                                @Param("direction") ExchangeOrderDirection direction);

    /**
     * 传输到指定表
     *
     * @param table 表名
     * @param order 订单
     * @return affected
     */
    int transferTo(@Param("table") String table, @Param("order") ExchangeOrder order);

    /**
     * 项目方中心 数据统计
     *
     * @param type       订单类型
     * @param coinSymbol 项目方币种
     * @param status     订单状态，不传递为所有订单，0=交易中，1=已完成订单
     * @param startTime  统计开始时间
     * @param endTime    统计截止时间
     * @return
     */
    List<ExchangeOrderStats> stats(@Param("type") ExchangeOrderType type,
                                   @Param("coinSymbol") String coinSymbol,
                                   @Param("status") Integer status,
                                   @Param("startTime") Long startTime,
                                   @Param("endTime") Long endTime);
}