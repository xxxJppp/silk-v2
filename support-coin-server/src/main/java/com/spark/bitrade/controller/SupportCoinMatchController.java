package com.spark.bitrade.controller;

import com.spark.bitrade.biz.ICoinMatchService;
import com.spark.bitrade.biz.ISupportPushService;
import com.spark.bitrade.constant.NoticeMessageConstant;
import com.spark.bitrade.constant.SectionTypeEnum;
import com.spark.bitrade.constant.SupportCoinMsgCode;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.SupportPayRecords;
import com.spark.bitrade.entity.SupportUpCoinApply;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.form.CoinMathForm;
import com.spark.bitrade.param.CoinMatchParam;
import com.spark.bitrade.service.SupportUpCoinApplyService;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.util.SupportUtil;
import com.spark.bitrade.vo.CoinMatchVo;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.06 09:29
 */
@Slf4j
@RestController
@RequestMapping("api/v2/coinMatch")
@Api(description = "交易对信息控制层")
public class SupportCoinMatchController extends ApiController{

    @Autowired
    private ICoinMatchService coinMatchService;

    @Autowired
    private ISupportPushService pushService;


    @Autowired
    private SupportUpCoinApplyService upCoinApplyService;
    /**
     * 获取交易对信息列表
     *
     * @author Zhong Jiang
     * @date 2019.11.05 9:19
     * @return 菜单列表
     */
    @ApiOperation(value = "交易对信息列表", tags = "交易对信息列表")
    @PostMapping("/list")
    public MessageRespResult<CoinMatchVo> list(@MemberAccount Member member, @ApiParam("查询公共参数") @Valid CoinMatchParam pageParam) {
        CoinMatchVo list = coinMatchService.findCoinMacthesList(member.getId(), pageParam);
        return success(list);
    }

    /**
     * 新增交易对
     * @return
     */
    @ApiOperation(value = "添加交易对", tags = "添加交易对")
    @PostMapping("/add")
    public MessageRespResult addCoinMath(@ApiIgnore @MemberAccount Member member,
                                         @Valid CoinMathForm coinMathForm,
                                         @RequestParam String moneyPassword,
                                         BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            throw new MessageCodeException(SupportCoinMsgCode.PARAMETER_INCORRECT);
        }
        SupportUpCoinApply apply = upCoinApplyService.findApprovedUpCoinByMember(member.getId());
// 扶持上币不允许新增交易对
        //        if(apply.getRealSectionType()==SectionTypeEnum.SUPPORT_UP_ZONE){
//            AssertUtil.isTrue(apply.getStreamStatus()==1,
//                    SupportCoinMsgCode.OPEN_STREAM_SWITCH_AFTER_ADD_MATCH);
//        }
        AssertUtil.isTrue(apply.getRealSectionType()!= SectionTypeEnum.SUPPORT_UP_ZONE,
                SupportCoinMsgCode.CANT_ADD_MATCH_COIN);
        SupportUtil.validatePassword(moneyPassword,member.getJyPassword(),member.getSalt());
        SupportPayRecords supportPayRecords = coinMatchService.addCoinMatch(member, coinMathForm, apply);

        String content=String.format( NoticeMessageConstant.MATCH_COIN_MESSAGE_SUBMIT, apply.getCoin(),
                coinMathForm.getTargetCoin(), coinMathForm.getPayCoin(), supportPayRecords.getPayAmount());
        pushService.sendStationMessage(content,"交易对添加申请",Long.valueOf(member.getId()));
        return success();
    }
}
