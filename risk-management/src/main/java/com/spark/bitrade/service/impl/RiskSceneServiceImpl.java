package com.spark.bitrade.service.impl;

import com.spark.bitrade.common.util.RedisKeyGenerator;
import com.spark.bitrade.common.util.RedisUtil;
import com.spark.bitrade.entity.RiskScene;
import com.spark.bitrade.mapper.RiskSceneMapper;
import com.spark.bitrade.service.RiskSceneService;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 风控场景配置 服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2020-02-25
 */
@Service
public class RiskSceneServiceImpl extends ServiceImpl<RiskSceneMapper, RiskScene> implements RiskSceneService {

	@Resource
	private RedisUtil redisUtil;
	@Override
	public void loadScene() {
		if(CollectionUtils.isNotEmpty(this.list())) {
			this.redisUtil.setVal(RedisKeyGenerator.riskSceneGet(), JSONArray.toJSONString(this.list()));
		}
	}

	
}
