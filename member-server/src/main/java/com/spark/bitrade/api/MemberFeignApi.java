package com.spark.bitrade.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.constant.FeignServiceConstant;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.MemberVo;
import com.spark.bitrade.entity.PageMemberVo;
import com.spark.bitrade.entity.SilkDataDist;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author: Zhong Jiang
 * @date: 2019-11-21 14:51
 */
@FeignClient(FeignServiceConstant.UC_SERVER)
public interface MemberFeignApi {

    @GetMapping("/uc2/api/v2/member/findInvitationRecord")
    MessageRespResult<PageMemberVo> findInvitationRecord(@RequestHeader("appId") Integer appId,
                                                         @RequestHeader("apiKey") String apiKey,
                                                         @RequestParam("current") Integer current,
                                                         @RequestParam("size") Integer size);


    @GetMapping("/uc2/api/v2/member/listMembersByIds")
    MessageRespResult<List<Member>> listMembersByIds(@RequestBody List<Long> memberIds);



    @GetMapping("/uc2/api/v2/silkDataDist/findOne")
    MessageRespResult<SilkDataDist> findOne(@RequestParam("id") String id, @RequestParam("key") String key);
}