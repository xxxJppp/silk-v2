package com.spark.bitrade.mapper;

import com.spark.bitrade.entity.CywWalletWalRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * CywWalArchiveMapper
 *
 * @author Pikachu
 * @since 2019/11/4 16:20
 */
@Mapper
@Repository
public interface CywWalArchiveMapper {

    /**
     * 查询所有已撤单的 trade_type = 4 且已经同步的流水订单Id
     *
     * @param table 表
     * @param date  日期
     * @return list
     */
    @Select("SELECT ref_id FROM ${table} r WHERE r.status = 1 AND r.trade_type = 4 AND r.create_time < #{date} LIMIT 0, 1000")
    List<String> queryRefIdForArchive(@Param("table") String table, @Param("date") Date date);

    /**
     * 根据ref_id查询流水记录
     *
     * @param table 表
     * @param ids   ids
     * @return records
     */
    List<CywWalletWalRecord> findByRefIds(@Param("table") String table, @Param("ids") List<String> ids);

    /**
     * 根据red_id删除流水记录
     *
     * @param table 表
     * @param ids   ids
     * @return affected
     */
    int deleteByRefIds(@Param("table") String table, @Param("ids") List<String> ids);
}
