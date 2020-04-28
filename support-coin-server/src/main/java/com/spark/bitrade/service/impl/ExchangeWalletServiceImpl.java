package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.entity.ExchangeWallet;
import com.spark.bitrade.mapper.ExchangeWalletMapper;
import com.spark.bitrade.service.ExchangeWalletService;
import com.spark.bitrade.vo.MembertVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 用户币币账户 服务实现类
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-26
 */
@Service
public class ExchangeWalletServiceImpl extends ServiceImpl<ExchangeWalletMapper, ExchangeWallet> implements ExchangeWalletService {

    @Resource
    private ExchangeWalletMapper exchangeWalletMapper;



    @Override
    public BigDecimal countExchangeWalletByCoinUnit(Long memberId, String coinUnit,BigDecimal start,BigDecimal end) {
        BigDecimal decimal = exchangeWalletMapper.selectCountByCoinUnit(memberId,coinUnit,start,end);
        return decimal;
    }

    @Override
    public IPage<MembertVo> findExchangeWalletChicangMembers(Long memberId,String coinUnit, BigDecimal start, BigDecimal end, IPage page) {

        List<MembertVo> list=exchangeWalletMapper.findExchangeWalletChicangMembers(memberId,coinUnit,start,end,page);
        page.setRecords(list);
        return page;
    }


}
