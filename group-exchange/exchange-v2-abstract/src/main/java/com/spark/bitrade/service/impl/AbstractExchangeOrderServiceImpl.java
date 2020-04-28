package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constant.ExchangeOrderStatus;
import com.spark.bitrade.constant.ExchangeOrderType;
import com.spark.bitrade.constants.ExchangeOrderMsgCode;
import com.spark.bitrade.dto.ExchangeOrderDto;
import com.spark.bitrade.dto.ExchangeOrderStats;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.mapper.ExchangeOrderMapper;
import com.spark.bitrade.service.ExchangeOrderService;
import com.spark.bitrade.service.ExchangeWalletProvider;
import com.spark.bitrade.service.PushMessage;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.SpringContextUtil;
import io.shardingsphere.api.HintManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * 币币订单表服务实现类
 *
 * @author yangch
 * @since 2019-09-02 11:23:46
 */
public abstract class AbstractExchangeOrderServiceImpl 
        extends ServiceImpl<ExchangeOrderMapper, ExchangeOrder> implements ExchangeOrderService {
    @Autowired
    protected ExchangeWalletProvider exchangeWalletProvider;
    @Autowired
    protected PushMessage pushMessage;

    @Override
    public ExchangeOrder createOrder(ExchangeOrder exchangeOrder) {
        // 下单
        this.getService().placeOrder(exchangeOrder);

        // 推送下单消息（注意：存在下单成功，消息发送失败的可能性）
        pushMessage.pushOrderCreate(exchangeOrder);

        return exchangeOrder;
    }

    @Transactional(rollbackFor = Exception.class)
    public ExchangeOrder placeOrder(ExchangeOrder exchangeOrder) {
        // 余额冻结操作
        if (exchangeWalletProvider.freezeBalance(exchangeOrder)) {
            AssertUtil.isTrue(this.baseMapper.insert(exchangeOrder) > 0, ExchangeOrderMsgCode.SAVE_ORDER_FAILED);
        }

        return exchangeOrder;
    }

    @Override
    public ExchangeOrder queryOrder(Long memberId, String orderId) {
        return this.baseMapper.queryOrder(memberId, orderId);
    }

    @Override
    public ExchangeOrder queryOrderWithMaster(Long memberId, String orderId) {
        HintManager hintManager = HintManager.getInstance();
        hintManager.setMasterRouteOnly();
        ExchangeOrder order = this.baseMapper.queryOrder(memberId, orderId);
        hintManager.close();
        return order;
    }

    @Override
    public boolean updateByOrderIdAndStatus(ExchangeOrder order, ExchangeOrderStatus oldStatus) {
        QueryWrapper<ExchangeOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", order.getOrderId());
        queryWrapper.eq("member_id", order.getMemberId());
        queryWrapper.eq("status", oldStatus.getValue());

        return SqlHelper.retBool(this.baseMapper.update(order, queryWrapper));
    }

    @Override
    public IPage<ExchangeOrderDto> openOrders(Page page, Long uid, String symbol, String coinSymbol,
                                              String baseSymbol, ExchangeOrderDirection direction) {
        return this.baseMapper.queryOrders(page, uid, symbol, coinSymbol, baseSymbol, direction, ExchangeOrderStatus.TRADING, null, null);
    }

    @Override
    @Cacheable(cacheNames = "exchangeOrder", key = "'entity:exchangeOrder:'+#orderId")
    public ExchangeOrder queryOrderWithCache(Long memberId, String orderId) {
        return this.baseMapper.queryOrder(memberId, orderId);
    }

    @Override
    public IPage<ExchangeOrder> historyOrders(Page page, Long memberId, String symbol) {
        return this.baseMapper.historyOrders(page, memberId, symbol);
    }

    @Override
    public IPage<ExchangeOrderDto> historyOrders(Page page, Long uid, String symbol, String coinSymbol,
                                                 String baseSymbol, ExchangeOrderDirection direction,
                                                 ExchangeOrderStatus status, Long startTime, Long endTime) {
        return this.baseMapper.queryOrders(page, uid, symbol, coinSymbol, baseSymbol, direction, status, startTime, endTime);
    }

    @Override
    public int findCurrentTradingCount(Long memberId, String symbol, ExchangeOrderDirection direction) {
        return this.baseMapper.findCurrentTradingCount(memberId, symbol, direction);
    }

    @Override
    public List<String> findOrderIdByValidatedAndLessThanTime(Long time) {
        return this.baseMapper.findOrderIdByValidatedAndLessThanTime(time);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void transfer(String orderId) {
        ExchangeOrder order = getById(orderId);

        if (order == null) {
            return;
        }
        // 迁移到目标表
        int ret = this.baseMapper.transferTo(getTransferTableName(), order);
        if (ret > 0) {
            // 移除
            this.removeById(orderId);
        }
    }

    @Override
    // @Cacheable(cacheNames = "exchangeOrder", key = "'entity:exchangeOrder:stats:'+#coinSymbol+'-'+#type.name()")
    public List<ExchangeOrderStats> stats(ExchangeOrderType type, String coinSymbol, Integer status, Long startTime, Long endTime) {
        return this.baseMapper.stats(type, coinSymbol, status, startTime, endTime);
    }


    /**
     * 每天01:00:00之前
     *
     * @return
     */
    protected long getEndTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public AbstractExchangeOrderServiceImpl getService() {
        return SpringContextUtil.getBean(AbstractExchangeOrderServiceImpl.class);
    }

    /**
     * 获取表名, 该处理都是归档到当月
     *
     * @return table
     */
    protected String getTransferTableName() {
        String prefix = "exchange_order_his_";

        Calendar instance = Calendar.getInstance();
        return prefix + new SimpleDateFormat("yyyyMM").format(instance.getTime());
    }
}