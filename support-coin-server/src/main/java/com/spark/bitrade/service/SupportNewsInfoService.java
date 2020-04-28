package com.spark.bitrade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.SupportNewsInfo;
import com.spark.bitrade.param.NewInfoParam;

/**
 * <p>
 * 扶持上币咨询信息 服务类
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
public interface SupportNewsInfoService extends IService<SupportNewsInfo> {

    /**
     * 查询列表
     * @param memberId
     * @param upCoinId
     * @param seacthParam
     * @return
     */
    IPage<SupportNewsInfo> findListBymemberIdAndupCoinId(Long memberId, Long upCoinId, NewInfoParam seacthParam);


    /**
     * 查询处于是否存在待核实中的资讯
     * @return
     */
    SupportNewsInfo findByAuditStatus(Long memberId, Long upCoinId);

    SupportNewsInfo findByGroupId(String groupId);
}
