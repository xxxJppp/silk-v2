package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constants.ExchangeOrderMsgCode;
import com.spark.bitrade.dsc.AlarmMonitor;
import com.spark.bitrade.dsc.AlarmType;
import com.spark.bitrade.dsc.BooleanStatus;
import com.spark.bitrade.dsc.DscContext;
import com.spark.bitrade.dsc.aop.annotation.DscUpdate;
import com.spark.bitrade.dsc.api.MessagePusher;
import com.spark.bitrade.dto.FeeStats;
import com.spark.bitrade.entity.ExchangeWalletWalRecord;
import com.spark.bitrade.entity.dto.WalletSyncCountDto;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.mapper.ExchangeWalletWalExtMapper;
import com.spark.bitrade.mapper.ExchangeWalletWalRecordMapper;
import com.spark.bitrade.service.ExchangeWalletWalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 账户WAL流水记录表(ExchangeWalletWalRecord)表服务实现类
 *
 * @author archx
 * @since 2019-09-02 14:45:22
 */
public abstract class AbstractExchangeWalletWalRecordServiceImpl extends ServiceImpl<ExchangeWalletWalRecordMapper, ExchangeWalletWalRecord>
        implements ExchangeWalletWalRecordService {

    protected ExchangeWalletWalExtMapper extMapper;
    protected DscContext dscContext;
    protected MessagePusher messagePusher;

    @Autowired
    public void setExtMapper(ExchangeWalletWalExtMapper extMapper) {
        this.extMapper = extMapper;
    }

    @Autowired
    public void setDscContext(DscContext dscContext) {
        this.dscContext = dscContext;
    }

    @Autowired
    public void setMessagePusher(MessagePusher messagePusher) {
        this.messagePusher = messagePusher;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateSetSyncId(Long memberId, String coinUnit, Long syncId) {
        int affected = baseMapper.updateSetSyncId(memberId, coinUnit, syncId);

        if (affected > 0) {
            // validate
            QueryWrapper<ExchangeWalletWalRecord> query = new QueryWrapper<>();
            query.eq("member_id", memberId).eq("coin_unit", coinUnit).eq("sync_id", syncId);

            int invalid = 0;
            List<Long> ids = new ArrayList<>();
            for (ExchangeWalletWalRecord record : list(query)) {
                record.setSyncId(0L);
                boolean validate = dscContext.getDscEntityResolver(record).validate();
                if (!validate) {
                    // 数据被篡改,转移剔除篡改数据
                    Long id = record.getId();
                    invalid++;
                    // 告警通知
                    AlarmMonitor monitor = new AlarmMonitor();
                    monitor.setMemberId(record.getMemberId());
                    monitor.setAlarmType(AlarmType.DSC_VERIFY_SIGNATURE);
                    monitor.setStatus(BooleanStatus.IS_FALSE);
                    monitor.setAlarmMsg(String.format("币币账户流水记录签名验证失败 [ id = %d , ref_id = %s ]",
                            record.getId(), record.getRefId()));
                    // Kafka?
                    messagePusher.push(monitor);

                    ids.add(id);
                }
            }

            // 修改数据状态为 -1, FIXME 暂时不做处理
            /*
            if (invalid > 0) {
                UpdateWrapper<ExchangeWalletWalRecord> update = new UpdateWrapper<>();
                update.set("sync_id", -1L);
                update.in("id", ids);
                update(update);
            }

            return affected - invalid;*/

        }

        return affected;
    }

    @Override
    public WalletSyncCountDto sumSyncId(Long syncId) {
        return baseMapper.sumSyncId(syncId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void transfer(String refId) {
        QueryWrapper<ExchangeWalletWalRecord> query = new QueryWrapper<>();
        query.eq("ref_id", refId);

        List<ExchangeWalletWalRecord> list = list(query);

        if (list != null && list.size() > 0) {
            int save = extMapper.saveBatch(getTransferTableName(), list);
            if (save == list.size()) {
                extMapper.removeByRefId(refId);
            } else {
                throw new MessageCodeException(ExchangeOrderMsgCode.ERROR_WRITE_TO_DB);
            }
        }

    }

    /**
     * 获取表名, 该处理都是归档到前一天
     *
     * @return table
     */
    protected String getTransferTableName() {
        String prefix = "exchange_wallet_wal_record_his_";

        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DATE, -1);
        return prefix + new SimpleDateFormat("yyyyMMdd").format(instance.getTime());
    }

    @Override
    @DscUpdate(memberId = "#record.memberId")
    public boolean signAndSave(ExchangeWalletWalRecord record) {
        return save(record);
    }

    @Override
    public List<FeeStats> stats(String startTime, String endTime) {
        return this.baseMapper.stats(startTime, endTime);
    }
}