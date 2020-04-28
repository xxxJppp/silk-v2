package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.entity.IncubatorsFundDetails;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * <p>
 * 孵化区-解锁仓明细表 Mapper 接口
 * </p>
 *
 * @author qiliao
 * @since 2019-08-30
 */
public interface IncubatorsFundDetailsMapper extends BaseMapper<IncubatorsFundDetails> {
    /**
     * 修改孵化区锁仓明细
     *
     * @param id  孵化区申请ID
     * @param num 锁仓数量
     * @return
     */
    Integer updateIncubatorsFundDetails(@Param("id") Long id, @Param("num") BigDecimal num);
}
