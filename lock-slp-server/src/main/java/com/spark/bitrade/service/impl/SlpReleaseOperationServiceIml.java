package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.LockSlpReleaseLevelConfig;
import com.spark.bitrade.entity.SilkDataDist;
import com.spark.bitrade.entity.SlpMemberPromotion;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.service.*;
import com.spark.bitrade.util.FeignFunctionUtil;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.util.SilkDataDistUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * SlpReleaseOperationServiceIml
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/7/9 19:09
 */
@Slf4j
@Service
public class SlpReleaseOperationServiceIml implements SlpReleaseOperationService {

    private ISilkDataDistApiService silkDataDistApiService;
    private IMemberApiService memberApiService;
    private SlpExchangeRateService slpExchangeRateService;
    private LockSlpReleaseLevelConfigService slpReleaseLevelConfigService;

    @Override
    public String getCoinInUnit() {
        // LOCK_SLP_ACTIVITY, HOLD_COIN_SYMBOL
        MessageRespResult<SilkDataDist> result = silkDataDistApiService.findOne("LOCK_SLP_ACTIVITY", "HOLD_COIN_SYMBOL");

        if (result.isSuccess()) {
            SilkDataDist data = result.getData();
            return SilkDataDistUtils.getVal(data, String.class, "SLP");
        }
        log.error("获取释放币种失败, code = {}, err = {}", result.getCode(), result.getMessage());
        throw new MessageCodeException(CommonMsgCode.of(result.getCode(), result.getMessage()));
    }

    @Override
    public BigDecimal getYesterdayExchangeRate2Usdt(String coinUnit) {
        final String symbol = coinUnit + "/USDT";
//        BigDecimal rate = new BigDecimal(10);
        // TODO 测试数据
        BigDecimal rate = slpExchangeRateService.exchangeRate4Yesterday(symbol);

        if (rate == null) {
            throw new IllegalArgumentException("无法获取交易对汇率 - " + symbol);
        }

        return rate;
    }

    @Override
    public BigDecimal getAllocProportion() {
        // LOCK_SLP_ACTIVITY, ALLOC_PROPORTION
        MessageRespResult<SilkDataDist> result = silkDataDistApiService.findOne("LOCK_SLP_ACTIVITY", "ALLOC_PROPORTION");

        if (result.isSuccess()) {
            SilkDataDist data = result.getData();
            return SilkDataDistUtils.getVal(data, BigDecimal.class, new BigDecimal("0.9"));
        }
        log.error("获取奖池分配比例失败, code = {}, err = {}", result.getCode(), result.getMessage());
        throw new MessageCodeException(CommonMsgCode.of(result.getCode(), result.getMessage()));
    }

    @Override
    public BigDecimal getZoomScale() {
        return FeignFunctionUtil.get(() -> silkDataDistApiService.findOne("LOCK_SLP_ACTIVITY", "EARNING_SCALE"), err -> {
            // 抛出错误
            log.error("获取锁仓活动奖励倍数失败, code = {}, err = {}", err.getCode(), err.getMessage());
            throw new MessageCodeException(err);
        }).map(dist -> SilkDataDistUtils.getVal(dist, BigDecimal.class, new BigDecimal("3"))).orElse(new BigDecimal("3"));
    }

    @Override
    public BigDecimal getInviteRatio() {
        return FeignFunctionUtil.get(() -> silkDataDistApiService.findOne("LOCK_SLP_ACTIVITY", "INVITE_RATIO"), err -> {
            // 抛出错误
            log.error("获取直推加速释放比例失败, code = {}, err = {}", err.getCode(), err.getMessage());
            throw new MessageCodeException(err);
        }).map(dist -> SilkDataDistUtils.getVal(dist, BigDecimal.class, new BigDecimal("0.1"))).orElse(new BigDecimal("0.1"));
    }

    @Override
    public Optional<SlpMemberPromotion> getSlpMemberPromotion(final Long memberId) {
        return FeignFunctionUtil.get(() -> memberApiService.getSlpMemberPromotion(memberId), err -> {
            log.error("获取推荐用户推荐关系出错 id = {}, code = {}, err = {}", memberId, err.getCode(), err.getMessage());
            throw new MessageCodeException(err);
        });
    }

    @Override
    public boolean isSunLevel(Long currentLevelId, String coinUnit) {
        log.info("判断是否为太阳等级====currentLevelId-{},coinUnit-{}=====",currentLevelId,coinUnit);
        QueryWrapper<LockSlpReleaseLevelConfig> query = new QueryWrapper<>();
        query.eq("coin_unit", coinUnit).orderByDesc("sort");

        List<LockSlpReleaseLevelConfig> list = slpReleaseLevelConfigService.list(query);
        // 默认和第一个比较
        boolean result = list.get(0).getLevelId() == currentLevelId.longValue();
        log.info("是否为太阳等级：结果-{}",result);
        return result;
    }

    @Autowired
    public void setSilkDataDistApiService(ISilkDataDistApiService silkDataDistApiService) {
        this.silkDataDistApiService = silkDataDistApiService;
    }

    @Autowired
    public void setMemberApiService(IMemberApiService memberApiService) {
        this.memberApiService = memberApiService;
    }

    @Autowired
    public void setSlpExchangeRateService(SlpExchangeRateService slpExchangeRateService) {
        this.slpExchangeRateService = slpExchangeRateService;
    }

    @Autowired
    public void setSlpReleaseLevelConfigService(LockSlpReleaseLevelConfigService slpReleaseLevelConfigService) {
        this.slpReleaseLevelConfigService = slpReleaseLevelConfigService;
    }
}
