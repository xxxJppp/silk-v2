package com.spark.bitrade.mapper;

import com.spark.bitrade.entity.OtcOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * (OtcOrder)表数据库访问层
 *
 * @author ss
 * @date 2020-03-19 10:23:50
 */
public interface OtcOrderMapper extends BaseMapper<OtcOrder>{

    List<Map<String, Long>> findCountByMembers(@Param("memberIds") Long[] memberIds, @Param("type") Integer type);

    List<Map<String, Long>> selectCountByMembersAnd48(@Param("memberIds") Long[] memberIds, @Param("type") Integer type, @Param("date") Date date);


    /**
     * 查询所有未完成的订单数量
     * @param customerId
     * @return
     */
    @Select("SELECT COUNT(1) from otc_order o where o.customer_id = #{customerId} and (o.status=1 or o.status=2)")
    int findUnFinishNum(@Param("customerId") long customerId);

}
