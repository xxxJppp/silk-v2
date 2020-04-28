package com.spark.bitrade.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.spark.bitrade.constant.MemberLevelEnum;
import com.spark.bitrade.entity.AmountOfDiscountRecord;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.enums.OtcExceptionMsg;
import com.spark.bitrade.service.AmountOfDiscountRecordService;
import com.spark.bitrade.service.IMemberBenefitsService;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 经纪人优惠兑币限额记录(AmountOfDiscountRecord)表控制层
 *
 * @author ss
 * @date 2020-04-08 15:58:56
 */
@RestController
@RequestMapping("amountOfDiscountRecord")
@Api(tags = "经纪人优惠额度控制器")
public class AmountOfDiscountRecordController extends ApiController{
    /**
     * 服务对象
     */
    @Resource
    private AmountOfDiscountRecordService amountOfDiscountRecordService;
    @Resource
    private IMemberBenefitsService memberLevelService;

    @ApiOperation(value = "获取经纪人优惠剩余额度")
    @PostMapping("getDiscountAmount")
    public MessageRespResult<AmountOfDiscountRecord> exchangeUsdc(@MemberAccount Member member) {
        return success(amountOfDiscountRecordService.getByMemberId(member.getId()));
    }

}