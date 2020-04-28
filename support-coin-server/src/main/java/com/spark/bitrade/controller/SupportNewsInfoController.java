package com.spark.bitrade.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.biz.INewsInfoService;
import com.spark.bitrade.biz.ISupportPushService;
import com.spark.bitrade.constant.NoticeMessageConstant;
import com.spark.bitrade.constant.SupportCoinMsgCode;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.SupportNewsInfo;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.form.SupportCoinRecordsForm;
import com.spark.bitrade.form.SupportNewsInfoForm;
import com.spark.bitrade.param.NewInfoParam;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import com.spark.bitrade.web.resubmit.annotation.ForbidResubmit;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.05 16:02
 */
@Slf4j
@RestController
@RequestMapping("api/v2/news")
@Api(description = "项目咨询控制层")
public class SupportNewsInfoController extends ApiController {

    @Autowired
    private INewsInfoService newsInfoService;

    @Autowired
    private ISupportPushService pushService;

    @PostMapping("/list")
    @ApiOperation(value = "获取项目方资讯列表", tags = "获取项目方资讯列表")
    public MessageRespResult<IPage<SupportNewsInfo>> list(@MemberAccount Member member,
                                                          @ApiParam("查询公共参数")  NewInfoParam pageParam) {

        IPage<SupportNewsInfo> page = newsInfoService.findNewsInfosList(member.getId(), pageParam);
        return success(page);
    }

    @PostMapping("/add/newApply")
    @ApiOperation(value = "新增资讯", notes = "新增资讯")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "资讯表单", name = "资讯Form", dataTypeClass = SupportCoinRecordsForm.class)
    })
    @ForbidResubmit
    public MessageRespResult addNewsInfo(@Valid SupportNewsInfoForm newsInfoForm) {
        log.info("添加参数为， {}", newsInfoForm);
        newsInfoService.saveNewsInfos(newsInfoForm);
        String content = String.format(NoticeMessageConstant.NEWS_INFO_MESSAGE_SUBMIT, newsInfoForm.getTitle());
        pushService.sendStationMessage(content, "项目资讯添加申请", Long.valueOf(newsInfoForm.getMemberId()));
        return success();
    }


}
