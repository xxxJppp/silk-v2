package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.api.vo.MinerBalanceTransactionsVO;
import com.spark.bitrade.repository.entity.BtBankMinerBalanceTransaction;
import com.spark.bitrade.repository.mapper.BtBankMinerBalanceTransactionMapper;
import com.spark.bitrade.repository.service.BtBankMinerBalanceTransactionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BtBankMinerBalanceTransactionServiceImpl extends ServiceImpl<BtBankMinerBalanceTransactionMapper, BtBankMinerBalanceTransaction> implements BtBankMinerBalanceTransactionService {

    @Override
    public MinerBalanceTransactionsVO getMinerBalanceTransactionsByMemberId(Long memberId, List<Integer> types, int page, int size) {


        QueryWrapper queryWrapper = new QueryWrapper<BtBankMinerBalanceTransaction>()
                .eq("member_id", memberId).orderByDesc("create_time");


        if (types != null && types.size() > 0) {
            queryWrapper.in("type", types);
        }

        Page<BtBankMinerBalanceTransaction> orderPage = new Page<>(page, size);
        IPage<BtBankMinerBalanceTransaction> minerBalancePage = this.baseMapper.selectPage(orderPage, queryWrapper);
        MinerBalanceTransactionsVO minerBalanceTransactionsVO = new MinerBalanceTransactionsVO();


        minerBalanceTransactionsVO.setContent(minerBalancePage.getRecords());
        minerBalanceTransactionsVO.setTotalElements(minerBalancePage.getTotal());

        return minerBalanceTransactionsVO;
    }

    @Override
    public int spendBalanceWithIdAndBalance(Long id, BigDecimal payDecimal) {

        return baseMapper.spendBalanceWithIdAndBalance(id, payDecimal);
    }

    @Override
    public BigDecimal getYestodayMinerBalanceTransactionsSumByMemberId(Long memberId, List types) {
        return baseMapper.getYestodayMinerBalanceTransactionsSumByMemberId(memberId, types);
    }
}
