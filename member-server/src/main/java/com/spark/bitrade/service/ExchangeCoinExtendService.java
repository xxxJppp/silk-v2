package com.spark.bitrade.service;

import com.spark.bitrade.entity.ExchangeCoinExtend;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 币币交易-交易对扩展表 服务类
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-18
 */
public interface ExchangeCoinExtendService extends IService<ExchangeCoinExtend> {

    ExchangeCoinExtend getOneExchangeCoinExtend(String symbol);

}
