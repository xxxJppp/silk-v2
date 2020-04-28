package com.spark.bitrade.service;

import com.spark.bitrade.constant.FeignServiceConstant;
import com.spark.bitrade.entity.IocoMemberWallet;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.vo.IocoPurchaseVo;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * @author shenzucai
 * @time 2019.07.03 21:38
 */
@FeignClient(FeignServiceConstant.SLP_SERVER)
public interface IiocoService {

    @PostMapping(value = "/lock-slp/iocoMemberTransaction/purchaseIndex")
    MessageRespResult<IocoPurchaseVo> getPurchaseData(@RequestParam("memberId") Long memberId);


    @PostMapping(value = "/lock-slp/iocoMemberTransaction/giftIndex")
    MessageRespResult<IocoMemberWallet> giftIndex(@RequestParam("memberId") Long memberId);

    @PostMapping(value = "/lock-slp/iocoMemberTransaction/purchaseSLP")
    MessageRespResult<IocoPurchaseVo> purchaseSLP(@RequestParam("memberId") Long memberId, @RequestParam("purchasetUnit") String purchasetUnit
            , @RequestParam("purchaseAmount") BigDecimal purchaseAmount
            , @RequestParam("slpAmount") BigDecimal slpAmount
            , @RequestParam("share") Integer share
            , @RequestParam("activityId") Long activityId);

    @PostMapping(value = "/lock-slp/iocoMemberTransaction/giftSLP")
    MessageRespResult<IocoPurchaseVo> giftSLP(@RequestParam("memberId") Long memberId, @RequestParam("giftUnit") String giftUnit
            , @RequestParam("giftAmount") BigDecimal giftAmount
            , @RequestParam("giftTo") String giftTo);

    @PostMapping(value = "/lock-slp/iocoMemberTransaction/purchaseSLP")
    MessageRespResult<Object> listByType(@RequestParam("memberId") Long memberId
            , @RequestParam("size") Integer size
            , @RequestParam("current") Integer current
            , @RequestParam("type") Integer type);
}
