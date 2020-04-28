package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.api.vo.MinerOrdersVO;
import com.spark.bitrade.repository.entity.BtBankMinerOrder;

import java.util.List;


public interface BtBankMinerOrderService extends IService<BtBankMinerOrder> {

    MinerOrdersVO getMinerOrdersByMemberIdOrderByProcessCreateTime(Long memberId, List<Integer> types, int page, int size);

    //按用户和状态 查询所有的订单
    MinerOrdersVO getMinerOrdersByMemberId(Long memberId, List<Integer> types, int page, int size);

    /**
     * 按状态 查询所有的订单
     *
     * @param types
     * @param page
     * @param size
     * @return
     */
    MinerOrdersVO getMinerOrders(List<Integer> types, int page, int size);

    /**
     * 查找符合派单的订单
     *
     * @param dispatchTime
     * @return true
     * @author shenzucai
     * @time 2019.10.24 20:28
     */
    List<BtBankMinerOrder> listDispatchOrder(Long dispatchTime);


    /**
     * 抢单并按老的状态修改新的状态
     *
     * @param order  新的order
     * @param status 旧的状态
     * @return
     */
    int grabMinerOrderByIdWithStatus(BtBankMinerOrder order, int status);

}
