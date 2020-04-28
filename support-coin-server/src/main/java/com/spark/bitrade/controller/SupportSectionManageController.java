package com.spark.bitrade.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.biz.IPayRecordService;
import com.spark.bitrade.biz.ISectionManageService;
import com.spark.bitrade.biz.ISupportPushService;
import com.spark.bitrade.constant.ModuleType;
import com.spark.bitrade.constant.NoticeMessageConstant;
import com.spark.bitrade.constant.SectionTypeEnum;
import com.spark.bitrade.constant.SupportCoinMsgCode;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.SupportPayRecords;
import com.spark.bitrade.entity.SupportSectionManage;
import com.spark.bitrade.entity.SupportUpCoinApply;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.form.ChangeSectionForm;
import com.spark.bitrade.param.SectionSearchParam;
import com.spark.bitrade.service.SupportUpCoinApplyService;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.util.SupportUtil;
import com.spark.bitrade.vo.SupportSectionManageVo;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import com.spark.bitrade.web.resubmit.annotation.ForbidResubmit;
import io.swagger.annotations.*;
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
 * <p>
 * 转版管理 前端控制器
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
@RestController
@RequestMapping("api/v2/sectionManage")
@Api(description = "转版管理接口")
@Slf4j
public class SupportSectionManageController {


    @Autowired
    private ISectionManageService sectionManageService;
    @Autowired
    private IPayRecordService payRecordService;
    @Autowired
    private ISupportPushService pushService;
    @Autowired
    private SupportUpCoinApplyService supportUpCoinApplyService;
    @PostMapping("changeSectionApply")
    @ApiOperation(value = "转版申请", notes = "转版申请")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "转版申请", name = "form", dataTypeClass = ChangeSectionForm.class)
    })
    @ForbidResubmit
    public MessageRespResult changeSectionApply(@ApiIgnore @MemberAccount Member member,
                                                @Valid ChangeSectionForm form,
                                                @RequestParam String moneyPassword,
                                                BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            throw new MessageCodeException(SupportCoinMsgCode.PARAMETER_INCORRECT);
        }
        SupportUtil.validatePassword(moneyPassword,member.getJyPassword(),member.getSalt());
        //查询上币申请
        SupportUpCoinApply apply = supportUpCoinApplyService.findApprovedUpCoinByMember(member.getId());
        if(apply.getRealSectionType()== SectionTypeEnum.SUPPORT_UP_ZONE){
            AssertUtil.isTrue(apply.getStreamStatus()==1,
                    SupportCoinMsgCode.OPEN_STREAM_SWITCH_AFTER_CHANGE_SECTION);
        }
        //构造申请记录
        SupportSectionManage sectionManage = sectionManageService.generateApply(member.getId(), form, apply);
        //有效用户
        Integer personCount = supportUpCoinApplyService.validPersonCount(apply.getCoin());
        //构造支付记录
        SupportPayRecords payRecords = payRecordService.generatePayRecord(member.getId(),
                sectionManage.getUpCoinId(), ModuleType.CHANGE_SECTION_MANAGE,
                form.getPayCoin(), "转版申请支付", SupportUtil.getChangeSectionAmount(personCount));
        //执行保存
        sectionManageService.doApplySectionManage(sectionManage, payRecords);

        // 消息推送
        String content = String.format(NoticeMessageConstant.CHANGE_SECTION_MESSAGE_SUBMIT, sectionManage.getCurrentSection().getCnName()
                , sectionManage.getTargetSection().getCnName(), payRecords.getPayCoin(), payRecords.getPayAmount().toPlainString());
        pushService.sendStationMessage(content, "转版申请", member.getId());
        return MessageRespResult.success();
    }

    @PostMapping("findSectionList")
    @ApiOperation(value = "查询转版申请记录", notes = "查询转版申请记录")
    public MessageRespResult<IPage<SupportSectionManageVo>> findSectionList(@ApiIgnore @MemberAccount Member member,
                                                                           @ApiParam("查询公共参数") SectionSearchParam param) {
        IPage<SupportSectionManageVo> sectionRecords = sectionManageService.findSectionRecords(member.getId(), param);

        return MessageRespResult.success4Data(sectionRecords);
    }


}
























