package com.spark.bitrade.biz;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.entity.MemberBenefitsExtends;
import com.spark.bitrade.entity.MemberBenefitsOrder;
import com.spark.bitrade.form.BenefitsOrderForm;
import com.spark.bitrade.param.PageParam;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.vo.CurrentAmountVo;
import com.spark.bitrade.vo.MemberBenefitsExtendsVo;

import java.text.ParseException;
import java.util.List;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.20 10:30
 */
public interface IBenefitsOrderService {

    /**
     * 开通会员等级，创建开通订单
     *
     * @param fitsOrderForm
     * @[param memberId
     */
    void saveMemberVip(Long memberId, BenefitsOrderForm fitsOrderForm) throws ParseException;

    /**
     * 获取当前用户vip等级
     * @param memberId
     * @return
     */
    MemberBenefitsExtendsVo getCurrentMemberVip(Long memberId);

    /**
     * 获取历史订单
     *
     * @param memberId
     */
    IPage<MemberBenefitsOrder> getMemberBenefitsOrderHistory(Long memberId, PageParam param) throws ParseException;

    /**
     * 奖励页面备注翻译
     * @param orederNumber
     * @return
     */
    String confirmRemarks(String orederNumber);

    /**
     * 年终活动赠送 VIP1
     * @param memberId
     */
    Integer giveMemberVip(Long memberId, Integer appId) throws ParseException;

}
