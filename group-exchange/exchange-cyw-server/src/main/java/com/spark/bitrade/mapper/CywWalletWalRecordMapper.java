package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.entity.CywWalletWalRecord;
import com.spark.bitrade.entity.dto.WalletSyncCountDto;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Repository;

/**
 * 机器人账户WAL流水记录表(CywWalletWalRecord)表数据库访问层
 *
 * @author archx
 * @since 2019-09-02 14:45:22
 */
@Mapper
@Repository
public interface CywWalletWalRecordMapper extends BaseMapper<CywWalletWalRecord> {

    /**
     * 更新同步流水号
     *
     * @param memberId 会员id
     * @param coinUnit 币种
     * @param syncId   流水号
     * @return affected
     */
    @Update("update cyw_wallet_wal_record set sync_id = #{syncId} where sync_id = 0 and status = 0 and member_id = #{memberId} and coin_unit = #{coinUnit}")
    int updateSetSyncId(@Param("memberId") Long memberId, @Param("coinUnit") String coinUnit, @Param("syncId") Long syncId);

    /**
     * 根据流水统计
     *
     * @param syncId 流水号
     * @return dto
     */
    @Results({
            @Result(column = "amount", property = "amount", jdbcType = JdbcType.DECIMAL),
            @Result(column = "increment", property = "increment", jdbcType = JdbcType.DECIMAL),
            @Result(column = "frozen", property = "frozen", jdbcType = JdbcType.DECIMAL),
    })
    @Select("SELECT SUM(r.`trade_balance`) amount, SUM( CASE WHEN r.`trade_balance` > 0 THEN r.`trade_balance` ELSE 0 END ) increment, SUM(r.`trade_frozen`) frozen FROM `cyw_wallet_wal_record` r WHERE r.`sync_id` = #{syncId}")
    WalletSyncCountDto sumSyncId(@Param("syncId") Long syncId);
}