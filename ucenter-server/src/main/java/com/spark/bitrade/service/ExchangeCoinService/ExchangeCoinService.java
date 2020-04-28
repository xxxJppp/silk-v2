package com.spark.bitrade.service.ExchangeCoinService;


import com.spark.bitrade.entity.ExchangeCoin;

import java.util.List;

/**
 * @description:
 * @author: ss
 * @date: 2020/2/28
 */
public interface ExchangeCoinService {
    /**
     * 获取所有交易对配置信息（展示区域和是否隐藏）
     * @return
     */
    List<ExchangeCoin> getAllSymbol();
}
