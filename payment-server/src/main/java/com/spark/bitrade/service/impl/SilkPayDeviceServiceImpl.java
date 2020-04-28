package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.entity.SilkPayDevice;
import com.spark.bitrade.mapper.SilkPayDeviceMapper;
import com.spark.bitrade.service.SilkPayDeviceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 支付设备(SilkPayDevice)表服务实现类
 *
 * @author wsy
 * @since 2019-07-18 10:38:08
 */
@Service("silkPayDeviceService")
public class SilkPayDeviceServiceImpl extends ServiceImpl<SilkPayDeviceMapper, SilkPayDevice> implements SilkPayDeviceService {

    @Resource
    private SilkPayDeviceMapper silkPayDeviceMapper;

    @Override
    public boolean login(String clientId, String username, String password) {
        QueryWrapper<SilkPayDevice> wrapper = new QueryWrapper<>();
        wrapper.eq("device_code", username);
        wrapper.eq("serial_no", clientId);
        wrapper.eq("enabled", BooleanEnum.IS_TRUE);
        SilkPayDevice device = silkPayDeviceMapper.selectOne(wrapper);
        return device != null && Objects.equals(password, device.getDevicePwd());
    }

    @Override
    public void updateAllOffline() {
        silkPayDeviceMapper.updateAllOffline();
    }

    @Override
    public void updateOnlineState(String username, String clientId, BooleanEnum state) {
        QueryWrapper<SilkPayDevice> wrapper = new QueryWrapper<>();
        wrapper.eq("device_code", username);
        wrapper.eq("serial_no", clientId);
        SilkPayDevice device = new SilkPayDevice();
        device.setState(state);
        update(device, wrapper);
    }
}