package com.spark.bitrade.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.biz.IExchangeOrderCountService;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.param.ExchangeOrderParam;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.vo.ExchangeOrderListVo;
import com.spark.bitrade.vo.ExchangeOrderStaticsVo;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.13 09:27
 */
@Slf4j
@RestController
@RequestMapping("api/v2/exchangeOrder")
@Api(description = "买卖盘统计控制层")
public class SupportExchangeOrderCountController extends ApiController {

    @Autowired
    private IExchangeOrderCountService exchangeOrderCountService;

    @PostMapping("/list")
    @ApiOperation(value = "买盘卖盘统计", tags = "买盘卖盘统计")
    public MessageRespResult<IPage<ExchangeOrderListVo>> buyExchangeOrderCount(@MemberAccount Member member,
                                                                               @ApiParam("查询公共参数") @Valid ExchangeOrderParam param) {
        IPage<ExchangeOrderListVo> page = exchangeOrderCountService.exchangeOrders(member, param);
        return success(page);
    }

    @PostMapping("/countTotal")
    @ApiOperation(value = "买盘统计", tags = "买盘统计")
    public MessageRespResult<ExchangeOrderStaticsVo> countTotal(@MemberAccount Member member, String startTime, String endTime){

        return MessageRespResult.success4Data(exchangeOrderCountService.exchangeOrdersCount(member.getId(),startTime,endTime));
    }

}







