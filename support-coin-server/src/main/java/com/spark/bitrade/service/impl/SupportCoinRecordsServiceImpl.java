package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constant.AuditStatusEnum;
import com.spark.bitrade.entity.SupportCoinRecords;
import com.spark.bitrade.mapper.SupportCoinRecordsMapper;
import com.spark.bitrade.service.SupportCoinRecordsService;
import com.spark.bitrade.service.SupportUpCoinApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 上币币种基本信息申请 服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
@Service
public class SupportCoinRecordsServiceImpl extends ServiceImpl<SupportCoinRecordsMapper, SupportCoinRecords> implements SupportCoinRecordsService {

    @Autowired
    public SupportCoinRecordsMapper coinRecordsMapper;

    @Autowired
    public SupportUpCoinApplyService upCoinApplyService;

    @Override
    public SupportCoinRecords getOneCoinRecords(Long memberId, Long upCoinId) {
        QueryWrapper<SupportCoinRecords> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SupportCoinRecords.MEMBER_ID, memberId);
        queryWrapper.eq(SupportCoinRecords.UP_COIN_ID, upCoinId);
        queryWrapper.orderByDesc(SupportCoinRecords.CREATE_TIME);
        //queryWrapper.eq(SupportCoinRecords.AUDIT_STATUS, AuditStatusEnum.PENDING);
        List<SupportCoinRecords> supportCoinRecords = coinRecordsMapper.selectList(queryWrapper);
        if(CollectionUtils.isEmpty(supportCoinRecords)){
            return null;
        }
        return supportCoinRecords.get(0);
    }

    @Override
    public List<SupportCoinRecords> getCoinRecordsList(Long memberId, Long upCoinId) {
        QueryWrapper<SupportCoinRecords> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SupportCoinRecords.MEMBER_ID, memberId);
        queryWrapper.eq(SupportCoinRecords.UP_COIN_ID, upCoinId);
        queryWrapper.ne(SupportCoinRecords.AUDIT_STATUS, AuditStatusEnum.PENDING);
        queryWrapper.orderByDesc(SupportCoinRecords.CREATE_TIME);
        return coinRecordsMapper.selectList(queryWrapper);
    }
}
