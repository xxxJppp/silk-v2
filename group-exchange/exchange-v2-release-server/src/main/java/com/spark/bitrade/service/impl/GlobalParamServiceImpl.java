package com.spark.bitrade.service.impl;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.SilkDataDist;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.service.GlobalParamService;
import com.spark.bitrade.service.ISilkDataDistApiService;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.util.SilkDataDistUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

/**
 * GlobalParamServiceImpl
 *
 * @author Archx[archx@foxmail.com]
 * @since 2020/1/19 14:12
 */
@Slf4j
@Service
public class GlobalParamServiceImpl implements GlobalParamService {

    private ISilkDataDistApiService silkDataDistApiService;

    /**
     * 创建缓存，默认10分钟过期
     */
    private TimedCache<String, SilkDataDist> timedCache = CacheUtil.newTimedCache(10 * 60 * 1000);


    @Override
    public BigDecimal getFeeAwardRatio() {
        // 手续费奖励比例
        return SilkDataDistUtils.getVal(this.get("fee-award-ratio").get(), BigDecimal.class, new BigDecimal(0.8));
    }

    @Override
    public BigDecimal getAccumulationBuyTotalAmount() {
        // 累计奖购买数量：直推用户累计购买ESP消耗的USDT数量
        return SilkDataDistUtils.getVal(this.get("accumulation-buy-total-amount").get(), BigDecimal.class, new BigDecimal(5000));
    }

    @Override
    public BigDecimal getAccumulationBuyAwardAmount() {
        // 累计奖励数量：直推用户达到累计奖购买数量后，奖励推荐人USDT的数量
        return SilkDataDistUtils.getVal(this.get("accumulation-buy-award-amount").get(), BigDecimal.class, new BigDecimal(40));
    }

    @Override
    public BigDecimal getBuyMaxExchangeRatio() {
        // 闪兑比例：买单最大闪兑比例，默认为0.5，用小数表示
        return SilkDataDistUtils.getVal(this.get("buy-max-exchange-ratio").get(), BigDecimal.class, new BigDecimal(0.5));
    }

    @Override
    public BigDecimal getExchangeRate() {
        // 闪兑手续费：闪兑手续费比例，默认为0.01，用小数表示
        return SilkDataDistUtils.getVal(this.get("exchange-rate").get(), BigDecimal.class, new BigDecimal(0.01));
    }

    @Override
    public Long getAwardAccount() {
        Optional<SilkDataDist> dict = get("award-account");
        if (dict.isPresent()) {
            return NumberUtils.toLong(dict.get().getDictVal());
        }
        throw new MessageCodeException(CommonMsgCode.of(400, "未配置闪兑归集账户 award-account"));
    }

    private Optional<SilkDataDist> get(String key) {
        if (Objects.nonNull(timedCache.get(key))) {
            return Optional.ofNullable(timedCache.get(key));
        }

        MessageRespResult<SilkDataDist> resp = silkDataDistApiService.findOne(EXCHANGE_RELEASE_ID, key);
        if (resp.isSuccess()) {
            // 缓存数据
            log.info("查询并缓存配置，key={}", key);
            timedCache.put(key, resp.getData());

            return Optional.ofNullable(resp.getData());
        }
        throw new MessageCodeException(CommonMsgCode.of(resp.getCode(), resp.getMessage()));
    }


    // --------------------------------------------
    // > S E T T E R S
    // --------------------------------------------

    @Autowired
    public void setSilkDataDistApiService(ISilkDataDistApiService silkDataDistApiService) {
        this.silkDataDistApiService = silkDataDistApiService;
    }
}
