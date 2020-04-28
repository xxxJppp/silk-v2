package com.spark.bitrade.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.common.vo.RiskDetailedCacheVo;
import com.spark.bitrade.entity.RiskDetailed;
import com.spark.bitrade.util.MessageRespResult;

/**
 * <p>
 * 风控出入金明细 服务类
 * </p>
 *
 * @author qiliao
 * @since 2020-02-25
 */
public interface RiskDetailedService extends IService<RiskDetailed> {

	public IPage<RiskDetailed> list(Long memberId ,  String inOut , String type , String unti , String timeStart , String timeEnd ,int pageNum ,int size);
	
	public MessageRespResult<Boolean> addDetailed(Long memberId ,String inOut ,double money ,String desc ,Long customerId);
	
	public boolean updateDetailed(RiskDetailedCacheVo vo);
}
