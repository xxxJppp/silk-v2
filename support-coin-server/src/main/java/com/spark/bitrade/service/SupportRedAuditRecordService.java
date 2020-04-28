package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.SupportRedAuditRecord;
import com.spark.bitrade.vo.ApplyRedPackAuditRecordVo;

import java.util.List;

/**
 * <p>
 * 红包审核记录表 服务类
 * </p>
 *
 * @author qhliao
 * @since 2020-02-19
 */
public interface SupportRedAuditRecordService extends IService<SupportRedAuditRecord> {
    List<ApplyRedPackAuditRecordVo> applyRedPackAuditHistory(Long applyRedPackId, Integer type);

    List<SupportRedAuditRecord> pendingList(Long applyRedPackId,Integer type);
}
