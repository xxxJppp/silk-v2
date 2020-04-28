package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.BtBankMinerBalance;

import java.math.BigDecimal;

public interface BtBankMinerBalanceService extends IService<BtBankMinerBalance> {

    BtBankMinerBalance findFirstByMemberId(Long memberId);

    int updateIncBalanceAmount(Long memberId, BigDecimal addedBalanceAmount);

    /**
     * 自动派单匹配矿工
     *
     * @param amount
     * @return true
     * @author shenzucai
     * @time 2019.10.24 20:16
     */
    BtBankMinerBalance dispatchMiner(BigDecimal amount);

    int grabSuccAndUpdate(Long minerBalanceId, BigDecimal money, BigDecimal reward);
}
