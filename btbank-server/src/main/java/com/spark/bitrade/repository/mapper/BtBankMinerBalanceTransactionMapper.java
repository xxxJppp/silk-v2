package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.BtBankMinerBalanceTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface BtBankMinerBalanceTransactionMapper extends BaseMapper<BtBankMinerBalanceTransaction> {

    @Update("update bt_bank_miner_balance_transaction set balance=balance-#{payDecimal} where id=#{id} and balance >=#{payDecimal}")
    int spendBalanceWithIdAndBalance(@Param("id") Long id, @Param("payDecimal") BigDecimal payDecimal);


    @ResultMap("")
    BigDecimal getYestodayMinerBalanceTransactionsSumByMemberId(@Param("memberId") Long memberId, @Param("types") List types);
}