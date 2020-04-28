package com.spark.bitrade.controller;

import com.spark.bitrade.filter.UpdatingFilter;
import com.spark.bitrade.service.UpdatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author young
 * @time 2019.10.30 16:39
 */
@RestController
@RequestMapping("/gateway")
public class UpdatingCtrl {
    @Autowired
    private UpdatingFilter updatingFilter;
    @Autowired
    private UpdatingService updatingService;

    @RequestMapping("/stat")
    @ResponseBody
    public String isUpdating() {
        if (updatingService.isUpdating()) {
            updatingFilter.setIsUpdating(true);
            return "{\"flag\":1}";
        } else {
            updatingFilter.setIsUpdating(false);
            return "{\"flag\":0}";
        }
    }

    /**
     * /gateway/nowStat?flag=0/1
     *
     * @param flag
     * @return
     */
    @RequestMapping("/nowStat")
    @ResponseBody
    public String flushUpdatingStatus(Integer flag) {
        if (updatingService.flushUpdatingStatus(flag)) {
            updatingFilter.setIsUpdating(true);
            return "{\"flag\":1}";
        } else {
            updatingFilter.setIsUpdating(false);
            return "{\"flag\":0}";
        }
    }

    @RequestMapping(value = "/api", produces = "application/json")
    @ResponseBody
    public String isUpdatingApi() {
        if (updatingService.isUpdating()) {
            return "{\"data\":\"\",\"code\":999,\"message\":\"系统正在升级...\"}";
        } else {
            return "{\"data\":\"\",\"code\":999,\"message\":\"系统升级接口...\"}";
        }
    }
}
