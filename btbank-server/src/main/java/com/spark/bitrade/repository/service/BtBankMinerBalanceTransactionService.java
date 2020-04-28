package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.api.vo.MinerBalanceTransactionsVO;
import com.spark.bitrade.repository.entity.BtBankMinerBalanceTransaction;

import java.math.BigDecimal;
import java.util.List;

public interface BtBankMinerBalanceTransactionService extends IService<BtBankMinerBalanceTransaction> {


    //按用户查询所有
    MinerBalanceTransactionsVO getMinerBalanceTransactionsByMemberId(Long memberId, List<Integer> types, int page, int size);

    int spendBalanceWithIdAndBalance(Long id, BigDecimal payDecimal);

    BigDecimal getYestodayMinerBalanceTransactionsSumByMemberId(Long memberId, List types);
}
