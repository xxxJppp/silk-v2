package com.spark.bitrade.controller;


import com.spark.bitrade.entity.AppItem;
import com.spark.bitrade.service.SilkPayAppService;
import com.spark.bitrade.util.MessageRespResult;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 应用管理(SilkPayApp)控制层
 *
 * @author wsy
 * @since 2019-07-19 16:28:05
 */
@RestController
@RequestMapping("api/v2/silkPayApp")
@Api(description = "应用管理控制层")
public class SilkPayAppController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private SilkPayAppService silkPayAppService;

    @PostMapping("no-auth/getAppItem")
    public MessageRespResult getAppItem() {
        List<AppItem> silkPayApps = this.silkPayAppService.getAppItems();
        return success(silkPayApps);
    }

    @PostMapping("/version/info")
    public MessageRespResult versionInfo() {
        silkPayAppService.versionInfo();
        return success();
    }
}