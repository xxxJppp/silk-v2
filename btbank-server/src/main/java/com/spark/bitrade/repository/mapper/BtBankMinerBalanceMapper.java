package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.BtBankMinerBalance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

@Mapper
public interface BtBankMinerBalanceMapper extends BaseMapper<BtBankMinerBalance> {
    BtBankMinerBalance findFirstByMemberId(@Param("memberId") Long memberId);

    int updateIncBalanceAmount(@Param("memberId") Long memberId, @Param("addedBalanceAmount") BigDecimal addedBalanceAmount);

    /**
     * 自动派单匹配矿工
     *
     * @param amount
     * @return true
     * @author shenzucai
     * @time 2019.10.24 20:16
     */
    BtBankMinerBalance dispatchMiner(@Param("amount") BigDecimal amount);


    /**
     * 抢单扣款
     *
     * @param money
     * @param reward
     * @return
     */

    @Update("update bt_bank_miner_balance set balance_amount = balance_amount-#{money},lock_amount=lock_amount+#{money},processing_reward_sum = processing_reward_sum+#{reward},update_time=now() where id=#{minerBalanceId} and  balance_amount>#{money}")
    int grabSuccAndUpdate(@Param("minerBalanceId") Long minerBalanceId, @Param("money") BigDecimal money, @Param("reward") BigDecimal reward);
}