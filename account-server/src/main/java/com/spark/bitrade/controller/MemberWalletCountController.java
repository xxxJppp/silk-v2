package com.spark.bitrade.controller;

import com.spark.bitrade.service.MemberWalletService;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.vo.MemberWalletCountVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * 项目方持仓统计
 *
 * @author: Zhong Jiang
 * @time: 2019.11.11 11:50
 */
@RestController
@RequestMapping("v2/memberWallet")
@Api(description = "项目方持仓统计")
public class MemberWalletCountController extends ApiController {

    @Autowired
    private MemberWalletService memberWalletService;

    /**
     * 项目方持仓统计
     *
     * @return true
     * @author Zhong Jiang
     * @time 2019.08.07 20:40
     */
    @ApiOperation(value = "getCountMemberWallet", notes = "getCountMemberWallet")
    @RequestMapping(value = "/getCountMemberWallet", method = {RequestMethod.GET})
    public MessageRespResult<MemberWalletCountVo> getCountMemberWallet( String coinId,
                                                                        BigDecimal balanceStart,
                                                                        BigDecimal balanceEnd,
                                                                        Integer page,
                                                                        Integer pageSize) {
        return success(memberWalletService.countMemberWallet(coinId, balanceStart, balanceEnd, page, pageSize));
    }


}
