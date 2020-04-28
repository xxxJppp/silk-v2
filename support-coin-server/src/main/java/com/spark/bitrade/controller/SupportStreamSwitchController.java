package com.spark.bitrade.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.biz.IPayRecordService;
import com.spark.bitrade.biz.IStreamSwitchService;
import com.spark.bitrade.biz.ISupportPushService;
import com.spark.bitrade.config.PayConfigProperties;
import com.spark.bitrade.constant.ModuleType;
import com.spark.bitrade.constant.NoticeMessageConstant;
import com.spark.bitrade.constant.SectionTypeEnum;
import com.spark.bitrade.constant.SupportCoinMsgCode;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.SupportPayRecords;
import com.spark.bitrade.entity.SupportStreamRecords;
import com.spark.bitrade.entity.SupportUpCoinApply;
import com.spark.bitrade.form.ChangeSectionForm;
import com.spark.bitrade.param.StreamSearchParam;
import com.spark.bitrade.service.SupportStreamRecordsService;
import com.spark.bitrade.service.SupportUpCoinApplyService;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.util.SupportUtil;
import com.spark.bitrade.vo.SupportStreamSwitchVo;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import com.spark.bitrade.web.resubmit.annotation.ForbidResubmit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>
 * 引流开关 前端控制器
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
@RestController
@RequestMapping("api/v2/streamSwitch")
@Api(description = "引流开关接口")
@Slf4j
public class SupportStreamSwitchController {


    @Autowired
    private IStreamSwitchService streamSwitchService;
    @Autowired
    private IPayRecordService payRecordService;
    @Autowired
    private ISupportPushService pushService;
    @Autowired
    private SupportUpCoinApplyService supportUpCoinApplyService;
    @Autowired
    private SupportStreamRecordsService supportStreamRecordsService;

    @PostMapping("openStreamSwitch")
    @ApiOperation(value = "引流开关申请", notes = "引流开关申请")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "引流开关申请", name = "form", dataTypeClass = ChangeSectionForm.class)
    })
    @ForbidResubmit
    public MessageRespResult openStreamSwitch(@ApiIgnore @MemberAccount Member member,
                                              @RequestParam String payCoin,
                                              @RequestParam String remark,
                                              @RequestParam String moneyPassword) {
        SupportUtil.validatePassword(moneyPassword,member.getJyPassword(),member.getSalt());
        SupportUpCoinApply coinUpApply = supportUpCoinApplyService.findApprovedUpCoinByMember(member.getId());
        AssertUtil.isTrue(coinUpApply.getRealSectionType()== SectionTypeEnum.SUPPORT_UP_ZONE,
                SupportCoinMsgCode.CANT_OPEN_STREAM_SWITCH);
        SupportStreamRecords streamRecords = streamSwitchService.generateStreamRecord(member.getId(), remark, coinUpApply.getId());
        //有效用户
        Integer personCount = supportUpCoinApplyService.validPersonCount(coinUpApply.getCoin());

        SupportPayRecords payRecords = payRecordService.generatePayRecord(member.getId(),
                streamRecords.getUpCoinId(),
                ModuleType.DRAINAGE_MANAGE,
                payCoin,
                "打开引流开关支付",SupportUtil.getStreamAmount(personCount));

        streamSwitchService.doSaveStreamSwitchApply(streamRecords, payRecords);

        // 消息推送
        String content= String.format(NoticeMessageConstant.STREAM_SWITCH_MESSAGE_SUBMIT,
                coinUpApply.getCoin(),payCoin,payRecords.getPayAmount().toPlainString());
        pushService.sendStationMessage(content,"打开引流开关",member.getId());
        return MessageRespResult.success();
    }


    @PostMapping("findStreamSwitchList")
    @ApiOperation(value = "查询引流开关申请记录", notes = "查询引流开关申请记录")
    public MessageRespResult<IPage<SupportStreamSwitchVo>> findStreamSwitchList(@ApiIgnore @MemberAccount Member member,
                                                                               StreamSearchParam param) {
        IPage<SupportStreamSwitchVo> streamSwitchRecord = streamSwitchService.findStreamSwitchRecord(member.getId(), param);
        return MessageRespResult.success4Data(streamSwitchRecord);
    }

    @ApiOperation(value = "查询上线交易对", notes = "查询上线交易对")
    @PostMapping("findMatchCoin")
    public MessageRespResult findMatchCoin(@ApiIgnore @MemberAccount Member member){
        SupportUpCoinApply approved = supportUpCoinApplyService.findApprovedUpCoinByMember(member.getId());
        return MessageRespResult.success4Data(supportStreamRecordsService.findCoinMatch(approved.getCoin()));
    }

}

