package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.entity.SilkPayOrder;
import com.spark.bitrade.entity.vo.SilkPayOrderVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 支付订单(SilkPayOrder)表数据库访问层
 *
 * @author wsy
 * @since 2019-07-18 10:39:01
 */
public interface SilkPayOrderMapper extends BaseMapper<SilkPayOrder> {

    @Select("SELECT o.*, NOW() AS `current_time` FROM `silk_pay_order` o WHERE o.id = #{orderSn}")
    SilkPayOrderVo selectByOrderSn(@Param("orderSn") String orderSn);
}