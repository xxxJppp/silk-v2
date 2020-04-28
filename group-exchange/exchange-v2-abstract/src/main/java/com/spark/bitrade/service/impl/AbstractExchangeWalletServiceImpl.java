package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.api.vo.ExchangeWalletVo;
import com.spark.bitrade.api.vo.WalletQueryVo;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.constants.ExchangeOrderMsgCode;
import com.spark.bitrade.dsc.DscValidateHelper;
import com.spark.bitrade.entity.ExchangeWallet;
import com.spark.bitrade.entity.constants.ExchangeLockStatus;
import com.spark.bitrade.lock.Callback;
import com.spark.bitrade.lock.DistributedLockTemplate;
import com.spark.bitrade.mapper.ExchangeWalletMapper;
import com.spark.bitrade.service.ExchangeWalletResetRecordService;
import com.spark.bitrade.service.ExchangeWalletService;
import com.spark.bitrade.util.ExceptionUitl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Optional;

/**
 * 机器人钱包(ExchangeWallet)表服务实现类
 *
 * @author archx
 * @since 2019-09-02 14:42:41
 */
@Slf4j
public abstract class AbstractExchangeWalletServiceImpl
        extends ServiceImpl<ExchangeWalletMapper, ExchangeWallet> implements ExchangeWalletService {

    protected DistributedLockTemplate lockTemplate;
    protected DscValidateHelper validateHelper;
    protected ExchangeWalletResetRecordService resetRecordService;

    @Autowired
    public void setLockTemplate(DistributedLockTemplate lockTemplate) {
        this.lockTemplate = lockTemplate;
    }

    @Autowired
    public void setValidateHelper(DscValidateHelper validateHelper) {
        this.validateHelper = validateHelper;
    }

    @Autowired
    public void setResetRecordService(ExchangeWalletResetRecordService resetRecordService) {
        this.resetRecordService = resetRecordService;
    }

    @Override
    public Optional<ExchangeWallet> findOne(Long memberId, String coinUnit) {
        return Optional.ofNullable(getById(getWalletPk(memberId, coinUnit)));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean create(Long memberId, String coinUnit) {
        ExchangeWallet wallet = new ExchangeWallet();

        wallet.setId(getWalletPk(memberId, coinUnit));
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setFrozenBalance(BigDecimal.ZERO);
        wallet.setMemberId(memberId);
        wallet.setCoinUnit(coinUnit);
        wallet.setIsLock(ExchangeLockStatus.UNLOCK);
        wallet.setCreateTime(Calendar.getInstance().getTime());

        // 签名
        validateHelper.udpate(memberId, null, wallet);

        return save(wallet);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean sync(Long memberId, String coinUnit, BigDecimal balance, BigDecimal frozenBalance) {

        String pk = getWalletPk(memberId, coinUnit);

        // 没有则创建
        if (!findOne(memberId, coinUnit).isPresent()) {
            if (!create(memberId, coinUnit)) {
                throw ExchangeOrderMsgCode.CREATE_WALLET_FAILED.asException();
            }
        }

        // 锁操作
        return lockTemplate.execute(pk, 3000, new Callback<Boolean>() {
            @Override
            public Boolean onGetLock() throws Exception {
                // todo 暂时屏蔽
                try {
                    // 验签
                    ExchangeWallet wallet = validateHelper.validate(memberId, null, () -> getById(pk));
                } catch (Exception ex) {
                    log.error("验签失败", ex);
                    log.warn("验签失败,pk={}", pk);
                }
                // 签名
                boolean sync = baseMapper.sync(pk, balance, frozenBalance) > 0;

                if (sync) {
                    ExchangeWallet new_value = getById(pk);
                    validateHelper.udpate(new_value.getMemberId(), null, new_value);
                    baseMapper.signature(pk, new_value.getSignature());
                    return true;
                }
                return false;
            }

            @Override
            public Boolean onTimeout() throws Exception {
                return false;
            }
        });

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean freeze(Long memberId, String coinUnit, BigDecimal amount, BigDecimal frozen) {
        String pk = getWalletPk(memberId, coinUnit);

        Optional<ExchangeWallet> optional = findOne(memberId, coinUnit);
        // 没有则中止
        if (!optional.isPresent()) {
            throw ExchangeOrderMsgCode.WALLET_NOT_FOUNT.asException();
        }
        // 锁定则失败
        if (optional.get().getIsLock() == ExchangeLockStatus.LOCKED) {
            throw ExchangeOrderMsgCode.ACCOUNT_DISABLE.asException();
        }

        // 锁操作
        return lockTemplate.execute(pk, 3000, new Callback<Boolean>() {
            @Override
            public Boolean onGetLock() throws Exception {
                // todo 暂时屏蔽
                try {
                    // 验签
                    validateHelper.validate(memberId, null, () -> getById(pk));
                } catch (Exception ex) {
                    log.error("验签失败", ex);
                    log.warn("验签失败,pk={}", pk);
                }
                // 签名
                boolean freeze = baseMapper.freeze(pk, amount, frozen) > 0;

                if (freeze) {
                    ExchangeWallet new_value = getById(pk);
                    validateHelper.udpate(-1L, null, new_value);
                    baseMapper.signature(pk, new_value.getSignature());
                    return true;
                }
                return false;
            }

            @Override
            public Boolean onTimeout() throws Exception {
                return false;
            }
        });

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean reset(Long memberId, String coinUnit, BigDecimal balance, BigDecimal frozenBalance) {
        String pk = getWalletPk(memberId, coinUnit);
        Optional<ExchangeWallet> walletOptional = findOne(memberId, coinUnit);

        if (!walletOptional.isPresent()) {
            ExceptionUitl.throwsMessageCodeException(CommonMsgCode.UNKNOWN_ACCOUNT);
        }

        if (walletOptional.get().getIsLock() == ExchangeLockStatus.LOCKED) {
            ExceptionUitl.throwsMessageCodeException(ExchangeOrderMsgCode.ACCOUNT_DISABLE);
        }

        // 锁操作
        return lockTemplate.execute(pk, 3000, new Callback<Boolean>() {
            @Override
            public Boolean onGetLock() throws Exception {
                // 添加重置记录
                if (resetRecordService.addResetRecord(walletOptional.get())) {
                    // 签名
                    boolean flag = baseMapper.reset(pk, balance, frozenBalance) > 0;
                    if (flag) {
                        ExchangeWallet new_value = getById(pk);
                        validateHelper.udpate(new_value.getMemberId(), null, new_value);
                        baseMapper.signature(pk, new_value.getSignature());
                        return true;
                    }
                } else {
                    log.warn("添加重置记录失败");
                }

                return false;
            }

            @Override
            public Boolean onTimeout() throws Exception {
                return false;
            }
        });

    }

    @Override
    public IPage<ExchangeWalletVo> findList(WalletQueryVo vo) {
        IPage<ExchangeWalletVo> page = new Page<>(vo.getPage(), vo.getRows());
        page.setRecords(baseMapper.findList(page, vo));
        return page;
    }

    protected String getWalletPk(Long memberId, String coinUnit) {
        return memberId + ":" + coinUnit;
    }
}