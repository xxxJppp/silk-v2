package com.spark.bitrade.api.controller;

import com.spark.bitrade.api.vo.OrderReceiverVO;
import com.spark.bitrade.biz.ScheduleService;
import com.spark.bitrade.util.MessageRespResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shenzucai
 * @time 2019.10.24 16:32
 */
@Api(tags = "定时调度控制器，仅限内网调用")
@RequestMapping(value = "inner/schedule", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@AllArgsConstructor
public class ScheduleInnerController {

    private ScheduleService scheduleService;

    @ApiOperation(value = "自动派单")
    @PostMapping("auto/dispatch")
    public MessageRespResult autoDispatch() {
        scheduleService.autoDispatch();
        return MessageRespResult.success();
    }

    @ApiOperation(value = "自动解锁")
    @PostMapping("auto/unlock")
    public MessageRespResult autoUnlock() {
        scheduleService.unLockAssert();
        return MessageRespResult.success();
    }
}
