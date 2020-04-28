package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.dto.IncubatorsBasicInformationDto;
import com.spark.bitrade.entity.IncubatorsBasicInformation;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * <p>
 * 孵化区-上币申请表 Mapper 接口
 * </p>
 *
 * @author qiliao
 * @since 2019-08-30
 */
public interface IncubatorsBasicInformationMapper extends BaseMapper<IncubatorsBasicInformation> {
    /**
     * 获取会员上币申请情况
     *
     * @param memberId 会员ID
     * @return 会员上币申请详情
     */
    IncubatorsBasicInformationDto getIncubatorsBasicInformationByMemberId(@Param("memberId") Long memberId);

    /**
     * 修改孵化区申请表锁仓数量
     *
     * @param id  id
     * @param num 锁仓数量
     * @return
     */
    Integer updateIncubatorsBasicInformation(@Param("id") Long id, @Param("num") BigDecimal num);
}
