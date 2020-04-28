package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.api.vo.MinerOrderVO;
import com.spark.bitrade.api.vo.MinerOrdersVO;
import com.spark.bitrade.repository.entity.BtBankMinerOrder;
import com.spark.bitrade.repository.mapper.BtBankMinerOrderMapper;
import com.spark.bitrade.repository.service.BtBankMinerOrderService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BtBankMinerOrderServiceImpl extends ServiceImpl<BtBankMinerOrderMapper, BtBankMinerOrder> implements BtBankMinerOrderService {

    @Override
    public MinerOrdersVO getMinerOrdersByMemberIdOrderByProcessCreateTime(Long memberId, List<Integer> types, int page, int size) {


        Page<BtBankMinerOrder> orderPage = new Page<>(page, size);
        IPage<MinerOrderVO> minerOrders = this.baseMapper.queryMinerOrderOrderByProcessStatusAndCreateTime(orderPage, memberId, types);

//        Long count = baseMapper.queryMinerOrderOrderByProcessStatusAndCreateTimeCount(memberId);
//        if (count == null) count = 0L;
        MinerOrdersVO minerOrdersVO = new MinerOrdersVO();
        minerOrdersVO.setContent(minerOrders.getRecords());
        minerOrdersVO.setTotalElements(minerOrders.getTotal());


        return minerOrdersVO;
    }

    @Override
    public MinerOrdersVO getMinerOrdersByMemberId(Long memberId, List<Integer> types, int page, int size) {


        QueryWrapper queryWrapper = new QueryWrapper<BtBankMinerOrder>()
                .eq("member_id", memberId);

        //queryWrapper.orderByAsc("status");
        queryWrapper.orderByDesc("process_time");
        queryWrapper.orderByDesc("money");

        if (types != null && types.size() > 0) {
            queryWrapper.in("status", types);
        }


        Page<BtBankMinerOrder> orderPage = new Page<>(page, size);
        IPage<BtBankMinerOrder> minerOrderIPage = this.baseMapper.selectPage(orderPage, queryWrapper);


        MinerOrdersVO minerOrdersVO = new MinerOrdersVO();
        minerOrdersVO.setContent(minerOrderIPage.getRecords());
        minerOrdersVO.setTotalElements(minerOrderIPage.getTotal());


        return minerOrdersVO;
    }

    @Override
    public MinerOrdersVO getMinerOrders(List<Integer> types, int page, int size) {


        IPage<BtBankMinerOrder> minerOrderIPage = baseMapper.queryOrdersListOrderByStatusAndCreateTime(new Page<>(page, size), types);


//        QueryWrapper queryWrapper = new QueryWrapper<BtBankMinerOrder>();
//
//        queryWrapper.orderByAsc("status");
//
//
//        if (types != null && (
//                types.contains(MinerOrderTransactionType.SECKILLED_ORDER)
//                        || types.contains(MinerOrderTransactionType.DISPATCHED_ORDER)
//        )) {
//            queryWrapper.orderByAsc("create_time");
//        } else {
//
//            queryWrapper.orderByDesc("create_time");
//        }
//
//        queryWrapper.orderByAsc("money");
//        queryWrapper.orderByDesc("process_time");
//
//        Page<BtBankMinerOrder> orderPage = new Page<>(page, size);
//        if (types != null && types.size() > 0) {
//            queryWrapper.in("type", types);
//        }
//        IPage<BtBankMinerOrder> minerOrderIPage = this.baseMapper.selectPage(orderPage, queryWrapper);

        MinerOrdersVO minerOrdersVO = new MinerOrdersVO();
        minerOrdersVO.setContent(minerOrderIPage.getRecords());
        minerOrdersVO.setTotalElements(minerOrderIPage.getTotal());


        return minerOrdersVO;
    }

    /**
     * 查找符合派单的订单
     *
     * @param dispatchTime
     * @return true
     * @author shenzucai
     * @time 2019.10.24 20:28
     */
    @Override
    public List<BtBankMinerOrder> listDispatchOrder(Long dispatchTime) {
        return baseMapper.listDispatchOrder(dispatchTime);
    }

    @Override
    public int grabMinerOrderByIdWithStatus(BtBankMinerOrder order, int status) {
        return
                baseMapper.grabMinerOrderByIdWithStatus(order, status);
    }
}
