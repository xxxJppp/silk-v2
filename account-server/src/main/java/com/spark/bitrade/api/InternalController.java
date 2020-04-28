package com.spark.bitrade.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.spark.bitrade.constant.CommonStatus;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.controller.ApiController;
import com.spark.bitrade.entity.Coin;
import com.spark.bitrade.service.CoinService;
import com.spark.bitrade.support.RateManager;
import com.spark.bitrade.util.MessageRespResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 内部接口
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/11/21 10:46
 */
@Slf4j
@RestController
@RequestMapping("/internal")
public class InternalController extends ApiController {

    private CoinService coinService;
    private RateManager rateManager;

    /**
     * 获取所有系统支持的币种
     *
     * @return resp
     */
    @GetMapping("/coins")
    public MessageRespResult<List<Coin>> coins() {

        // 查询并填充汇率
        //modify by qhliao 增加币种状态查询status=0的为正常
        QueryWrapper<Coin> q=new QueryWrapper<>();
        q.lambda().eq(Coin::getStatus, CommonStatus.NORMAL);
        List<Coin> list = coinService.list(q).stream().peek(coin -> {
            RateManager.CoinRate rate = rateManager.get(coin);
            coin.setUsdRate(rate.usdOrElse(coin.getUsdRate()));
            coin.setCnyRate(rate.cnyOrElse(coin.getCnyRate()));
        }).collect(Collectors.toList());

        return success(list);
    }

    /**
     * 获取币种
     *
     * @param unit 币种单位
     * @return resp
     */
    @GetMapping("/coins/{unit}")
    public MessageRespResult<Coin> getCoin(@PathVariable("unit") String unit) {
        Coin coin = coinService.findByUnit(unit);
        if (coin != null) {
            RateManager.CoinRate rate = rateManager.get(coin);
            coin.setUsdRate(rate.usdOrElse(coin.getUsdRate()));
            coin.setCnyRate(rate.cnyOrElse(coin.getCnyRate()));
            return success(coin);
        }

        return failed(CommonMsgCode.FAILURE);
    }


    @Autowired
    public void setCoinService(CoinService coinService) {
        this.coinService = coinService;
    }

    @Autowired
    public void setRateManager(RateManager rateManager) {
        this.rateManager = rateManager;
    }
}
