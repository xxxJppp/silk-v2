package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.entity.AppItem;
import com.spark.bitrade.entity.MqttAppVersion;
import com.spark.bitrade.mapper.SilkPayAppMapper;
import com.spark.bitrade.entity.SilkPayApp;
import com.spark.bitrade.mqtt.MqttRestApi;
import com.spark.bitrade.mqtt.MqttSender;
import com.spark.bitrade.service.SilkPayAppService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 应用管理(SilkPayApp)表服务实现类
 *
 * @author wsy
 * @since 2019-07-19 16:28:05
 */
@Service("silkPayAppService")
public class SilkPayAppServiceImpl extends ServiceImpl<SilkPayAppMapper, SilkPayApp> implements SilkPayAppService {

    @Resource
    private MqttSender mqttSender;

    @Override
    public List<AppItem> getAppItems() {
        return getBaseMapper().getAppItems();
    }

    @Override
    public void versionInfo() {
        MqttAppVersion version = new MqttAppVersion();
        // TODO 获取版本信息并推送
        mqttSender.sendToAll("version", version);
    }
}