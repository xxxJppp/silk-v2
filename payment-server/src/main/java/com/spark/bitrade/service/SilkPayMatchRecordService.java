package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.GpsLocation;
import com.spark.bitrade.entity.MqttPayState;
import com.spark.bitrade.entity.SilkPayMatchRecord;
import com.spark.bitrade.entity.SilkPayOrder;

/**
 * 付款匹配记录(SilkPayMatchRecord)表服务接口
 *
 * @author wsy
 * @since 2019-07-18 10:38:51
 */
public interface SilkPayMatchRecordService extends IService<SilkPayMatchRecord> {

    /**
     * 订单匹配和拆分
     * @author shenzucai
     * @time 2019.07.30 16:54
     * @param silkPayOrder
     * @param gpsLocation
     * @return true
     */
    Boolean payOrSplitOrderMatchRecord(SilkPayOrder silkPayOrder, GpsLocation gpsLocation);


    /**
     * 订单下发成功确认
     * @author shenzucai
     * @time 2019.08.05 16:42
     * @param mqttPayState
     * @return true
     */
    void distributeSuccess(MqttPayState mqttPayState);

    /**
     * 订单支付成功
     * @author shenzucai
     * @time 2019.08.05 17:03
     * @param mqttPayState
     * @return true
     */
    void paySuccessed(String clientId,MqttPayState mqttPayState);

    /**
     * 订单支付失败
     * @author shenzucai
     * @time 2019.08.05 17:03
     * @param mqttPayState
     * @return true
     */
    void payFailed(MqttPayState mqttPayState);

    /**
     * 订单分发
     * @author shenzucai
     * @time 2019.08.16 17:12
     * @param clientId
     * @return true
     */
    void asyncDistribute(String clientId);

    void updateStat(SilkPayOrder silkPayOrder);
}