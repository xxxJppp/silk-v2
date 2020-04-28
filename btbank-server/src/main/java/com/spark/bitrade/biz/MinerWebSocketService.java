package com.spark.bitrade.biz;

import com.spark.bitrade.repository.entity.BtBankMinerOrder;

/**
 * @author ww
 * @time 2019.10.30 15:07
 */
public interface MinerWebSocketService {
    //
    void sendNewOrderStatusToAllClient(BtBankMinerOrder minerOrder);


    void sendMessageToSingleClient(String msg, String subject);
}
