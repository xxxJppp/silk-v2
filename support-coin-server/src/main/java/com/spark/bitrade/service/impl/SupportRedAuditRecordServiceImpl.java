package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.entity.SupportRedAuditRecord;
import com.spark.bitrade.mapper.SupportRedAuditRecordMapper;
import com.spark.bitrade.service.SupportRedAuditRecordService;
import com.spark.bitrade.vo.ApplyRedPackAuditRecordVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 红包审核记录表 服务实现类
 * </p>
 *
 * @author qhliao
 * @since 2020-02-19
 */
@Service
public class SupportRedAuditRecordServiceImpl extends ServiceImpl<SupportRedAuditRecordMapper, SupportRedAuditRecord> implements SupportRedAuditRecordService {
    @Override
    public List<ApplyRedPackAuditRecordVo> applyRedPackAuditHistory(Long applyRedPackId, Integer type) {
        return baseMapper.applyRedPackAuditHistory(applyRedPackId,type);
    }

    @Override
    public List<SupportRedAuditRecord> pendingList(Long applyRedPackId, Integer type) {
        return baseMapper.pendingList(applyRedPackId,type);
    }
}
