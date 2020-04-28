package com.spark.bitrade.biz.impl;

import com.spark.bitrade.api.vo.MinerOrderStatusVO;
import com.spark.bitrade.biz.MinerWebSocketService;
import com.spark.bitrade.repository.entity.BtBankMinerOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author ww
 * @time 2019.10.30 15:08
 */
@Component
public class MinerWebSocketServiceImpl implements MinerWebSocketService {
    @Autowired
    private SimpMessagingTemplate template;

    /**
     * 把修改后的订单新状态发送到sockjs
     */

    @Async
    @Override
    public void sendNewOrderStatusToAllClient(BtBankMinerOrder minerOrder) {

        MinerOrderStatusVO minerOrderStatusVO = new MinerOrderStatusVO();
        if (minerOrder != null) {
            BeanUtils.copyProperties(minerOrder, minerOrderStatusVO);


            template.convertAndSend("/miner/order/status/changed", minerOrderStatusVO);

            //MinerWebSocket.sendMessageToAllWebSocket(MessageRespResult.getSuccessInstance(BtBankMsgCode.WEBSOCKET_ORDER_STATUS_CHANGED.getMessage(), minerOrderStatusVO).toString());
        }
    }

    @Override
    public void sendMessageToSingleClient(String msg, String subject) {
        template.convertAndSend(subject, msg);
    }

}
