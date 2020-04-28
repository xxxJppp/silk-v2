package com.spark.bitrade.biz;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.vo.MemberRecommendCommisionVo;
import com.spark.bitrade.entity.MemberRecommendCommision;
import com.spark.bitrade.param.PageParam;
import com.spark.bitrade.vo.RecommendCommisionVo;

import java.util.List;

/**
 *
 * @author: Zhong Jiang
 * @date: 2019-11-21 9:24
 */
public interface IRecommendCommisionService {
    /**
     * 获取会员推荐佣列表
     *
     * @param memberId
     * @param param     查询参数
     * @return
     */
    IPage<RecommendCommisionVo> findMemberRecommendCommisionsByBuy(Long memberId, PageParam param);


    /**
     * 获取会员奖励 币币交易
     *
     * @param memberId
     * @param param
     * @return
     */
    IPage<MemberRecommendCommision> findMemberRecommendCommisionsByExchange(Long memberId, PageParam param);

    /**
     * 获取已发放的佣金
     *
     * @param memberId
     * @param param
     * @return
     */
    IPage<MemberRecommendCommision> findMemberRecommendCommisionsBySend(Long memberId, PageParam param);

    /**
     * 统计会员奖励
     */
    List<MemberRecommendCommisionVo> countRecommendCommision(Long meberId);


}
