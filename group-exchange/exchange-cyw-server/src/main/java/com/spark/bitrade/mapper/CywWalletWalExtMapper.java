package com.spark.bitrade.mapper;

import com.spark.bitrade.entity.CywWalletWalRecord;
import com.spark.bitrade.mapper.dto.WalRecordDto;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * CywWalletWalExtMapper
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/26 14:24
 */
@Mapper
@Repository
public interface CywWalletWalExtMapper {

    @Results({
            @Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT),
            @Result(column = "ref_id", property = "refId", jdbcType = JdbcType.VARCHAR),
            @Result(column = "trade_type", property = "tradeType", jdbcType = JdbcType.INTEGER),
            @Result(column = "trade_balance", property = "tradeBalance", jdbcType = JdbcType.DECIMAL),
            @Result(column = "trade_frozen", property = "tradeFrozen", jdbcType = JdbcType.DECIMAL),
    })
    @Select("SELECT id, ref_id, trade_type, trade_balance,trade_frozen FROM cyw_wallet_wal_record r WHERE r.status = 1 AND r.ref_id = #{refId}")
    List<WalRecordDto> queryByRefId(@Param("refId") String refId);

    /**
     * 查询所有已撤单的 trade_type = 4 且已经同步的流水
     *
     * @param table 表
     * @param date 日期
     * @return list
     */
    @Results({
            @Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT),
            @Result(column = "ref_id", property = "refId", jdbcType = JdbcType.VARCHAR),
    })
    @Select("SELECT id, ref_id FROM ${table} r WHERE r.status = 1 AND r.trade_type = 4 AND r.create_time < #{date} LIMIT 0, 1000")
    List<WalRecordDto> queryForArchive(@Param("table") String table, @Param("date") Date date);

    @Delete("delete from cyw_wallet_wal_record where ref_id = #{refId}")
    int removeByRefId(@Param("refId") String refId);

    /**
     * 批量写入
     *
     * @param table   表名
     * @param records 记录
     * @return int
     */
    int saveBatch(@Param("table") String table, @Param("records") List<CywWalletWalRecord> records);
}
