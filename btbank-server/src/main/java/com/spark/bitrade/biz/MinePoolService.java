package com.spark.bitrade.biz;

import com.spark.bitrade.api.vo.OrderReceiverVO;

/**
 * @author davi
 */
public interface MinePoolService {
    void receiveOrder(OrderReceiverVO vo);
}
