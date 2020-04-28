package com.spark.bitrade.biz.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.biz.IHoldPositionService;
import com.spark.bitrade.entity.SupportUpCoinApply;
import com.spark.bitrade.param.HoldCountParam;
import com.spark.bitrade.service.ExchangeWalletService;
import com.spark.bitrade.service.SupportUpCoinApplyService;
import com.spark.bitrade.vo.HoldPositionVo;
import com.spark.bitrade.vo.MembertVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.07 11:43
 */
@Service
@Slf4j
public class HoldPositionServiceImpl implements IHoldPositionService {

    @Autowired
    private SupportUpCoinApplyService upCoinApplyService;

    @Autowired
    private ExchangeWalletService exchangeWalletService;

    @Override
    public HoldPositionVo countMemberWallet(Long memberId, HoldCountParam param) {
        // 获取币种名称
        HoldPositionVo vo=new HoldPositionVo();
        SupportUpCoinApply apply = upCoinApplyService.findApprovedUpCoinByMember(memberId);
        //持仓用户列表
        IPage<MembertVo> page = exchangeWalletService.findExchangeWalletChicangMembers(memberId,apply.getCoin(),
                param.getBalanceStart(), param.getBalanceEnd(), new Page<>(param.getPage(), param.getPageSize()));
        vo.setPage(page);
        //有效用户
        Integer personCount = upCoinApplyService.validPersonCount(apply.getCoin());
        vo.setEffectiveUserNum(personCount);
        //持仓币数 总币数随 条件改变而改变
        BigDecimal exchangeWalletCount =
                exchangeWalletService.countExchangeWalletByCoinUnit(memberId,apply.getCoin(),param.getBalanceStart(), param.getBalanceEnd());
        vo.setHoldCoinNums(exchangeWalletCount);
        //持仓用户数
        vo.setHoldUserNum(Integer.valueOf(String.valueOf(page.getTotal())));
        //有效用户持仓币数
        BigDecimal HoldCoinCount = this.validHoldCoinCount(apply.getCoin());
        vo.setHoldEffectiveUserNum(HoldCoinCount);
        return vo;
    }

    /**
     * 有效用户持仓币数统计
     * @param coinUnit
     * @return
     */
    @Override
    public BigDecimal validHoldCoinCount(String coinUnit) {
        return upCoinApplyService.validHoldCoinCount(coinUnit);
    }


}
