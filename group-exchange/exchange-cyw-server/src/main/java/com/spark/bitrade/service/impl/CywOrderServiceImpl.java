package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.dao.ExchangeOrderDetailRepository;
import com.spark.bitrade.entity.ExchangeCywOrder;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.ExchangeOrderDetail;
import com.spark.bitrade.entity.constants.CywRedisKeys;
import com.spark.bitrade.service.*;
import com.spark.bitrade.service.optfor.RedisHashService;
import com.spark.bitrade.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *  订单服务实现类
 *
 * @author young
 * @time 2019.09.02 11:47
 */

@Slf4j
@Service
public class CywOrderServiceImpl implements CywOrderService {
    @Autowired
    private RedisHashService redisHashService;
    @Autowired
    private ExchangeCywOrderService exchangeCywOrderService;
    @Autowired
    private CywWalletProvider cywWalletProvider;

    private StringRedisTemplate redisTemplate;

    @Autowired
    private CywTradingOrderService tradingOrderService;

    @Autowired
    private CywCancelOrderService cywCancelOrderService;
    @Autowired
    private CywCompletedOrderService completedOrderService;
    @Autowired
    private ExchangeOrderDetailRepository orderDetailRepository;

    @Override
    public ExchangeOrder createOrder(ExchangeCywOrder exchangeCywOrder) {
        // 余额冻结操作
        if (cywWalletProvider.freezeBalance(exchangeCywOrder)) {
            //订单信息存入正在进行的缓存库中
            tradingOrderService.addTradingOrderToReids(exchangeCywOrder);
        }

        return exchangeCywOrder;
    }

    @Override
    public ExchangeOrder completedOrder(Long memberId, String orderId, BigDecimal tradedAmount, BigDecimal turnover) {
        return completedOrderService.completedOrder(memberId, orderId, tradedAmount, turnover);
    }


    @Override
    public ExchangeCywOrder queryOrder(Long memberId, String orderId) {
        // 首先，从redis正在交易的队列中查询
        ExchangeCywOrder order = this.queryTradingOrderFromRedis(memberId, orderId);

        // 其次，从redis已撤单的队列中查询
        if (order == null) {
            log.info("从redis已撤单的队列中查询，订单号={}", orderId);
            order = cywCancelOrderService.queryCanceledOrderFromRedis(orderId);
        }

        // 再其次，从redis已完成的队列中查询
        if (order == null) {
            log.info("从redis已完成的队列中查询，订单号={}", orderId);
            order = completedOrderService.queryCompletedOrderFromRedis(orderId);
        }

        // 最后，从数据库中查询
        if (order == null) {
            log.info("从数据库中查询，订单号={}", orderId);
            order = exchangeCywOrderService.queryOrder(memberId, orderId);
        }

        return order;
    }

    @Override
    public ExchangeOrder claimCancelOrder(Long memberId, String orderId) {
        return cywCancelOrderService.claimCancelOrder(memberId, orderId);
    }

    @Override
    public ExchangeOrder canceledOrder(Long memberId, String orderId, BigDecimal tradedAmount, BigDecimal turnover) {
        return cywCancelOrderService.canceledOrder(memberId, orderId, tradedAmount, turnover);
    }

    @Override
    public ExchangeOrder canceledOrder(Long memberId, String orderId) {
        return cywCancelOrderService.canceledOrder(memberId, orderId);
    }

    @Override
    public void canceledOrder(String orderId) {
        cywCancelOrderService.canceledOrder(orderId);
    }


    @Override
    public Set<Long> openMembers(String symbol) {
        Set<String> keys = redisTemplate.keys(CywRedisKeys.getCywOrderTradingKeys(symbol));
        Set<Long> members = new HashSet<>(keys.size());
        keys.forEach(k -> members.add(Long.parseLong(k.substring(k.lastIndexOf(":") + 1))));
        return members;
    }


    @Override
    public List<ExchangeOrder> openOrders(String symbol, Long memberId) {
        //查询所有订单
        List<ExchangeOrder> lst = new ArrayList<>();
        redisHashService.hValues(CywRedisKeys.getCywOrderTradingKey(memberId, symbol)).forEach(c -> {
            lst.add((ExchangeOrder) c);
        });
        return lst;
    }

    @Override
    public IPage<ExchangeOrder> historyOrders(String symbol, Long memberId, Integer size, Integer current) {
        return exchangeCywOrderService.historyOrders(new Page<>(current, size), memberId, symbol);
    }

    @Override
    public List<ExchangeOrderDetail> listTradeDetail(String orderId) {
        return orderDetailRepository.findAllByOrderId(orderId);
    }

    public CywOrderServiceImpl getService() {
        return SpringContextUtil.getBean(CywOrderServiceImpl.class);
    }

    @Autowired
    public void setRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        this.redisTemplate = new StringRedisTemplate(redisConnectionFactory);
    }

    /**
     * 从Redis中获取交易的订单
     *
     * @param memberId
     * @param orderId
     * @return
     */
    private ExchangeCywOrder queryTradingOrderFromRedis(Long memberId, String orderId) {
        return tradingOrderService.queryTradingOrderFromRedis(memberId, orderId);
    }

}
