package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.api.dto.UnlockDTO;
import com.spark.bitrade.api.vo.MinerOrderTransactionsVO;
import com.spark.bitrade.repository.entity.BtBankMinerOrderTransaction;

import java.util.Date;
import java.util.List;

public interface BtBankMinerOrderTransactionService extends IService<BtBankMinerOrderTransaction> {

    //查询订单记录
    MinerOrderTransactionsVO getMinerOrderTransactionsByMemberId(Long memberId, List<Integer> types, int page, int size);

    /**
     * @param memberId
     * @param types
     * @param page
     * @param size
     * @return
     */

    /**
     * 可解锁记录
     *
     * @param time
     * @return true
     * @author shenzucai
     * @time 2019.10.24 22:42
     */
    List<UnlockDTO> listUnlockRecords(Date time);


    int insertGrabOrDepatchOrder(BtBankMinerOrderTransaction minerOrderTransaction, Integer timeSpan);
}
