package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.dto.SuperAwardDto;
import com.spark.bitrade.entity.SuperAwardRecord;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author qiliao
 * @since 2019-07-16
 */
public interface SuperAwardRecordMapper extends BaseMapper<SuperAwardRecord> {

    List<SuperAwardDto> findSuperAwards(@Param("transationType") int transationType,
                                        @Param("exitIds") List<Long> exitIds,
                                        @Param("startDate") String startDate,
                                        @Param("endDate") String endDate,
                                        @Param("rate")BigDecimal rate,
                                        @Param("wardPids") List<String> wardPids);



    List<SuperAwardDto> findBBExchangeTotal(@Param("start") long start, @Param("end") long end, @Param("exitIds") List<Long> exitIds);

}
