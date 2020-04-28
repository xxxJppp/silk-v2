package com.spark.bitrade.biz;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.param.ExchangeOrderParam;
import com.spark.bitrade.vo.ExchangeOrderListVo;
import com.spark.bitrade.vo.ExchangeOrderStaticsVo;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.13 09:28
 */
public interface IExchangeOrderCountService {

    /**
     * 买盘统计
     * @param param
     * @return
     */
    IPage<ExchangeOrderListVo> exchangeOrders(Member member, ExchangeOrderParam param);


    /**
     * 统计总量 卖盘买盘
     * @param memberId
     * @param startTime
     * @param endTime
     * @return
     */
    ExchangeOrderStaticsVo exchangeOrdersCount(Long memberId, String startTime, String endTime);

}
