package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constants.ExchangeCywMsgCode;
import com.spark.bitrade.entity.dto.WalletSyncCountDto;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.mapper.CywWalletWalExtMapper;
import com.spark.bitrade.mapper.CywWalletWalRecordMapper;
import com.spark.bitrade.entity.CywWalletWalRecord;
import com.spark.bitrade.service.CywWalletWalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * 机器人账户WAL流水记录表(CywWalletWalRecord)表服务实现类
 *
 * @author archx
 * @since 2019-09-02 14:45:22
 */
@Service("cywWalletWalRecordService")
public class CywWalletWalRecordServiceImpl extends ServiceImpl<CywWalletWalRecordMapper, CywWalletWalRecord>
        implements CywWalletWalRecordService {

    private CywWalletWalExtMapper extMapper;

    @Autowired
    public void setExtMapper(CywWalletWalExtMapper extMapper) {
        this.extMapper = extMapper;
    }

    @Transactional
    @Override
    public int updateSetSyncId(Long memberId, String coinUnit, Long syncId) {
        return baseMapper.updateSetSyncId(memberId, coinUnit, syncId);
    }

    @Override
    public WalletSyncCountDto sumSyncId(Long syncId) {
        return baseMapper.sumSyncId(syncId);
    }

    @Transactional
    @Override
    public void transfer(String refId) {
        QueryWrapper<CywWalletWalRecord> query = new QueryWrapper<>();
        query.eq("ref_id", refId);

        List<CywWalletWalRecord> list = list(query);

        if (list != null && list.size() > 0) {
            int save = extMapper.saveBatch(getTransferTableName(), list);
            if (save == list.size()) {
                extMapper.removeByRefId(refId);
            } else {
                throw new MessageCodeException(ExchangeCywMsgCode.ERROR_WRITE_TO_DB);
            }
        }

    }

    /**
     * 获取表名, 该处理都是归档到前一天
     *
     * @return table
     */
    private String getTransferTableName() {
        String prefix = "cyw_wallet_wal_record_his_";

        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DATE, -1);
        return prefix + new SimpleDateFormat("yyyyMMdd").format(instance.getTime());
    }
}