package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.CywWalletWalRecord;
import com.spark.bitrade.entity.dto.WalletSyncCountDto;

/**
 * 机器人账户WAL流水记录表(CywWalletWalRecord)表服务接口
 *
 * @author archx
 * @since 2019-09-02 14:45:22
 */
public interface CywWalletWalRecordService extends IService<CywWalletWalRecord> {

    /**
     * 更新同步流水号
     *
     * @param memberId 会员id
     * @param coinUnit 币种
     * @param syncId   流水号
     * @return affected
     */
    int updateSetSyncId(Long memberId, String coinUnit, Long syncId);

    /**
     * 根据流水统计
     *
     * @param syncId 流水号
     * @return dto
     */
    WalletSyncCountDto sumSyncId(Long syncId);

    /**
     * 转移流水记录
     *
     * @param refId 订单id
     */
    void transfer(String refId);
}