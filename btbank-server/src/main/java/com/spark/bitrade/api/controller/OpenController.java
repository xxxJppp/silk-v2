package com.spark.bitrade.api.controller;

import com.spark.bitrade.api.vo.OrderReceiverVO;
import com.spark.bitrade.biz.MinePoolService;
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
 * @author davi
 */
@Api(tags = "开放接口控制器")
@RequestMapping(path = "api/v2/no-auth/dmz", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@AllArgsConstructor
public class OpenController {
    private MinePoolService minePoolService;

    @ApiOperation(value = "公开接口-接单")
    @PostMapping("orderReceiver")
    public MessageRespResult orderReceiver(@RequestBody OrderReceiverVO vo) {
        minePoolService.receiveOrder(vo);
        return MessageRespResult.success();
    }
}
