package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.BusinessErrorMonitor;
import com.spark.bitrade.entity.constants.BusinessErrorMonitorType;

/**
 * (BusinessErrorMonitor)表服务接口
 *
 * @author yangch
 * @since 2019-09-17 16:45:45
 */
public interface BusinessErrorMonitorService extends IService<BusinessErrorMonitor> {

    /**
     * 记录业务错误记录
     *
     * @param type
     * @param inData
     * @param errorMsg
     */
    void add(BusinessErrorMonitorType type, String inData, String errorMsg);
}