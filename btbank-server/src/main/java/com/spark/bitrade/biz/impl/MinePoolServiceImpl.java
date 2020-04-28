package com.spark.bitrade.biz.impl;

import com.spark.bitrade.api.vo.OrderReceiverVO;
import com.spark.bitrade.biz.MinePoolService;
import com.spark.bitrade.biz.MinerWebSocketService;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.BtBankMinerOrder;
import com.spark.bitrade.repository.service.BtBankMinerOrderService;
import com.spark.bitrade.service.BtBankConfigService;
import com.spark.bitrade.util.IdWorkByTwitter;
import com.spark.bitrade.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author davi
 */
@Slf4j
@Service
public class MinePoolServiceImpl implements MinePoolService {
    @Autowired
    private IdWorkByTwitter idWorkByTwitter;
    @Autowired
    private BtBankMinerOrderService minerOrderService;
    @Autowired
    private BtBankConfigService configService;
    @Autowired
    private RedisTemplate redisTemplate;


    @Autowired
    MinerWebSocketService minerWebSocketService;

    @Value("${dmz.cipher:*20191021Pay#}")
    private String cipher;

    @Override
    public void receiveOrder(OrderReceiverVO vo) {
        this.checkReceiverSwitch();

        this.authenticSignature(vo);

        BtBankMinerOrder order = new BtBankMinerOrder();
        order.setId(idWorkByTwitter.nextId());
        order.setCreateTime(new Date());
        order.setStatus(0);
        order.setUpstreamOrderId(vo.getOrderSn());
        BigDecimal money = new BigDecimal(vo.getTotalPrice());
        order.setMoney(money);
        if (!minerOrderService.save(order)) {
            log.error("insert new order error:{}", order);
            throw new BtBankException(BtBankMsgCode.ORDER_RECEIVED_ABNORMAL);
        }
        minerWebSocketService.sendNewOrderStatusToAllClient(order);
    }

    private void authenticSignature(OrderReceiverVO vo) {
        String format = String.format("%s%s%s%s", cipher, vo.getTotalPrice(), vo.getOrderSn(), cipher);
        String md5Encode = MD5Util.md5Encode(format).toLowerCase();
        if (!md5Encode.equalsIgnoreCase(vo.getSign())) {
            log.error("receiver invalid order:{}", vo);
            throw new BtBankException(CommonMsgCode.INVALID_REQUEST_METHOD);
        }
    }

    private void checkReceiverSwitch() {
        String isReceiverOrder = (String) configService.getConfig("RECEIVING_ORDER_SWITCH");
        if (isReceiverOrder == null || !isReceiverOrder.equalsIgnoreCase("1")) {
            throw new BtBankException(CommonMsgCode.SERVICE_UNAVAILABLE);
        }
    }
}
