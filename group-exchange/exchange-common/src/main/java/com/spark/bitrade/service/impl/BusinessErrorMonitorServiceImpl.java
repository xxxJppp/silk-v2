package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.entity.BusinessErrorMonitor;
import com.spark.bitrade.entity.constants.BusinessErrorMonitorType;
import com.spark.bitrade.mapper.BusinessErrorMonitorMapper;
import com.spark.bitrade.service.BusinessErrorMonitorService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * (BusinessErrorMonitor)表服务实现类
 *
 * @author yangch
 * @since 2019-09-17 16:45:45
 */
@Service("businessErrorMonitorService")
public class BusinessErrorMonitorServiceImpl extends ServiceImpl<BusinessErrorMonitorMapper, BusinessErrorMonitor> implements BusinessErrorMonitorService {

    @Override
    public void add(BusinessErrorMonitorType type, String inData, String errorMsg) {
        BusinessErrorMonitor entity = new BusinessErrorMonitor();
        entity.setCreateTime(new Date());
        entity.setMaintenanceTime(new Date());
        entity.setType(type);
        entity.setInData(inData);
        entity.setErrorMsg(errorMsg);
        entity.setMaintenanceStatus(BooleanEnum.IS_FALSE);
        this.baseMapper.insert(entity);
    }
}