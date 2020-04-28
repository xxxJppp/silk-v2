package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.entity.CywWalletSyncRecord;
import com.spark.bitrade.entity.dto.WalletSyncCountDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * 账户WAL流水同步记录表(CywWalletSyncRecord)表数据库访问层
 *
 * @author archx
 * @since 2019-09-02 14:44:18
 */
@Mapper
@Repository
public interface CywWalletSyncRecordMapper extends BaseMapper<CywWalletSyncRecord> {

    /**
     * 获取最新一条记录
     *
     * @param memberId 会员ID
     * @param coinUnit 币种
     * @return
     */
    CywWalletSyncRecord getNewest(@Param("memberId") Long memberId, @Param("coinUnit") String coinUnit);

    /**
     * 根据时间段汇总数据
     *
     * @param memberId
     * @param coinUnit
     * @param startTime
     * @param endTime
     * @return
     */
    WalletSyncCountDto sum(@Param("memberId") Long memberId,
                           @Param("coinUnit") String coinUnit,
                           @Param("startTime") Date startTime,
                           @Param("endTime") Date endTime);
}