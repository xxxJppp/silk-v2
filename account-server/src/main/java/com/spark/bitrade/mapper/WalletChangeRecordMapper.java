package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constant.TccStatus;
import com.spark.bitrade.entity.WalletChangeRecord;
import org.apache.ibatis.annotations.Param;

/**
 * 用户钱包资金变更流水记录(WalletChangeRecord)表数据库访问层
 *
 * @author yangch
 * @since 2019-06-15 16:40:21
 */
public interface WalletChangeRecordMapper extends BaseMapper<WalletChangeRecord> {

    /**
     * 更新状态
     *
     * @param memberId             用户ID
     * @param walletChangeRecordId 资金变更流水ID
     * @param oldStatus            更改前的状态
     * @param newStatus            更改后的状态
     * @return
     */
    int updateStatus(@Param("memberId") long memberId,
                     @Param("id") long walletChangeRecordId,
                     @Param("oldStatus") BooleanEnum oldStatus,
                     @Param("newStatus") BooleanEnum newStatus);

    /**
     * 更新tcc状态
     *
     * @param memberId             用户ID
     * @param walletChangeRecordId 资金变更流水ID
     * @param oldStatus            更改前的tcc状态
     * @param newStatus            更改后的tcc状态
     * @return
     */
    int updateTccStatus(@Param("memberId") long memberId,
                        @Param("id") long walletChangeRecordId,
                        @Param("oldStatus") TccStatus oldStatus,
                        @Param("newStatus") TccStatus newStatus);
}