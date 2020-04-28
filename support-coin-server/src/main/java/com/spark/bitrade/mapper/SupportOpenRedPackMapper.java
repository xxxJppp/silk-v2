package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.entity.SupportOpenRedPack;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 红包开通申请表 Mapper 接口
 * </p>
 *
 * @author qhliao
 * @since 2020-02-19
 */
public interface SupportOpenRedPackMapper extends BaseMapper<SupportOpenRedPack> {

    @Select("select * from support_open_red_pack where project_coin=#{projectCoin} limit 1")
    SupportOpenRedPack findByProjectCoin(@Param("projectCoin") String projectCoin);
}
