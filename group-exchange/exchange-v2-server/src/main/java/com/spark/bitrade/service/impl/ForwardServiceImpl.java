package com.spark.bitrade.service.impl;

import com.alibaba.fastjson.JSON;
import com.spark.bitrade.config.ExchangeForwardStrategyConfiguration;
import com.spark.bitrade.config.YamlForwardConfiguration;
import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constant.ExchangeOrderType;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.ExchangeTrade;
import com.spark.bitrade.service.ForwardService;
import com.spark.bitrade.util.MessageRespResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Optional;

/**
 *  
 *
 * @author young
 * @time 2020.01.02 11:25
 */
@Slf4j
@Service
public class ForwardServiceImpl implements ForwardService {
    @Autowired
    private YamlForwardConfiguration yamlForwardConfiguration;
    @Autowired
    private RestTemplate restTemplate;

    private String uri_prefix = "http://%s/%s/service/v1/order/";


    @Override
    public Optional<ExchangeForwardStrategyConfiguration> getStrategy(String symbol) {
        return yamlForwardConfiguration.getStrategy(symbol);
    }

    @Override
    public MessageRespResult<ExchangeOrder> placeOrder(Long memberId, ExchangeOrderDirection direction,
                                                       String symbol, BigDecimal price, BigDecimal amount,
                                                       ExchangeOrderType type, String tradeCaptcha) {
        StringBuilder url = new StringBuilder(this.getUriPrefix(this.getStrategy(symbol).get()) + "place?");
        url.append("memberId=").append(memberId);
        url.append("&direction=").append(direction);
        url.append("&symbol=").append(symbol);
        url.append("&price=").append(price);
        url.append("&amount=").append(amount);
        url.append("&type=").append(type);
        url.append("&tradeCaptcha=").append(tradeCaptcha);
        return rpc(url);
    }

    @Override
    public MessageRespResult<ExchangeOrder> tradeBuy(ExchangeTrade trade) {
        ResponseEntity<MessageRespResult> res = restTemplate.postForEntity(this.getUriPrefix(this.getStrategy(trade.getSymbol()).get()) + "tradeBuy",
                trade, MessageRespResult.class);
        if (res.getStatusCode() == HttpStatus.OK && res.getBody().isSuccess()) {
            return MessageRespResult.success4Data(JSON.parseObject(JSON.toJSONString(res.getBody().getData()), ExchangeOrder.class));
        }

        return MessageRespResult.error("rpc-error");
    }

    @Override
    public MessageRespResult<ExchangeOrder> tradeSell(ExchangeTrade trade) {
        ResponseEntity<MessageRespResult> res = restTemplate.postForEntity(this.getUriPrefix(this.getStrategy(trade.getSymbol()).get()) + "tradeSell",
                trade, MessageRespResult.class);
        if (res.getStatusCode() == HttpStatus.OK && res.getBody().isSuccess()) {
            return MessageRespResult.success4Data(JSON.parseObject(JSON.toJSONString(res.getBody().getData()), ExchangeOrder.class));
        }

        return MessageRespResult.error("rpc-error");
    }

    @Override
    public MessageRespResult<ExchangeOrder> completedOrder(String symbol, Long memberId, String orderId,
                                                           BigDecimal tradedAmount, BigDecimal turnover) {
        StringBuilder url = new StringBuilder(this.getUriPrefix(this.getStrategy(symbol).get()) + "completedOrder?");
        url.append("memberId=").append(memberId);
        url.append("&orderId=").append(orderId);
        url.append("&tradedAmount=").append(tradedAmount);
        url.append("&turnover=").append(turnover);


        return rpc(url);
    }

    @Override
    public MessageRespResult<ExchangeOrder> canceledOrder(String symbol, Long memberId, String orderId,
                                                          BigDecimal tradedAmount, BigDecimal turnover) {
        StringBuilder url = new StringBuilder(this.getUriPrefix(this.getStrategy(symbol).get()) + "canceledOrder?");
        url.append("memberId=").append(memberId);
        url.append("&orderId=").append(orderId);
        url.append("&tradedAmount=").append(tradedAmount);
        url.append("&turnover=").append(turnover);

        return rpc(url);
    }

    @Override
    public MessageRespResult<ExchangeOrder> canceledOrder(String symbol, Long memberId, String orderId) {
        StringBuilder url = new StringBuilder(this.getUriPrefix(this.getStrategy(symbol).get()) + "canceledOrder2?");
        url.append("memberId=").append(memberId);
        url.append("&orderId=").append(orderId);

        return rpc(url);
    }

    @Override
    public MessageRespResult redo(@RequestParam("id") Long id, String symbol) {
        StringBuilder url = new StringBuilder(this.getUriPrefix(this.getStrategy(symbol).get()) + "redo?");
        url.append("id=").append(id);

        ResponseEntity<MessageRespResult> res = restTemplate.getForEntity(url.toString(), MessageRespResult.class);
        if (res.getStatusCode() == HttpStatus.OK && res.getBody().isSuccess()) {
            return MessageRespResult.success();
        }

        return MessageRespResult.error("rpc-error");
    }


    private String getUriPrefix(ExchangeForwardStrategyConfiguration configuration) {
        return String.format(uri_prefix, configuration.getApplicationName(), configuration.getServerContextPath());
    }

    /**
     * 远程调用服务接口
     *
     * @param url 调用url
     * @return
     */
    private MessageRespResult<ExchangeOrder> rpc(StringBuilder url) {
        ResponseEntity<MessageRespResult> res = restTemplate.getForEntity(url.toString(), MessageRespResult.class);
        if (res.getStatusCode() == HttpStatus.OK) {
            return JSON.parseObject(JSON.toJSONString(res.getBody()), MessageRespResult.class);
        }

        return MessageRespResult.error("rpc-error");
    }
}
