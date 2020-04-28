package com.spark.bitrade.service;

import com.spark.bitrade.entity.RiskControlSettings;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 风险控制设置内容 服务类
 * </p>
 *
 * @author qiliao
 * @since 2020-02-25
 */
public interface RiskControlSettingsService extends IService<RiskControlSettings> {

	void loadSettings() ;
}
