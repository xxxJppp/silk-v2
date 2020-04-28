package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.*;
import com.spark.bitrade.entity.vo.SilkPayOrderVo;

import java.math.BigDecimal;

/**
 * 支付订单(SilkPayOrder)表服务接口
 *
 * @author wsy
 * @since 2019-07-18 10:39:01
 */
public interface SilkPayOrderService extends IService<SilkPayOrder> {

    /**
     *
     * @author shenzucai
     * @time 2019.07.30 16:03
     * @param gpsLocation 位置（经纬度）
     * @param silkPayCoin 配置币种
     * @param member 用户
     * @param receiptContent 接收的二维码内容
     * @param receiptName 收款人名称
     * @param amount 金额
     * @param unit 币种
     * @return true
     */
    SilkPayOrder createOrder(GpsLocation gpsLocation, SilkPayCoin silkPayCoin, SilkPayUserConfig userConfig, Member member, String receiptContent, String receiptName, BigDecimal amount, String unit);

    SilkPayOrderVo getByOrderSn(String order_sn);
}