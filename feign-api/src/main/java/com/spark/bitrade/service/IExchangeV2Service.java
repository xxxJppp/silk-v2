package com.spark.bitrade.service;

import com.spark.bitrade.constant.FeignServiceConstant;
import com.spark.bitrade.constant.WalletType;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.vo.WalletAssetsVo;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

/**
 * IExchangeV2Service
 * <p>
 * prefix=/exchange2
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/11/21 15:51
 */
@FeignClient(FeignServiceConstant.EXCHANGE_SERVER_V2)
public interface IExchangeV2Service {

    /**
     * 转账
     *
     * @param memberId id
     * @param coinUnit coin
     * @param from     from
     * @param to       to
     * @param amount   amount
     * @return refId
     */
    @RequestMapping("/exchange2/internal/transfer")
    MessageRespResult<String> transfer(@RequestParam("memberId") Long memberId,
                                       @RequestParam("coinUnit") String coinUnit,
                                       @RequestParam("from") WalletType from,
                                       @RequestParam("to") WalletType to,
                                       @RequestParam("amount") BigDecimal amount);

    /**
     * 资产列表
     *
     * @param memberId 会员id
     * @return list
     */
    @GetMapping("/exchange2/internal/assets")
    MessageRespResult<List<WalletAssetsVo>> assets(@RequestParam("memberId") Long memberId);
    
    /**
     * 查询指定订单信息
     * @param memberId
     * @param orderId
     * @return
     * @author zhaopeng
     * @since 2020年1月8日
     */
    @GetMapping(value = "/exchange2/service/v1/order/queryOrder")
    public MessageRespResult<ExchangeOrder> queryOrder(@RequestParam("memberId") Long memberId, @RequestParam("orderId") String orderId);
}
