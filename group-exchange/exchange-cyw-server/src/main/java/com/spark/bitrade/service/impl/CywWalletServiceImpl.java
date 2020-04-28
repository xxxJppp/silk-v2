package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constants.ExchangeCywMsgCode;
import com.spark.bitrade.entity.CywWallet;
import com.spark.bitrade.entity.constants.CywLockStatus;
import com.spark.bitrade.mapper.CywWalletMapper;
import com.spark.bitrade.service.CywWalletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Optional;

/**
 * 机器人钱包(CywWallet)表服务实现类
 *
 * @author archx
 * @since 2019-09-02 14:42:41
 */
@Service("cywWalletService")
public class CywWalletServiceImpl extends ServiceImpl<CywWalletMapper, CywWallet> implements CywWalletService {

    @Override
    public Optional<CywWallet> findOne(Long memberId, String coinUnit) {
        String pk = memberId + ":" + coinUnit;
        return Optional.ofNullable(getById(pk));
    }

    @Transactional
    @Override
    public boolean create(Long memberId, String coinUnit) {
        String pk = memberId + ":" + coinUnit;

        CywWallet wallet = new CywWallet();

        wallet.setId(pk);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setFrozenBalance(BigDecimal.ZERO);
        wallet.setMemberId(memberId);
        wallet.setCoinUnit(coinUnit);
        wallet.setIsLock(CywLockStatus.UNLOCK);
        wallet.setCreateTime(Calendar.getInstance().getTime());

        return save(wallet);
    }

    @Transactional
    @Override
    public boolean sync(Long memberId, String coinUnit, BigDecimal balance, BigDecimal frozenBalance) {

        String pk = memberId + ":" + coinUnit;

        // 没有则创建
        if (!findOne(memberId, coinUnit).isPresent()) {
            if (!create(memberId, coinUnit)) {
                throw ExchangeCywMsgCode.CREATE_WALLET_FAILED.asException();
            }
        }

        return baseMapper.sync(pk, balance, frozenBalance) > 0;
    }
}