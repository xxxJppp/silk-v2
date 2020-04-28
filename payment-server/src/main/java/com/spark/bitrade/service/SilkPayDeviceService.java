package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.entity.SilkPayDevice;

/**
 * 支付设备(SilkPayDevice)表服务接口
 *
 * @author wsy
 * @since 2019-07-18 10:38:07
 */
public interface SilkPayDeviceService extends IService<SilkPayDevice> {

    boolean login(String clientId, String username, String password);

    /**
     * 变更设备在线状态
     *
     * @param username 用户名
     * @param clientId 设备码
     * @param state    在线状态
     */
    void updateOnlineState(String username, String clientId, BooleanEnum state);

    /**
     * 变更所有设备为离线状态
     */
    void updateAllOffline();
}