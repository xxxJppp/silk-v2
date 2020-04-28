package com.spark.bitrade.controller;

import com.spark.bitrade.entity.ExchangeCoin;
import com.spark.bitrade.entity.SilktraderConfig;
import com.spark.bitrade.entity.SymbolConifg;
import com.spark.bitrade.service.ExchangeCoinService.ExchangeCoinService;
import com.spark.bitrade.util.MessageRespResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 交易所初始化信息接口
 * @author: ss
 * @date: 2020/2/26
 */
@RestController
@RequestMapping("/silktrader")
@Api(tags = "交易所初始化")
public class InitController extends ApiController{

    @Autowired
    private ExchangeCoinService exchangeCoinService;

    @ApiOperation(value = "交易所初始化接口", tags = "初始化接口")
    @PostMapping(value = {"/getInit", "/no-auth/getInit"})
    public MessageRespResult<SilktraderConfig> init(){
        SilktraderConfig init = new SilktraderConfig();
        List<ExchangeCoin> coinList = exchangeCoinService.getAllSymbol();
        List<SymbolConifg> symbolConifgList = new ArrayList<>();
        coinList.stream().forEach(item -> {
            symbolConifgList.add(new SymbolConifg(item));
        });
        init.setSymbolConfig(symbolConifgList);
        return success(init);
    }
}
