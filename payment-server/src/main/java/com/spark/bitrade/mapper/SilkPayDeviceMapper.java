package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.entity.SilkPayDevice;
import org.apache.ibatis.annotations.Update;

/**
 * 支付设备(SilkPayDevice)表数据库访问层
 *
 * @author wsy
 * @since 2019-07-18 10:38:05
 */
public interface SilkPayDeviceMapper extends BaseMapper<SilkPayDevice> {

    @Update("UPDATE `silk_pay_device` SET state = 0")
    int updateAllOffline();

}