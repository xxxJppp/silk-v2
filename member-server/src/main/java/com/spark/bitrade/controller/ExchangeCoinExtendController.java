package com.spark.bitrade.controller;


import javax.validation.Valid;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spark.bitrade.biz.IExchangeCoinService;
import com.spark.bitrade.entity.ExchangeCoinExtend;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;

import io.swagger.annotations.Api;

/**
 * <p>
 * 币币交易-交易对扩展表 前端控制器
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-18
 */
@RestController
@RequestMapping("api/v2/member/exchangeCoinExtend")
@Api(tags = "币币交易")
public class ExchangeCoinExtendController extends ApiController{


    @Autowired
    private IExchangeCoinService exchangeCoinService;

    @PostMapping("/findBySymbol")
    @ApiOperation(value = "币币交易费率", notes = "币币交易费率")
    public MessageRespResult<ExchangeCoinExtend> findExchangeCoinExtend(@MemberAccount Member member, @Valid String symbol) {
        ExchangeCoinExtend extendBySymbol = exchangeCoinService.getExchangeCoinExtendBySymbol(symbol);
        return success(extendBySymbol);
    }

}
