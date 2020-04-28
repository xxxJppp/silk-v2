package com.spark.bitrade.service.impl;

import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constant.ExchangeOrderType;
import com.spark.bitrade.dao.ExchangeOrderDetailRepository;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.ExchangeOrderDetail;
import com.spark.bitrade.service.TradeDetailService;
import com.spark.bitrade.trans.OrderAggregation;
import com.spark.bitrade.trans.TradeSettleDelta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 *  交易明细服务实现
 *
 * @author young
 * @time 2019.09.05 10:39
 */
@Slf4j
@Service
public class TradeDetailServiceImpl implements TradeDetailService {
    @Resource
    private ExchangeOrderDetailRepository repository;

    @Override
    public boolean existsByOrderIdAndRefOrderId(String orderId, String refOrderId) {
        return repository.existsByOrderIdAndRefOrderId(orderId, refOrderId);
    }

    @Override
    public ExchangeOrderDetail save(ExchangeOrderDetail entity) {
        // 保存交易明细
        return repository.save(entity);
    }

    @Override
    public int deleteByOrderIdAndRefOrderId(String orderId, String refOrderId) {
        return repository.deleteExchangeOrderDetailByOrderIdAndRefOrderId(orderId, refOrderId);
    }


    /**
     * 获取指定订单号的交易数量和成交额
     *
     * @param exchangeOrder 币币交易订单
     * @return
     */
    @Override
    public OrderAggregation aggregation(ExchangeOrder exchangeOrder) {
        OrderAggregation aggregation = new OrderAggregation();
        aggregation.setOrderId(exchangeOrder.getOrderId());
        aggregation.setMemberId(exchangeOrder.getMemberId());

        //交易数量
        BigDecimal tradedAmount = BigDecimal.ZERO;
        //交易额
        BigDecimal tradedTurnover = BigDecimal.ZERO;

        //获取交易成交详情
        List<ExchangeOrderDetail> details = repository.findAllByOrderId(exchangeOrder.getOrderId());
        if (null == details || details.isEmpty()) {
            log.info("无成交明细记录：orderId={}", exchangeOrder.getOrderId());
            aggregation.setTradedAmount(tradedAmount);
            aggregation.setTradedTurnover(tradedTurnover);
            return aggregation;
        }
        log.info("查询交明细：orderId={},size={}", exchangeOrder.getOrderId(), details.size());
        for (ExchangeOrderDetail trade : details) {
            //设置交易数量
            if (exchangeOrder.getType() == ExchangeOrderType.MARKET_PRICE
                    && exchangeOrder.getDirection() == ExchangeOrderDirection.BUY) {
                tradedAmount = tradedAmount.add(trade.getTurnover());
            } else {
                tradedAmount = tradedAmount.add(trade.getAmount());
            }

            //设置交易额
            tradedTurnover = tradedTurnover.add(trade.getTurnover());
        }

        aggregation.setTradedAmount(tradedAmount);
        aggregation.setTradedTurnover(tradedTurnover);

        return aggregation;
    }

    @Override
    public ExchangeOrderDetail saveTradeDetail(TradeSettleDelta delta) {
        ExchangeOrderDetail detail = new ExchangeOrderDetail();
        detail.setOrderId(delta.getOrderId());
        detail.setRefOrderId(delta.getRefOrderId());
        detail.setSymbol(delta.getSymbol());
        detail.setPrice(delta.getPrice());
        detail.setBaseUsdRate(delta.getBaseUsdRate());
        detail.setAmount(delta.getAmount());
        detail.setTurnover(delta.getTurnover());
        detail.setFee(delta.getRealFee());
        detail.setFeeDiscount(delta.getFeeDiscount());
        detail.setTime(System.currentTimeMillis());

        return this.save(detail);
    }

    @Override
    public void deleteTradeDetail(TradeSettleDelta delta) {
        this.deleteByOrderIdAndRefOrderId(delta.getOrderId(), delta.getRefOrderId());
    }
}
