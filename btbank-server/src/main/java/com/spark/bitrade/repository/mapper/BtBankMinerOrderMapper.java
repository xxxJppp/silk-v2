package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.api.vo.MinerOrderVO;
import com.spark.bitrade.repository.entity.BtBankMinerOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface BtBankMinerOrderMapper extends BaseMapper<BtBankMinerOrder> {

    /**
     * 查找符合派单的订单
     *
     * @param dispatchTime
     * @return true
     * @author shenzucai
     * @time 2019.10.24 20:28
     */
    List<BtBankMinerOrder> listDispatchOrder(@Param("dispatchTime") Long dispatchTime);


    @Update("update bt_bank_miner_order set status=#{order.status},member_id=#{order.memberId},process_time=#{order.processTime} where id = #{order.id} and status=#{oldStatus}")
    int grabMinerOrderByIdWithStatus(@Param("order") BtBankMinerOrder order, @Param("oldStatus") int status);

    List<BtBankMinerOrder> listSecKillOrder(Page page);


    //@Select("SELECT * FROM bt_bank_miner_order LEFT JOIN bt_bank_miner_order_transaction AS tran ON tran.miner_order_id = bt_bank_miner_order.id  where tran.type IN (1, 2) and bt_bank_miner_order.member_id=#{memberId}  ORDER BY tran.create_time DESC")
    IPage<MinerOrderVO> queryMinerOrderOrderByProcessStatusAndCreateTime(Page page, @Param("memberId") Long memberId, @Param("types") List<Integer> types);

    Long queryMinerOrderOrderByProcessStatusAndCreateTimeCount(Long memberId);

    IPage<BtBankMinerOrder> queryOrdersListOrderByStatusAndCreateTime(Page page, @Param("types") List<Integer> types);
}