package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constant.ExchangeOrderStatus;
import com.spark.bitrade.dao.ExchangeOrderDetailRepository;
import com.spark.bitrade.dto.ExchangeOrderDto;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.ExchangeOrderDetail;
import com.spark.bitrade.entity.constants.ExchangeRedisKeys;
import com.spark.bitrade.service.ExchangeCancelOrderService;
import com.spark.bitrade.service.ExchangeCompletedOrderService;
import com.spark.bitrade.service.ExchangeOrderService;
import com.spark.bitrade.service.OrderFacadeService;
import com.spark.bitrade.service.optfor.RedisHashService;
import com.spark.bitrade.util.OrderUtil;
import com.spark.bitrade.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *  订单服务实现类
 *
 * @author young
 * @time 2019.09.02 11:47
 */

@Slf4j
public abstract class AbstractOrderFacadeServiceImpl implements OrderFacadeService {
    @Autowired
    protected RedisHashService redisHashService;
    @Autowired
    protected ExchangeOrderService exchangeOrderService;

    @Autowired
    protected ExchangeCancelOrderService exchangeCancelOrderService;
    @Autowired
    protected ExchangeCompletedOrderService completedOrderService;
    @Autowired
    protected ExchangeOrderDetailRepository orderDetailRepository;

    @Override
    public ExchangeOrder createOrder(ExchangeOrder exchangeOrder) {
        return exchangeOrderService.createOrder(exchangeOrder);
    }

    @Override
    public ExchangeOrder completedOrder(Long memberId, String orderId, BigDecimal tradedAmount, BigDecimal turnover) {
        this.checkOrderIdFormat(orderId);
        return completedOrderService.completedOrder(memberId, orderId, tradedAmount, turnover);
    }


    @Override
    public ExchangeOrder queryOrder(Long memberId, String orderId) {
        this.checkOrderIdFormat(orderId);
        return exchangeOrderService.queryOrder(memberId, orderId);
    }

    @Override
    public ExchangeOrderDto queryOrderDetail(Long memberId, String orderId) {
        this.checkOrderIdFormat(orderId);
        ExchangeOrder order = exchangeOrderService.queryOrder(memberId, orderId);
        if (order != null) {
            ExchangeOrderDto orderDto = new ExchangeOrderDto();
            BeanUtils.copyProperties(order, orderDto);
            if (order.getStatus().equals(ExchangeOrderStatus.TRADING)) {
                orderDto.setDetail(this.listTradeDetail(orderId));
            } else {
                orderDto.setDetail(getService().listHistoryByOrderId(orderId));
            }

            return orderDto;
        } else {
            return null;
        }
    }

    @Override
    public ExchangeOrder claimCancelOrder(Long memberId, String orderId) {
        return exchangeCancelOrderService.claimCancelOrder(memberId, orderId);
    }

    @Override
    public ExchangeOrder canceledOrder(Long memberId, String orderId, BigDecimal tradedAmount, BigDecimal turnover) {
        this.checkOrderIdFormat(orderId);
        return exchangeCancelOrderService.canceledOrder(memberId, orderId, tradedAmount, turnover);
    }

    @Override
    public ExchangeOrder canceledOrder(Long memberId, String orderId) {
        this.checkOrderIdFormat(orderId);
        return exchangeCancelOrderService.canceledOrder(memberId, orderId);
    }

    @Override
    public List<ExchangeOrder> openOrders(String symbol, Long memberId) {
        //todo 查询所有订单
        List<ExchangeOrder> lst = new ArrayList<>();
        redisHashService.hValues(ExchangeRedisKeys.getExchangeOrderTradingKey(memberId, symbol)).forEach(c -> {
            lst.add((ExchangeOrder) c);
        });
        return lst;
    }

    @Override
    public IPage<ExchangeOrderDto> openOrders(Long uid, String symbol, int pageNo, int pageSize,
                                              String coinSymbol, String baseSymbol,
                                              ExchangeOrderDirection direction) {
        return exchangeOrderService.openOrders(new Page<>(pageNo, pageSize),
                uid, symbol, coinSymbol, baseSymbol, direction);
    }

    @Override
    public IPage<ExchangeOrder> historyOrders(String symbol, Long memberId, Integer size, Integer current) {
        return exchangeOrderService.historyOrders(new Page<>(current, size), memberId, symbol);
    }

    @Override
    public IPage<ExchangeOrderDto> historyOrders(Long uid, String symbol, int pageNo, int pageSize,
                                                 String coinSymbol, String baseSymbol,
                                                 ExchangeOrderDirection direction, ExchangeOrderStatus status,
                                                 Long startTime, Long endTime) {
        return exchangeOrderService.historyOrders(new Page<>(pageNo, pageSize),
                uid, symbol, coinSymbol, baseSymbol, direction, status, startTime, endTime);
    }

    @Override
    public List<ExchangeOrderDetail> listTradeDetail(String orderId) {
        return orderDetailRepository.findAllByOrderId(orderId);
    }

    /**
     * 历史订单明细查询
     *
     * @param orderId  
     */
    @Override
    @Cacheable(cacheNames = "exchangeOrderDetail", key = "'entity:exchangeOrderDtlLst:'+#orderId")
    public List<ExchangeOrderDetail> listHistoryByOrderId(String orderId) {
        return orderDetailRepository.findAllByOrderId(orderId);
    }

    public AbstractOrderFacadeServiceImpl getService() {
        return SpringContextUtil.getBean(AbstractOrderFacadeServiceImpl.class);
    }


    /**
     * 校验订单格式
     *
     * @param orderId
     */
    protected void checkOrderIdFormat(String orderId) {
        //订单格式：E1168423154092716041
        OrderUtil.checkOrderIdFormat(orderId);
//        AssertUtil.notNull(orderId, CommonMsgCode.INVALID_PARAMETER);
//
//        if (!orderId.startsWith(ExchangeConstants.ORDER_PREFIX)) {
//            ExceptionUitl.throwsMessageCodeException(ExchangeOrderMsgCode.BAD_ORDER);
//        }
    }
}
