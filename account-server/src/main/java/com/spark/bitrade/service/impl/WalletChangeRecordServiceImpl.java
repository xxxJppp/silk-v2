package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constant.TccStatus;
import com.spark.bitrade.mapper.WalletChangeRecordMapper;
import com.spark.bitrade.entity.WalletChangeRecord;
import com.spark.bitrade.service.WalletChangeRecordService;
import org.springframework.stereotype.Service;

/**
 * 用户钱包资金变更流水记录(WalletChangeRecord)表服务实现类
 *
 * @author yangch
 * @since 2019-06-15 16:40:21
 */
@Service("walletChangeRecordService")
public class WalletChangeRecordServiceImpl extends ServiceImpl<WalletChangeRecordMapper, WalletChangeRecord> implements WalletChangeRecordService {

    @Override
    public WalletChangeRecord findOne(long memberId, long walletChangeRecordId) {
        return this.baseMapper.selectOne(new QueryWrapper<WalletChangeRecord>()
                .eq("member_id", memberId)
                .eq("id", walletChangeRecordId));
    }

    @Override
    public boolean updateStatus(long memberId, long walletChangeRecordId,
                                BooleanEnum oldStatus, BooleanEnum newStatus) {
        return SqlHelper.retBool(this.baseMapper.updateStatus(memberId, walletChangeRecordId, oldStatus, newStatus));
    }

    @Override
    public boolean updateTccStatus(long memberId, long walletChangeRecordId, TccStatus oldStatus, TccStatus newStatus) {
        return SqlHelper.retBool(this.baseMapper.updateTccStatus(memberId, walletChangeRecordId, oldStatus, newStatus));
    }
}