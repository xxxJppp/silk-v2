package com.spark.bitrade.cyw.api;


import com.spark.bitrade.entity.CywWalletWalRecord;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * ICywService
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/2 11:43
 */
@FeignClient(Constants.CYW_SERVER)
public interface ICywService {

    /**
     * 获取余额
     *
     * @param memberId 会员ID
     * @param coinUnit 币种
     * @return resp
     */
    @GetMapping("/ex_cyw/internal/balance")
    MessageRespResult<BigDecimal> getBalance(@RequestParam("memberId") Long memberId, @RequestParam("coinUnit") String coinUnit);

    /**
     * 转入
     *
     * @param memberId 会员ID
     * @param coinUnit 币种
     * @param amount   数量
     * @param refId    关联的转账业务id
     * @return resp
     */
    @PostMapping("/ex_cyw/internal/transIn")
    MessageRespResult<CywWalletWalRecord> transferIn(@RequestParam("memberId") Long memberId,
                                                     @RequestParam("coinUnit") String coinUnit,
                                                     @RequestParam("amount") BigDecimal amount,
                                                     @RequestParam("refId") String refId);

    /**
     * 转出
     *
     * @param memberId 会员ID
     * @param coinUnit 币种
     * @param amount   数量
     * @param refId    关联的转账业务id
     * @return resp
     */
    @PostMapping("/ex_cyw/internal/transOut")
    MessageRespResult<CywWalletWalRecord> transferOut(@RequestParam("memberId") Long memberId,
                                                      @RequestParam("coinUnit") String coinUnit,
                                                      @RequestParam("amount") BigDecimal amount,
                                                      @RequestParam("refId") String refId);
}
