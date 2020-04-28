package com.spark.bitrade.service;

import com.spark.bitrade.constant.FeignServiceConstant;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * BT OPEN API 内部调用
 *
 * @author
 * @time
 */
@FeignClient(FeignServiceConstant.C2C_API_SERVER)
public interface IBTOpenApiService {

    /**
     * app-资产查询
     *
     * @param memberId
     * @return com.spark.bitrade.util.MessageRespResult<com.spark.bitrade.vo.IocoPurchaseVo>
     * @author zhangYanjun
     * @time 2019.10.01 16:41
     */
    @PostMapping(value = "/api/app/appAssert")
    MessageRespResult<Object> appAssert(@RequestParam("memberId") Long memberId);


    /**
     * app-查询交易记录
     *
     * @param memberId
     * @param size
     * @param current
     * @return com.spark.bitrade.util.MessageRespResult<java.lang.Object>
     * @author zhangYanjun
     * @time 2019.10.01 16:40
     */
    @PostMapping(value = "/api/app/appOrderPages")
    MessageRespResult<Object> appOrderPages(@RequestParam("current") Integer current, @RequestParam("size") Integer size,
                                            @RequestParam("memberId") Long memberId);

    /**
     * app-查询正在进行的订单
     * @author zhangYanjun
     * @time 2019.10.02 10:19
     * @param current
     * @param size
     * @param memberId
     * @return com.spark.bitrade.util.MessageRespResult<java.lang.Object>
     */
    @PostMapping(value = "/api/app/appGoingOrderPages")
    MessageRespResult<Object> appGoingOrderPages(@RequestParam("current") Integer current, @RequestParam("size") Integer size,
                                            @RequestParam("memberId") Long memberId);

    /**
     * app-资产明细查询
     *
     * @param memberId
     * @param size
     * @param current
     * @return com.spark.bitrade.util.MessageRespResult<java.lang.Object>
     * @author zhangYanjun
     * @time 2019.10.01 16:43
     */
    @PostMapping(value = "/api/app/assertPages")
    MessageRespResult<Object> assertPages(@RequestParam("current") Integer current, @RequestParam("size") Integer size,
                                          @RequestParam("memberId") Long memberId);
}
