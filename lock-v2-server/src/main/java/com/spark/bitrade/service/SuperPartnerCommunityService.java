package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constant.SuperRegistration;
import com.spark.bitrade.entity.SuperPartnerCommunity;
import com.spark.bitrade.form.SuperPartnerCommunityForm;

/**
 * <p>
 * 合伙人用户关联表 服务类
 * </p>
 *
 * @author qiliao
 * @since 2019-07-16
 */
public interface SuperPartnerCommunityService extends IService<SuperPartnerCommunity> {

    /**
     * 查询根据社区id 并缓存
     *
     * @param id 社区id
     * @return
     */
    SuperPartnerCommunity findById(Long id, BooleanEnum usable);


    /**
     * 查询合伙人的社区 并缓存
     *
     * @param memberId memberId
     * @return
     */
    SuperPartnerCommunity findByMemberId(Long memberId, BooleanEnum usable);

    /**
     * 查询合伙人最近的社区
     *
     * @param memberId
     * @return
     */
    SuperPartnerCommunity findByMemberId(Long memberId);

    /**
     * 创建合伙人
     *
     * @param memberId
     */
    void createSuperPartner(Long memberId, SuperPartnerCommunityForm form);


    /**
     * 退出合伙人
     *
     * @param memberId
     */
    void exitSuperPartner(Long memberId, SuperPartnerCommunity partnerCommunity);

    /**
     * 获取是否主动创建社区
     *
     * @param memberId          会员ID
     * @param usable            是否有效
     * @param superRegistration 来源渠道
     * @return
     */
    SuperPartnerCommunity getSuperPartnerCommunityByMemmberId(Long memberId, BooleanEnum usable, SuperRegistration superRegistration);
}
