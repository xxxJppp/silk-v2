package com.spark.bitrade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.SupportCoinMatch;
import com.spark.bitrade.param.CoinMatchParam;

import java.util.List;

/**
 * <p>
 * 扶持上币交易对  服务类
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
public interface SupportCoinMatchService extends IService<SupportCoinMatch> {

    /**
     * 获取已通过审核的交易对
     *
     * @param upCoinId
     * @return
     */
    List<SupportCoinMatch> findByUpCoinId(Long upCoinId);

    /**
     * 根据 memberId 和 upCoinId 查询交易对列表
     *
     * @param memberId
     * @param upCoinId
     * @return
     */
    IPage<SupportCoinMatch> findByUpCoinIdAndMemberId(Long memberId, Long upCoinId, CoinMatchParam param);


    /**
     * 根据审核状态、memberId、upCoinId 查询
     *
     * @param memberId
     * @param upCoinId
     * @return
     */
    SupportCoinMatch findByAuditStauts(Long memberId, Long upCoinId);

    List<SupportCoinMatch> findByMatch(Long memberId,String match);

    List<String> findByCoinUnit(String coin);
}
