package com.spark.bitrade.controller;

import com.spark.bitrade.service.UserConfigurationCenterService;
import com.spark.bitrade.util.MessageRespResult;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 个人中心-消息提醒设置
 *
 * @author zhongxj
 * @date 2019.10.21
 */
@Slf4j
@RestController
@RequestMapping("api/v2/userCenterConfiguration")
@Api(description = "个人中心-消息提醒设置")
public class UserCenterConfigurationController extends ApiController {
    @Resource
    private UserConfigurationCenterService userConfigurationCenterService;

    /**
     * 新增
     *
     * @param memberId        会员ID
     * @param triggeringEvent 事件{0:充值到账提醒,1:新订单创建提醒,2:交易即将过期,3:已付款提醒,4:已释放提醒,5:申诉处理结果提醒}
     * @param isSms           短信
     * @param isEmail         邮件
     * @param isApns          APP通知
     * @return
     */
    @PostMapping(value = {"/no-auth/addUserConfigurationCenter"})
    public MessageRespResult addUserConfigurationCenter(Long memberId, Integer triggeringEvent, Integer isSms, Integer isEmail, Integer isApns) {
        Integer count = userConfigurationCenterService.addUserConfigurationCenter(memberId, triggeringEvent, isSms, isEmail, isApns);
        if (count > 0) {
            return success();
        } else {
            return failed();
        }
    }

    /**
     * 修改
     *
     * @param memberId        会员ID
     * @param triggeringEvent 事件{0:充值到账提醒,1:新订单创建提醒,2:交易即将过期,3:已付款提醒,4:已释放提醒,5:申诉处理结果提醒}
     * @param channel         渠道，isSms-短信，isEmail-邮件，isApns-APP
     * @param status          开关，1-开启，2-关闭
     * @return
     */
    @PostMapping(value = {"/no-auth/updateUserConfigurationCenter"})
    public MessageRespResult updateUserConfigurationCenter(Long memberId, Integer triggeringEvent, String channel, Integer status) {
        Integer count = userConfigurationCenterService.updateUserConfigurationCenter(memberId, triggeringEvent, channel, status);
        if (count > 0) {
            return success();
        } else {
            return failed();
        }
    }
}
