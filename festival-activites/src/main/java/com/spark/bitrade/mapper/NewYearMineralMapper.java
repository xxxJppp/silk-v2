package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.entity.NewYearMineral;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Map;

/**
 * <p>
 * 矿石表 Mapper 接口
 * </p>
 *
 * @author qiliao
 * @since 2019-12-30
 */
public interface NewYearMineralMapper extends BaseMapper<NewYearMineral> {

    @Update("update new_year_mineral set cost = cost + 1 where mineral_type = #{mineralType}")
    void updateByType(@Param("mineralType") Integer type);

    @Select("select * from silk_plat_information where info_type=16")
    Map<String,Object> findSilkPlatInfo();
}
