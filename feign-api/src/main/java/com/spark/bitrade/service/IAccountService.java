package com.spark.bitrade.service;

import com.spark.bitrade.constant.FeignServiceConstant;
import com.spark.bitrade.entity.Coin;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * IAccountService
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/11/21 11:32
 */
@FeignClient(FeignServiceConstant.ACCOUNT_SERVER)
public interface IAccountService {

    /**
     * 获取所有币种
     * <p>
     * 已填充汇率
     *
     * @return resp
     */
    @GetMapping("/acct/internal/coins")
    MessageRespResult<List<Coin>> getCoins();

    /**
     * 获取币种
     *
     * @param unit 币种单位
     * @return resp
     */
    @GetMapping("/acct/internal/coins/{unit}")
    MessageRespResult<Coin> getCoin(@PathVariable("unit") String unit);
}
