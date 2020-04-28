package com.spark.bitrade.biz;

import com.spark.bitrade.api.dto.MinerAssetDTO;
import com.spark.bitrade.api.vo.MinerBalanceTransactionsVO;
import com.spark.bitrade.api.vo.MinerBalanceVO;
import com.spark.bitrade.api.vo.MinerOrderTransactionsVO;
import com.spark.bitrade.api.vo.MinerOrdersVO;
import com.spark.bitrade.repository.entity.BtBankMinerOrderTransaction;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author davi
 */
public interface MinerService {
    MinerAssetDTO queryMinerAsset(Long id);

    /**
     * 抢订单
     *
     * @param memberId 用户ID
     * @param orderId  订单ID
     * @return
     */

    BtBankMinerOrderTransaction grabMineOrder(Long memberId, Long orderId);

    /**
     * 查询用户资金明细
     *
     * @param memberId 用户ID
     * @param types    订单类型
     * @param page     分页页码
     * @param size     每页数量
     * @return
     */
    MinerOrdersVO getMinerOrdersByMemberId(Long memberId, List<Integer> types, int page, int size);


    /**
     * 查自己的列表 ，按 1，2 状态 及时间倒序排列
     *
     * @param memberId
     * @param types
     * @param page
     * @param size
     * @return
     */

    MinerOrdersVO getMyMinerOrdersByMemberId(
            Long memberId, List<Integer> types, int page, int size);

    MinerOrdersVO getMinerOrdersByMemberId(Long memberId, int page, int size);

    /**
     * 查询用户订单明细
     *
     * @param types 订单类型 MinerOrerTransactionType
     * @param page  分页页码
     * @param size  每页数量
     * @return
     */
    MinerOrdersVO getMinerOrders(List<Integer> types, int page, int size);

    MinerOrdersVO getMinerOrders(int page, int size);

    /**
     * 按用户查询余额变动记录
     *
     * @param memberId 用户ID
     * @param types    记录类型MinerBalanceTransactionType
     * @param page
     * @param size
     * @return
     */
    MinerBalanceTransactionsVO getMinerBalanceTransactionsByMemberId(Long memberId, List<Integer> types, int page, int size);

    MinerBalanceTransactionsVO getMinerBalanceTransactionsByMemberId(Long memberId, int page, int size);

    void transferAsset(BigDecimal amount, Long memberId);

    MinerBalanceVO getMinerBalance(Long id);

    //查询订单记录


    //按用户查询所有订单记录
    MinerOrderTransactionsVO getMinerOrderTransactionsByMemberId(Long memberId, List<Integer> types, int page, int size);

    MinerOrderTransactionsVO getMinerOrderTransactionsByMemberId(Long memberId, int page, int size);


    int lockMinerBalanceAndAddProcessingReward(Long minerBalanceId, Long memberId, Long minerOrderTransactionId, BigDecimal money, BigDecimal reward, Long orderId);
}
