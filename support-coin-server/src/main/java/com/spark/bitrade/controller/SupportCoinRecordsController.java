package com.spark.bitrade.controller;

import com.spark.bitrade.biz.ICoinRecordService;
import com.spark.bitrade.biz.ISupportPushService;
import com.spark.bitrade.constant.NoticeMessageConstant;
import com.spark.bitrade.constant.SupportCoinMsgCode;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.form.SupportCoinRecordsForm;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.util.StringUtil;
import com.spark.bitrade.vo.CoinApplyVo;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import com.spark.bitrade.web.resubmit.annotation.ForbidResubmit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.05 09:49
 */
@Slf4j
@RestController
@RequestMapping("api/v2/coin")
@Api(description = "上币币种基本信息申请控制层")
public class SupportCoinRecordsController extends ApiController {

    @Autowired
    private ICoinRecordService recordService;

    @Autowired
    private ISupportPushService pushService;

    /**
     * 获取上币申请
     *
     * @param member
     * @return 信息
     * @author Zhong Jiang
     * @date 2019.11.05 9:51
     */
    @PostMapping("/getOne")
    @ApiOperation(value = "获取某个币种基本信息", notes = "获取某个币种基本信息")
    public MessageRespResult<CoinApplyVo> getCoinRecord(@MemberAccount Member member) {
        return success(recordService.getCoinApplyVo(member.getId()));
    }

    @PostMapping("/add/coinRecordApply")
    @ApiOperation(value = "币种基本信息修改", notes = "币种基本信息修改")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "币种基本信息修改申请表单", name = "recordsForm", dataTypeClass = SupportCoinRecordsForm.class)
    })
    @ForbidResubmit
    public MessageRespResult addCoinRecordApply(@MemberAccount Member member,
                                                @ApiIgnore @Valid SupportCoinRecordsForm recordsForm) throws UnsupportedEncodingException {
        if (StringUtils.isEmpty(recordsForm.getIntroCn())) {
            throw new MessageCodeException(SupportCoinMsgCode.COIN_MESSAGE_INTRO_NOT_EMPTY);
        }
        recordsForm.setMemberId(String.valueOf(member.getId()));
        recordService.saveCoinRecordApply(recordsForm);
        // 消息推送
        String content = NoticeMessageConstant.COIN_INFO_MESSAGE_SUBMIT;
        pushService.sendStationMessage(content, "币种基本信息修改申请", member.getId());
        return success();
    }

}
