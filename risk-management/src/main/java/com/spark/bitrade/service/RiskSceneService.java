package com.spark.bitrade.service;

import com.spark.bitrade.entity.RiskScene;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 风控场景配置 服务类
 * </p>
 *
 * @author qiliao
 * @since 2020-02-25
 */
public interface RiskSceneService extends IService<RiskScene> {

	void loadScene();
}
