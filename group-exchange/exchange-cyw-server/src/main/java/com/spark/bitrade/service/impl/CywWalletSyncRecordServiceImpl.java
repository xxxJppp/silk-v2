package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.entity.CywWalletSyncRecord;
import com.spark.bitrade.entity.dto.WalletSyncCountDto;
import com.spark.bitrade.mapper.CywWalletSyncRecordMapper;
import com.spark.bitrade.service.CywWalletSyncRecordService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 账户WAL流水同步记录表(CywWalletSyncRecord)表服务实现类
 *
 * @author archx
 * @since 2019-09-02 14:44:18
 */
@Service("cywWalletSyncRecordService")
public class CywWalletSyncRecordServiceImpl extends ServiceImpl<CywWalletSyncRecordMapper, CywWalletSyncRecord>
        implements CywWalletSyncRecordService {
    @Override
    public CywWalletSyncRecord getNewest(Long memberId, String coinUnit) {
        return this.baseMapper.getNewest(memberId, coinUnit);
    }

    @Override
    public WalletSyncCountDto sum(Long memberId, String coinUnit, Date startTime, Date endTime) {
        return this.baseMapper.sum(memberId, coinUnit, startTime, endTime);
    }

}