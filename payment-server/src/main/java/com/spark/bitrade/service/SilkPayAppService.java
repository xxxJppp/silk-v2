package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.AppItem;
import com.spark.bitrade.entity.SilkPayApp;

import java.util.List;

/**
 * 应用管理(SilkPayApp)表服务接口
 *
 * @author wsy
 * @since 2019-07-19 16:28:05
 */
public interface SilkPayAppService extends IService<SilkPayApp> {

    /**
     * 获取应用管理数据
     * @author zhangYanjun
     * @time 2019.08.14 9:55
     * @param
     * @return java.util.List<com.spark.bitrade.entity.AppItem>
     */
    List<AppItem> getAppItems();

    void versionInfo();
}