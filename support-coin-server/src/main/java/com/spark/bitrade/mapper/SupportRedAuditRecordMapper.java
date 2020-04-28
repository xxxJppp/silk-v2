package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.entity.SupportRedAuditRecord;
import com.spark.bitrade.vo.ApplyRedPackAuditRecordVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 红包审核记录表 Mapper 接口
 * </p>
 *
 * @author qhliao
 * @since 2020-02-19
 */
public interface SupportRedAuditRecordMapper extends BaseMapper<SupportRedAuditRecord> {
    List<ApplyRedPackAuditRecordVo> applyRedPackAuditHistory(@Param("applyRedPackId") Long applyRedPackId, @Param("type") Integer type);

    @Select("select * from support_red_audit_record where open_red_id=#{applyRedPackId} and apply_type=#{type} and audit_status=0")
    List<SupportRedAuditRecord> pendingList(@Param("applyRedPackId") Long applyRedPackId, @Param("type") Integer type);
}
