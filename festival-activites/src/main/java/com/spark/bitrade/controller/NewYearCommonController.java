package com.spark.bitrade.controller;

import com.spark.bitrade.biz.MemberDailyTaskBizService;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.NewYearConfig;
import com.spark.bitrade.entity.NewYearMemberInfo;
import com.spark.bitrade.service.AppIndexConfigService;
import com.spark.bitrade.service.NewYearConfigService;
import com.spark.bitrade.service.NewYearMemberInfoService;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.vo.AppIndexConfigVo;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
@Api(value = "公共接口")
@RestController
@RequestMapping("api/v2/common")
public class NewYearCommonController {

    @Autowired
    private NewYearMemberInfoService newYearMemberInfoService;
    @Autowired
    private NewYearConfigService newYearConfigService;
    @Autowired
    private AppIndexConfigService appIndexConfigService;
    @ApiOperation(tags = "公共接口" ,value = "剩余挖矿次数")
    @RequestMapping(value = "/myDigTimes",method = {RequestMethod.POST,RequestMethod.GET})
    public MessageRespResult myDigTimes(@MemberAccount Member member){
        Integer times=0;
        NewYearMemberInfo info = newYearMemberInfoService.findRecordByMemberId(member.getId());
        if (info!=null){
            times= Optional.ofNullable(info.getDigTimes()).orElse(0);
        }
        return MessageRespResult.success4Data(times);
    }

    @ApiOperation(tags = "公共接口" ,value = "活动配置:返回活动结束时间")
    @RequestMapping(value = "no-auth/actConfig",method = {RequestMethod.POST,RequestMethod.GET})
    public MessageRespResult actConfig(){
        List<NewYearConfig> newYearConfig = newYearConfigService.findNewYearConfig();
        return MessageRespResult.success4Data(newYearConfig.get(0).getLuckyEndTime());
    }

    @ApiOperation(tags = "公共接口" ,value = "app快捷入口配置")
    @RequestMapping(value = "no-auth/appIndexConfig",method = {RequestMethod.POST,RequestMethod.GET})
    public MessageRespResult<List<AppIndexConfigVo>> appIndexConfig(HttpServletRequest request){
        String language = request.getHeader("language");
        if(StringUtils.isBlank(language)){
            language= "zh_CN";
        }
        return MessageRespResult.success4Data(appIndexConfigService.appIndexList(language));
    }

}
