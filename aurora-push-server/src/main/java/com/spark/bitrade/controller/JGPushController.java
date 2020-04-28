package com.spark.bitrade.controller;


import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Platform;
import com.spark.bitrade.dto.PushJGDto;
import com.spark.bitrade.util.JGPushObjectUtils;
import com.spark.bitrade.util.JGPushRequestUtils;
import com.spark.bitrade.util.MessageRespResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static com.spark.bitrade.util.MessageRespResult.success;


@Slf4j
@RestController
@RequestMapping("jgpush")
@Api(value = "BCC JGpush Controller", tags = {"接口操作"})
public class JGPushController {


    @Autowired
    private JPushClient jPushClient;

    @RequestMapping(value = "/push", method = RequestMethod.POST)
    @ApiOperation(value = "发送通知或者消息")
    @ApiImplicitParam(value = "pushJGDto", dataTypeClass = PushJGDto.class)
    public MessageRespResult<String> push(@RequestBody PushJGDto pushJGDto) {
        Platform platform = null;
        switch (pushJGDto.getDeviceType()){
            case ALL:
                platform = Platform.all();
                break;
            case IOS:
                platform = Platform.ios();
                break;
            case Android:
                platform = Platform.android();
                break;
                default:
                    break;
        }
        log.info("发送消息：{}",pushJGDto);
        PushResult pushResult = JGPushRequestUtils.pushPayload(jPushClient, JGPushObjectUtils.buildPushObject(pushJGDto.getTags()
                , pushJGDto.getAlias()
                , pushJGDto.getExtras()
                , platform
                , pushJGDto.getAlert()
                , pushJGDto.getMessage()
                , pushJGDto.getBadge(),
                false));
        return success(String.valueOf(pushResult.msg_id));
    }

}
