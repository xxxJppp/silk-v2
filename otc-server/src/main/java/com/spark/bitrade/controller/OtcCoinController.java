package com.spark.bitrade.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spark.bitrade.entity.CurrencyManage;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.enums.OtcExceptionMsg;
import com.spark.bitrade.service.CurrencyManageService;
import com.spark.bitrade.service.CurrencyRateService;
import com.spark.bitrade.service.OtcCoinService;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


/**
 * (OtcCoin)表控制层
 *
 * @author ss
 * @date 2020-03-19 10:23:48
 */
@RestController
@RequestMapping("api/v2/otcCoin")
@Api(tags = "otc")
public class OtcCoinController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private OtcCoinService otcCoinService;
    @Resource
    private CurrencyRateService currencyRateService;
    @Resource
    private CurrencyManageService currencyManageService;


    /**
     * 取得正常的币种和余额
     *
     * @return
     */
    @PostMapping(value = "pcall")
    @ApiOperation(value = "取得正常的币种和余额")
    public MessageRespResult allCoin(@MemberAccount Member member) throws Exception {
        List<Map<String, Object>> list = otcCoinService.getAllNormalCoinAndBalance(member.getId());
        List<Map<String, Object>> result = new ArrayList<>();
        //
        MessageRespResult<CurrencyManage> currencyResult = currencyManageService.getMemberPaySetting(member);
        AssertUtil.isTrue(currencyResult.isSuccess(), OtcExceptionMsg.MEMBER_HAS_NO_BASE_CURRENCY);
        list.stream().forEachOrdered(x -> {
            String[] ids = x.get("currencyId").toString().split(",");
            Arrays.stream(ids).forEach(id -> {
                if (id.equals(currencyResult.getData().getId().toString())) {
                    x.put("tradeMinLimit", currencyResult.getData().getMinAmount());
                    x.put("tradeMaxLimit", currencyResult.getData().getMaxAmount());
                    x.put("marketPrice", currencyRateService.getCurrencyRate(currencyResult.getData().getUnit(), String.valueOf(x.get("unit"))));
                    result.add(x);
                }
            });
        });
        return success(result);
    }


}
