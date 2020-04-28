package com.spark.bitrade.service.impl;

import com.spark.bitrade.common.util.RedisUtil;
import com.spark.bitrade.entity.RiskControlSettings;
import com.spark.bitrade.mapper.RiskControlSettingsMapper;
import com.spark.bitrade.service.RiskControlSettingsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 风险控制设置内容 服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2020-02-25
 */
@Service
public class RiskControlSettingsServiceImpl extends ServiceImpl<RiskControlSettingsMapper, RiskControlSettings> implements RiskControlSettingsService {

	@Resource
	private RedisUtil redisUtil;
	
	@Override
	public void loadSettings() {
		if(CollectionUtils.isNotEmpty(this.list())) {
			this.list().stream().forEach(each -> {
				this.redisUtil.setVal(each.getSetKey(), each.getSetVal());
			});
		}
	}

	
}
