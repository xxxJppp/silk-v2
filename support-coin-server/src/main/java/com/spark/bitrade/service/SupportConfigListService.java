package com.spark.bitrade.service;

import com.spark.bitrade.entity.SupportConfigList;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 扶持上币配置KEY-VALUE 服务类
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
public interface SupportConfigListService extends IService<SupportConfigList> {


    SupportConfigList findByKey(String configKey, String dicKey);

}
