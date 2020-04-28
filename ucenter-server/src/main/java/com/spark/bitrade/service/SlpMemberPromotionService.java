package com.spark.bitrade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.PromotionMemberDTO;
import com.spark.bitrade.entity.SlpMemberPromotion;
import com.spark.bitrade.vo.PromotionMemberVO;

/**
 * 会员推荐关系表(SlpMemberPromotion)表服务接口
 *
 * @author wsy
 * @since 2019-06-20 10:02:09
 */
public interface SlpMemberPromotionService extends IService<SlpMemberPromotion> {

    int findPromotionCount(Long memberId);

    Page<PromotionMemberDTO> findPromotionList(Long memberId, int pageSize, int pageNo);

    boolean queryRecipt(Long memberId,Long cMemberId);

    void updateTotal(Long memberId);

    /**
     * 获取直推部门
     *
     * @param memberId 会员ID
     * @param current   页数
     * @param size 条数
     * @return 直推部门
     */
    IPage<SlpMemberPromotion> findPromotionMember(Integer current, Integer size, Long memberId);

}