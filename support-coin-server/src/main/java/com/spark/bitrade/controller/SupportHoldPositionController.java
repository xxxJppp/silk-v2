package com.spark.bitrade.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.biz.IHoldPositionService;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.param.HoldCountParam;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.vo.HoldPositionVo;
import com.spark.bitrade.vo.MembertVo;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.07 11:39
 */
@Slf4j
@RestController
@RequestMapping("api/v2/hold")
@Api(description = "持仓统计控制层")
public class SupportHoldPositionController extends ApiController {

    @Autowired
    private IHoldPositionService positionService;

    @PostMapping("/count")
    @ApiOperation(value = "持仓统计", notes = "持仓统计")
    public MessageRespResult<HoldPositionVo> countHoldPositions(@MemberAccount Member member,
                                                                @ApiParam("查询公共参数") @Valid HoldCountParam param) {
        HoldPositionVo iPage = positionService.countMemberWallet(member.getId(), param);
        return success(iPage);
    }
}
