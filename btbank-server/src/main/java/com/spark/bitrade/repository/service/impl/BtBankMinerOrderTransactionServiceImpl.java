package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.api.dto.UnlockDTO;
import com.spark.bitrade.api.vo.MinerOrderTransactionsVO;
import com.spark.bitrade.repository.entity.BtBankMinerOrderTransaction;
import com.spark.bitrade.repository.mapper.BtBankMinerOrderTransactionMapper;
import com.spark.bitrade.repository.service.BtBankMinerOrderTransactionService;
import com.spark.bitrade.util.DateUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class BtBankMinerOrderTransactionServiceImpl extends ServiceImpl<BtBankMinerOrderTransactionMapper, BtBankMinerOrderTransaction> implements BtBankMinerOrderTransactionService {

    @Override
    public MinerOrderTransactionsVO getMinerOrderTransactionsByMemberId(Long memberId, List<Integer> types, int page, int size) {
        QueryWrapper queryWrapper = new QueryWrapper<BtBankMinerOrderTransaction>()
                .eq("member_id", memberId);
        queryWrapper.orderByDesc("create_time");

        if (types != null && types.size() > 0) {
            queryWrapper.in("type", Arrays.asList(types));
        }


        Page<BtBankMinerOrderTransaction> orderPage = new Page<>(page, size);
        IPage<BtBankMinerOrderTransaction> minerBalancePage = this.baseMapper.selectPage(orderPage, queryWrapper);

        MinerOrderTransactionsVO minerOrderTransactionsVO = new MinerOrderTransactionsVO();
        minerOrderTransactionsVO.setContent(minerBalancePage.getRecords());
        minerOrderTransactionsVO.setTotalElements(minerBalancePage.getTotal());

        return minerOrderTransactionsVO;
    }

    /**
     * 可解锁记录
     *
     * @param time
     * @return true
     * @author shenzucai
     * @time 2019.10.24 22:42
     */
    @Override
    public List<UnlockDTO> listUnlockRecords(Date time) {
        time = DateUtil.addDay(time, 1);
        List<UnlockDTO> unlockDTOS = baseMapper.listUnlockRecords(time);
        List<UnlockDTO> unlockDTOAll = new ArrayList<>(unlockDTOS);
        List<UnlockDTO> unlockDTOSCopy = baseMapper.listUnlockRecordsCopy(time);
        unlockDTOAll.addAll(unlockDTOSCopy);

        unlockDTOAll.sort(BtBankMinerOrderTransactionServiceImpl::sortedByDatetime);

        return unlockDTOAll;
    }

    @Override
    public int insertGrabOrDepatchOrder(BtBankMinerOrderTransaction minerOrderTransaction, Integer timeSpan) {
        return baseMapper.insertGrabOrDepatchOrder(minerOrderTransaction, timeSpan);
    }

    private static int sortedByDatetime(UnlockDTO dto, UnlockDTO t1) {
        int seconds = DateUtil.compareDateSec(dto.getCreateTime(), t1.getCreateTime());
        return Integer.compare(0, seconds);
    }
}
