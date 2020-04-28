package com.spark.bitrade.service;

import com.spark.bitrade.constant.FeignServiceConstant;
import com.spark.bitrade.util.MessageRespResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * BT OPEN API 内部调用
 *
 * @author
 * @time
 */
@FeignClient(FeignServiceConstant.BTBANK_SERVER)
@RequestMapping("btbank")
public interface IBtbankServerService {

    @PostMapping("inner/schedule/auto/dispatch")
    MessageRespResult autoDispatch();

    @PostMapping("inner/schedule/auto/unlock")
    MessageRespResult autoUnlock();
}
