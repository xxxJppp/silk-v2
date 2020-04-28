package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.spark.bitrade.dto.request.ExchangeReleaseWalletDTO;
import com.spark.bitrade.entity.ExchangeReleaseWallet;
import com.spark.bitrade.mapper.ExchangeReleaseWalletMapper;
import com.spark.bitrade.service.ExchangeReleaseWalletService;
import com.spark.bitrade.uitl.WalletUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * 币交易释放-锁仓释放总数表(ExchangeReleaseWallet)表服务实现类
 *
 * @author yangch
 * @since 2019-12-16 14:52:16
 */
@Service("exchangeReleaseWalletService")
public class ExchangeReleaseWalletServiceImpl
        extends ServiceImpl<ExchangeReleaseWalletMapper, ExchangeReleaseWallet> implements ExchangeReleaseWalletService {


    @Override
    public boolean updateExchangeReleaseWalletRecord(ExchangeReleaseWalletDTO requestDTO) {
        //.selectOne(new QueryWrapper<ExchangeReleaseWallet>().eq(ExchangeReleaseWallet.ID, requestDTO.getId())
        ExchangeReleaseWallet exchangeReleaseWallet = this.baseMapper.selectById(requestDTO.getId());
        if (exchangeReleaseWallet == null) {
            return SqlHelper.retBool(this.baseMapper.addExchangeWalletRecord(requestDTO.getId(),requestDTO.getMemberId(),requestDTO.getCoinSymbol(),requestDTO.getLockAmount()));
        }
            return SqlHelper.retBool(this.baseMapper.updateExchangeWalletRecord(requestDTO.getId(),requestDTO.getLockAmount()));
    }

    @Override
    public Optional<ExchangeReleaseWallet> find(long memberId, String coinSymbol) {
        return Optional.ofNullable(this.baseMapper.selectById(this.getPk(memberId, coinSymbol)));
    }

    @Override
    public Boolean decreaseLockAmount(long memberId, String coinSymbol, BigDecimal releaseAmount) {
        return SqlHelper.retBool(this.baseMapper.decreaseLockAmount(this.getPk(memberId, coinSymbol), WalletUtils.positiveOf(releaseAmount)));
    }

    private String getPk(long memberId, String coinSymbol) {
        return new StringBuilder().append(memberId).append(":").append(coinSymbol).toString();
    }
}