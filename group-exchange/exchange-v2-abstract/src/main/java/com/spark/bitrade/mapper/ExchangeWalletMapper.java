package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.api.vo.ExchangeWalletVo;
import com.spark.bitrade.api.vo.WalletQueryVo;
import com.spark.bitrade.entity.ExchangeWallet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 机器人钱包(ExchangeWallet)表数据库访问层
 *
 * @author archx
 * @since 2019-09-02 14:42:41
 */
@Mapper
@Repository
public interface ExchangeWalletMapper extends BaseMapper<ExchangeWallet> {

    @Update("update exchange_wallet set balance = balance + #{balance}, frozen_balance = frozen_balance + #{frozen}, update_time = now() where id = #{id}")
    int sync(@Param("id") String id, @Param("balance") BigDecimal balance, @Param("frozen") BigDecimal frozenBalance);

    @Update("update exchange_wallet set balance = balance - #{amount}, frozen_balance = frozen_balance + #{frozen}, update_time = now() where id = #{id} and balance >= #{amount}")
    int freeze(@Param("id") String id, @Param("amount") BigDecimal amount, @Param("frozen") BigDecimal frozen);

    @Update("update exchange_wallet set balance = #{balance}, frozen_balance = #{frozen}, update_time = now() where id = #{id}")
    int reset(@Param("id") String id, @Param("balance") BigDecimal balance, @Param("frozen") BigDecimal frozenBalance);

    @Update("update exchange_wallet set signature = #{sign}, update_time = now() where id = #{id}")
    int signature(@Param("id") String id, @Param("sign") String sign);

    List<ExchangeWalletVo> findList(IPage<ExchangeWalletVo> page, @Param("qo") WalletQueryVo qo);
}