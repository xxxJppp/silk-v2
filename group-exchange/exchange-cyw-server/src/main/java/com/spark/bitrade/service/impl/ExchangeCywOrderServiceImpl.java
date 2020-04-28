package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.entity.ExchangeCywOrder;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.mapper.ExchangeCywOrderMapper;
import com.spark.bitrade.service.ExchangeCywOrderService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * (ExchangeCywOrder)表服务实现类
 *
 * @author yangch
 * @since 2019-09-02 11:23:46
 */
@Service("exchangeCywOrderService")
public class ExchangeCywOrderServiceImpl extends ServiceImpl<ExchangeCywOrderMapper, ExchangeCywOrder> implements ExchangeCywOrderService {

    @Override
    @Cacheable(cacheNames = "exchangeCywOrder", key = "'entity:exchangeCywOrder:'+#orderId")
    public ExchangeCywOrder queryOrder(Long memberId, String orderId) {
        return this.baseMapper.queryOrder(memberId, orderId);
    }

    @Override
    public IPage<ExchangeOrder> historyOrders(Page page, Long memberId, String symbol) {
        return this.baseMapper.historyOrders(page, memberId, symbol);
    }

    @Override
    public List<String> findOrderIdByValidatedAndLessThanTime(Long time) {
        return this.baseMapper.findOrderIdByValidatedAndLessThanTime(time);
    }

    @Transactional
    @Override
    public void transfer(String orderId) {
        ExchangeCywOrder order = getById(orderId);

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

    /**
     * 获取表名, 该处理都是归档到当月
     *
     * @return table
     */
    private String getTransferTableName() {
        String prefix = "exchange_cyw_order_his_";

        Calendar instance = Calendar.getInstance();
        return prefix + new SimpleDateFormat("yyyyMM").format(instance.getTime());
    }
}