package com.spark.bitrade.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.biz.ISupportPushService;
import com.spark.bitrade.biz.IUpCoinApplyService;
import com.spark.bitrade.config.PayConfigProperties;
import com.spark.bitrade.constant.*;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.SupportConfigList;
import com.spark.bitrade.entity.SupportUpCoinApply;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.form.SupportUpCoinApplyForm;
import com.spark.bitrade.param.PageParam;
import com.spark.bitrade.service.*;
import com.spark.bitrade.util.DateUtil;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.util.MessageResult;
import com.spark.bitrade.util.SupportUtil;
import com.spark.bitrade.vo.NumberOfPeopleVo;
import com.spark.bitrade.vo.UpCoinApplyRecordVo;
import com.spark.bitrade.vo.UpCoinConfigVo;
import com.spark.bitrade.vo.WidthRechargeStaticsVo;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import com.spark.bitrade.web.resubmit.annotation.ForbidResubmit;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * <p>
 * 扶持上币项目方主表 前端控制器
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
@RestController
@RequestMapping("api/v2/supportUpCoinApply")
@Api(description = "扶持上币项目方接口")
@Slf4j
public class SupportUpCoinApplyController {


    @Autowired
    private IUpCoinApplyService upCoinApplyService;
    @Autowired
    private ISupportPushService pushService;
    @Autowired
    private ICoinApiService coinApiService;
    @Autowired
    private SupportUpCoinApplyService supportUpCoinApplyService;
    @Autowired
    private SupportConfigService supportConfigService;
    @Autowired
    private ICoinExchange coinExchange;
    @Autowired
    private PayConfigProperties payConfigProperties;
    @PostMapping("upCoinApply")
    @ApiOperation(value = "上币申请", notes = "上币申请")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "申请表单", name = "applyForm", dataTypeClass = SupportUpCoinApplyForm.class)
    })
    @ForbidResubmit
    public MessageRespResult upCoinApply(@ApiIgnore @MemberAccount Member member,
                                         @Valid SupportUpCoinApplyForm applyForm, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            throw new MessageCodeException(SupportCoinMsgCode.PARAMETER_INCORRECT);
        }
        //验证是否能申请扶持上币
        upCoinApplyService.validateCanUpCoin(member.getId(),applyForm.getCoin());
        //执行申请
        //解码微信地址
        applyForm.setWechatUrl(SupportUtil.decodeUrl(applyForm.getWechatUrl()));
        upCoinApplyService.doUpCoinApply(member.getId(), applyForm);
        // 消息推送
        String content = String.format(NoticeMessageConstant.UP_COIN_MESSAGE_SUBMIT, applyForm.getName());
        pushService.sendStationMessage(content, "自主上币申请", member.getId());
        return MessageRespResult.success();
    }

    @PostMapping("upCoinStatus")
    @ApiOperation(value = "上币申请状态", notes = "上币申请状态")
    public MessageRespResult<UpCoinApplyRecordVo> upCoinStatus(@ApiIgnore @MemberAccount Member member) {
        UpCoinApplyRecordVo record = upCoinApplyService.findUpCoinRecordByMember(member.getId());
        return MessageRespResult.success4Data(record);
    }

    @PostMapping("hasEntrance")
    @ApiOperation(value = "是否有项目方入口", notes = "是否有项目方入口")
    public MessageRespResult hasEntrance(@ApiIgnore @MemberAccount Member member){
        Integer en=supportUpCoinApplyService.hasEntrance(member.getId());
        return MessageRespResult.success4Data(en);
    }


    /**
     * 提供给管理后台调用
     *
     * @param memberId
     * @param title
     * @param content
     * @return
     */
    @PostMapping("sendStationMessage")
    @ApiOperation(value = "发送信息", notes = "发送信息")
    public MessageResult sendStationMessage(@RequestParam(value = "memberId") Long memberId,
                                            @RequestParam(value = "title") String title,
                                            @RequestParam(value = "content") String content) {

        pushService.sendStationMessage(content, title, memberId);
        return MessageResult.success();
    }

    /**
     * 获取上币的配置
     *
     * @return
     */
    @PostMapping("upCoinConfig")
    public MessageRespResult upCoinConfig() {
        UpCoinConfigVo vo = new UpCoinConfigVo();
        SectionTypeEnum[] values = SectionTypeEnum.values();
        List<JSONObject> objects = new ArrayList<>();
        for (SectionTypeEnum typeEnum : values) {
            JSONObject o = new JSONObject();
            o.put("name", typeEnum.getCnName());
            o.put("value", typeEnum.getOrdinal());
            objects.add(o);
        }
        vo.setSectionTypes(objects);
        return MessageRespResult.success4Data(vo);
    }

    /**
     * 上币文案
     *
     * @return
     */
    @ApiOperation(value = "上币文案", notes = "上币文案")
    @PostMapping(value = {"no-auth/upCoinText","upCoinText"})
    public MessageRespResult upCoinText(HttpServletRequest request) {
        String accept = request.getHeader("language");
        Map<String,String> upCoinText = upCoinApplyService.findUpCoinText();
        String resultMessage = upCoinText.get("content");
        if(accept != null && !accept.trim().equals("")){
            if("ko_kr".equals(accept.toLowerCase())){
                //韩文
                resultMessage = upCoinText.get("contentKo");
            }else if("zh_hk".equals(accept.toLowerCase())){
                //繁体中文
                resultMessage = upCoinText.get("contentZhTw");
            }else if("en_us".equals(accept.toLowerCase())){
                //英文
                resultMessage = upCoinText.get("contentEn");
            }
        }

        return MessageRespResult.success4Data(resultMessage);
    }

    @Autowired
    IMemberTransactionApiService memberTransactionApiService;

    @ApiOperation(value = "充值提币统计")
    @PostMapping("widthRechargeStatistics")
    public MessageRespResult<IPage<WidthRechargeStaticsVo>> widthDrawStatistics(@ApiIgnore @MemberAccount Member member,
                                                                                @ApiParam(value = "1:提币统计2:充值统计") @RequestParam Integer type,
                                                                                PageParam pageParam) {
        pageParam.transTime();
        Date start = null;
        if (StringUtils.hasText(pageParam.getStartTime())) {
            start = DateUtil.stringToDate(pageParam.getStartTime(), "yyyy-MM-dd HH:mm:ss");
        }
        Date end = null;
        if (StringUtils.hasText(pageParam.getEndTime())) {
            end = DateUtil.stringToDate(pageParam.getEndTime(), "yyyy-MM-dd HH:mm:ss");
        }
        SupportUpCoinApply apply = upCoinApplyService.getByMemberId(member.getId());
        if (apply == null) {
            return MessageRespResult.success();
        }
        if (type == 1) {
            return memberTransactionApiService.list(TransactionType.WITHDRAW, pageParam.getPage(), pageParam.getPageSize()
                    , start, end, apply.getCoin());
        } else if (type == 2) {
            return memberTransactionApiService.list(TransactionType.RECHARGE, pageParam.getPage(), pageParam.getPageSize()
                    , start, end, apply.getCoin());
        }
        return MessageRespResult.success();
    }


    @ApiOperation(value = "提币审核记录")
    @PostMapping("widthDrawToAuditList")
    public MessageRespResult<IPage<WidthRechargeStaticsVo>> widthDrawToAuditList(@ApiIgnore @MemberAccount Member member, PageParam pageParam) {
        SupportUpCoinApply apply = upCoinApplyService.getByMemberId(member.getId());
        if (apply == null) {
            return MessageRespResult.success4Data(new Page<WidthRechargeStaticsVo>());
        }
        MessageRespResult<String> unit = coinApiService.getCoinNameByUnit(apply.getCoin());
        if (!unit.isSuccess()) {
            return MessageRespResult.success4Data(new Page<WidthRechargeStaticsVo>());
        }
        IPage<WidthRechargeStaticsVo> page = supportUpCoinApplyService.widthDrawToAuditList(new Page<>(pageParam.getPage(), pageParam.getPageSize()),
                pageParam, unit.getData());
        return MessageRespResult.success4Data(page);
    }

    @ApiOperation(value = "提现充值总数人数统计")
    @PostMapping("numberOfPeople")
    public MessageRespResult<NumberOfPeopleVo> numberOfPeople(@ApiIgnore @MemberAccount Member member,
                                                              PageParam pageParam) {
        NumberOfPeopleVo vo =new NumberOfPeopleVo();
        SupportUpCoinApply apply = upCoinApplyService.getByMemberId(member.getId());
        if (apply == null) {
            return MessageRespResult.success4Data(vo);
        }
        MessageRespResult<String> unit = coinApiService.getCoinNameByUnit(apply.getCoin());
        if (!unit.isSuccess()) {
            return MessageRespResult.success4Data(vo);
        }
          vo = upCoinApplyService.numberOfPeople(apply.getCoin(), unit.getData(),pageParam);
        Integer integer = supportUpCoinApplyService.validPersonCount(apply.getCoin());
        vo.setValidPersonCount(integer);
        return MessageRespResult.success4Data(vo);
    }


    @PostMapping("findPayConfigByType")
    @ApiOperation(value = "获取支付配置模块名称{0:引流交易码管理,1:交易对管理,2:转版管理}", notes = "获取支付配置模块名称{0:引流交易码管理,1:交易对管理,2:转版管理}")
    public MessageRespResult<UpCoinConfigVo> findStreamPayConfig(@RequestParam ModuleType type,@ApiIgnore @MemberAccount Member member) {
        SupportUpCoinApply approved = supportUpCoinApplyService.findApprovedUpCoinByMember(member.getId());
        UpCoinConfigVo vo=new UpCoinConfigVo();
        List<SupportConfigList> lists = supportConfigService.findByModule(type);
        List<Map<String, String>> re = new ArrayList<>();
        BigDecimal usdt = BigDecimal.ZERO;
        //交易对
        if(type==ModuleType.EXCHANGE_MANAGE){
            SectionTypeEnum sectionType = approved.getRealSectionType();
            if(sectionType==SectionTypeEnum.SUPPORT_UP_ZONE||sectionType==SectionTypeEnum.INNOVATION_ZONE){
                usdt=new BigDecimal(payConfigProperties.getInnovativeCoin());
            }else {
                usdt=new BigDecimal(payConfigProperties.getMainCoin());
            }
        }
        //引流
        Integer integer = supportUpCoinApplyService.validPersonCount(approved.getCoin());
        if(type==ModuleType.DRAINAGE_MANAGE){
            usdt=SupportUtil.getStreamAmount(integer);
        }
        //转版
        if(type==ModuleType.CHANGE_SECTION_MANAGE){
            usdt=SupportUtil.getChangeSectionAmount(integer);
        }

        for (SupportConfigList c : lists) {
            try {
                MessageRespResult<BigDecimal> usdExchangeRate = coinExchange.getUsdExchangeRate(c.getDictKey());
                if(usdExchangeRate.isSuccess()){
                    Map<String, String> m = new HashMap<>();
                    m.put("coin", c.getDictKey());
                    m.put("value", usdt.divide(usdExchangeRate.getData(), 8, RoundingMode.HALF_UP).toPlainString());
                    re.add(m);
                }
            }catch (Exception e){
                log.error("----------查询汇率失败-------");
            }

        }
        vo.setPayConfigs(re);
        vo.setSwitchStatus(approved.getStreamStatus());
        vo.setTradeCode(approved.getTradeCode());
        vo.setCurrentSection(approved.getRealSectionType());
        vo.setCoin(approved.getCoin());
        vo.setValidPersonCount(integer);
        return MessageRespResult.success4Data(vo);
    }


    @PostMapping("findValidPersonCountByCoin")
    @ApiOperation(value = "查询有效用户数")
    public MessageResult findValidPersonCount(String coinUnit){
        Integer integer = supportUpCoinApplyService.validPersonCount(coinUnit);
        return MessageResult.success("成功",integer);
    }

    @ApiOperation(value = "查询有效用户数")
    @PostMapping("findValidPersonCount")
    public MessageRespResult findValidPersonCount(@MemberAccount Member member){
        SupportUpCoinApply approvedUpCoinByMember = supportUpCoinApplyService.findApprovedUpCoinByMember(member.getId());
        return MessageRespResult.success4Data(supportUpCoinApplyService.validPersonCount(approvedUpCoinByMember.getCoin()));
    }

    @ApiOperation(value = "查询支付条件")
    @PostMapping("findCondition")
    public MessageRespResult findCondition(@RequestParam ModuleType type,@MemberAccount Member member){
        StringBuilder builder=new StringBuilder("");
        SupportUpCoinApply ap = supportUpCoinApplyService.findApprovedUpCoinByMember(member.getId());
        Integer integer = supportUpCoinApplyService.validPersonCount(ap.getCoin());
        if(type==ModuleType.CHANGE_SECTION_MANAGE){
            BigDecimal changeSectionAmount = SupportUtil.getChangeSectionAmount(integer);
            Map<Integer, Integer> changeSectionPay = new TreeMap<>(payConfigProperties.getChangeSectionPay());
            builder.append("你目前的有效拉新用户数为:").append(integer)
                    .append(",申请转版需要缴纳").append(changeSectionAmount.toPlainString())
                    .append("USDT或等值其他币种,收费标准如下:<br/>");
            Set<Map.Entry<Integer, Integer>> entries = changeSectionPay.entrySet();
            for (Map.Entry<Integer,Integer> entry:entries){
                builder.append("有效拉新用户数小于").append(entry.getKey()).append("的,缴纳")
                        .append(entry.getValue()).append("USDT<br/>");
            }
            builder.append("有效拉新用户数大于等于5000的,免费<br/>");

        }

        if(type==ModuleType.DRAINAGE_MANAGE){
            Map<Integer, Integer> streamPay = new TreeMap<>(payConfigProperties.getStreamPay());
            BigDecimal streamAmount = SupportUtil.getStreamAmount(integer);
            builder.append("你目前的有效拉新用户数为:").append(integer)
                    .append(",打开引流开关需要缴纳").append(streamAmount.toPlainString())
                    .append("USDT或等值其他币种,收费标准如下:<br/>");
            Set<Map.Entry<Integer, Integer>> entries = streamPay.entrySet();
            for (Map.Entry<Integer,Integer> entry:entries){
                builder.append("有效拉新用户数小于").append(entry.getKey()).append("的,缴纳")
                        .append(entry.getValue()).append("USDT<br/>");
            }
            builder.append("有效拉新用户数大于等于5000的,免费<br/>");

        }

        if(type==ModuleType.EXCHANGE_MANAGE){
            String sec="";
            Integer a=0;
            if(ap.getRealSectionType()==SectionTypeEnum.SUPPORT_UP_ZONE||ap.getRealSectionType()==SectionTypeEnum.INNOVATION_ZONE){
                a=payConfigProperties.getInnovativeCoin();
                sec="创新区";
            }else {
                a=payConfigProperties.getMainCoin();
                sec="主区";
            }
            builder.append("新增交易对说明:<br/>你的项目是").append(sec)
                    .append("项目,申请增加交易对收每个交易对/").append(a).append("USDT,或等值其他币种<br/>")
                    .append("收费标准:<br/>")
                    .append("创新区项目增加交易对,每个交易对/")
                    .append(payConfigProperties.getInnovativeCoin())
                    .append("USDT或等值其他币种<br/>")
                    .append("主区项目增加交易对,每个交易对/")
                    .append(payConfigProperties.getMainCoin())
                    .append("USDT或等值其他币种");


        }

        return MessageRespResult.success4Data(builder.toString());
    }
}

