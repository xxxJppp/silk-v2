package com.spark.bitrade.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.common.util.RedisKeyGenerator;
import com.spark.bitrade.common.util.RedisUtil;
import com.spark.bitrade.entity.RiskSummary;
import com.spark.bitrade.mapper.RiskSummaryMapper;
import com.spark.bitrade.service.RiskSummaryService;
import com.spark.bitrade.util.MessageRespResult;

/**
 * <p>
 * 风控出入金汇总 服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2020-02-25
 */
@Service
public class RiskSummaryServiceImpl extends ServiceImpl<RiskSummaryMapper, RiskSummary> implements RiskSummaryService {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Resource
	private RiskSummaryMapper riskSummaryMapper;
	@Resource
	private RedisUtil redisUtil;
	
	@Override
	public IPage<RiskSummary> list(Long memberId, String phone, String outTimeStart,
			String outTimeEnd, Double coefficientStart, Double coefficientEnd ,int pageNum ,int pageSize) {
		QueryWrapper<RiskSummary> queryWrapper = new QueryWrapper<RiskSummary>();
		if(memberId != -1) {
			queryWrapper.eq("member_id", memberId);
		}
		if(StringUtils.isNotBlank(phone)) {
			queryWrapper.eq("phone", phone);
		}
		if(StringUtils.isNotBlank(outTimeStart)) {
			try {
				queryWrapper.ge("last_update_time", sdf.parse(outTimeStart));
			} catch (ParseException e) {
				log.error("时间格式错误yyyy-MM-dd HH:mm:ss ---->" + outTimeStart);
			}
		}
		if(StringUtils.isNotBlank(outTimeEnd)) {
			try {
				queryWrapper.le("last_update_time", sdf.parse(outTimeEnd));
			} catch (ParseException e) {
				log.error("时间格式错误yyyy-MM-dd HH:mm:ss ---->" + outTimeEnd);
			}
		}
		if(coefficientStart != -1) {
			queryWrapper.ge("coefficient", new BigDecimal(coefficientStart));
		}
		if(coefficientEnd != -1) {
			queryWrapper.le("coefficient", new BigDecimal(coefficientEnd));
		}
		return this.page(new Page<RiskSummary>(pageNum, pageSize) ,queryWrapper);
	}

	
}
