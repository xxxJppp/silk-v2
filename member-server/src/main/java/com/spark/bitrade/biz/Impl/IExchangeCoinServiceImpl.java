package com.spark.bitrade.biz.Impl;

import com.spark.bitrade.biz.IExchangeCoinService;
import com.spark.bitrade.entity.ExchangeCoin;
import com.spark.bitrade.entity.ExchangeCoinExtend;
import com.spark.bitrade.service.ExchangeCoinExtendService;
import com.spark.bitrade.service.ICoinExchange;
import com.spark.bitrade.util.MessageRespResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @author: Zhong Jiang
 * @date: 2019-11-22 10:48
 */
@Service
@Slf4j
public class IExchangeCoinServiceImpl implements IExchangeCoinService {

    @Autowired
    private ICoinExchange coinExchange;

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ExchangeCoinExtend getExchangeCoinExtendBySymbol(String symbol) {
        ExchangeCoin data = coinExchange.findSymbol(symbol);
        log.info("========== 查询v1 的结果为：{}", data);
        ExchangeCoinExtend coin = new ExchangeCoinExtend();
        if (data != null) {
            coin.setBuyFeeDiscount(data.getFee().multiply(BigDecimal.ONE.subtract(data.getFeeBuyDiscount())));
            coin.setSellFeeDiscount(data.getFee().multiply(BigDecimal.ONE.subtract(data.getFeeSellDiscount())));
            coin.setEntrustBuyDiscount(data.getFee().multiply(BigDecimal.ONE.subtract(data.getFeeEntrustBuyDiscount())));
            coin.setEntrustSellDiscount(data.getFee().multiply(BigDecimal.ONE.subtract(data.getFeeEntrustSellDiscount())));
        }
            return coin;
    }
}
