package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.SuperMemberCommunity;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.vo.CommunityMemberVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author qiliao
 * @since 2019-07-16
 */
public interface SuperMemberCommunityService extends IService<SuperMemberCommunity> {

    /**
     * 查询用户当前加入的社区 并缓存
     * @param memberId
     * @return
     */
    SuperMemberCommunity findMemberCommunity(Long memberId);


    /**
     * 加入社区
     * @param memberId
     * @param communityId
     */
    void joinCommunity(Long memberId, Long communityId);


    /**
     * 退出社区
     * @param memberCommunity
     */
    void exitCommunity(SuperMemberCommunity memberCommunity,boolean isReduce,String coinUnit);


    /**
     * 查询社区成员
     * @param communityId
     * @param pageNo
     * @param pageSize
     * @return
     */
    Page<CommunityMemberVo> findCommunityMembers(Long communityId, int pageNo, int pageSize);


    /**
     * 调用资金的接口
     * @param tradeEntity
     * @return
     */

    boolean traceWallet(WalletTradeEntity tradeEntity);
}
