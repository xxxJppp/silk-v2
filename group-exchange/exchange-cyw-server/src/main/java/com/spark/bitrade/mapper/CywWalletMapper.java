package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.entity.CywWallet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

/**
 * 机器人钱包(CywWallet)表数据库访问层
 *
 * @author archx
 * @since 2019-09-02 14:42:41
 */
@Mapper
@Repository
public interface CywWalletMapper extends BaseMapper<CywWallet> {

    @Update("update cyw_wallet set balance = balance + #{balance}, frozen_balance = frozen_balance + #{frozen}, update_time = now() where id = #{id}")
    int sync(@Param("id") String id, @Param("balance")BigDecimal balance, @Param("frozen") BigDecimal frozenBalance);
}