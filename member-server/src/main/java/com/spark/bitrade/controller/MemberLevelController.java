package com.spark.bitrade.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.spark.bitrade.constant.MemberLevelTypeEnum;
import com.spark.bitrade.entity.MemberBenefitsExtends;
import com.spark.bitrade.service.MemberBenefitsExtendsService;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

/**
 * <p>
 * 会员等级 前端控制器
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-18
 */
@RestController
@RequestMapping("api/v2/member/memberLevel")
public class MemberLevelController {

    @Resource
    MemberBenefitsExtendsService memberBenefitsExtendsService;

    /**
     * 内部接口，供otc-server 经纪人USDC优惠购币通道判断是否经纪人使用
     * 判断用户是否是经纪人
     * @param memberId
     * @return
     */
    @PostMapping("isMemberLevel_5")
    public MessageRespResult<Boolean> getMemberLevelByMember(Long memberId){
        MemberBenefitsExtends memberBenefitsExtends = memberBenefitsExtendsService.getOne(new LambdaQueryWrapper<MemberBenefitsExtends>().eq(MemberBenefitsExtends::getMemberId,memberId).ge(MemberBenefitsExtends::getEndTime,new Date()));
        if(memberBenefitsExtends == null){
            return MessageRespResult.success4Data(false);
        }
        //经纪人 levelID = 5
        return MessageRespResult.success4Data(memberBenefitsExtends.getLevelId() == MemberLevelTypeEnum.AGENT.getCode());
    }

}
