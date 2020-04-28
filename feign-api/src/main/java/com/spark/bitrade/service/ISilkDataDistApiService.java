package com.spark.bitrade.service;

import com.spark.bitrade.constant.FeignServiceConstant;
import com.spark.bitrade.entity.SilkDataDist;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 提供 系统配置 API服务
 *
 * @author yangch
 * @time 2019-06-22 15:33:36
 */
@FeignClient(FeignServiceConstant.UC_SERVER)
public interface ISilkDataDistApiService {

    /**
     * 查询指定配置ID下所有配置数据接口
     *
     * @param id 配置编号
     * @return 所有数据
     */
    @PostMapping(value = "/uc2/api/v2/silkDataDist/list")
    MessageRespResult<List<SilkDataDist>> list(@RequestParam("id") String id);

    /**
     * 通过主键查询单条数据
     *
     * @param id  配置编号
     * @param key 配置KEY
     * @return 单条数据
     */
    @PostMapping(value = "/uc2/api/v2/silkDataDist/findOne")
    MessageRespResult<SilkDataDist> findOne(@RequestParam("id") String id, @RequestParam("key") String key);
}
