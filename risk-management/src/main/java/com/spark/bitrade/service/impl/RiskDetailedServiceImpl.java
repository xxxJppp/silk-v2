package com.spark.bitrade.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.common.util.RedisKeyGenerator;
import com.spark.bitrade.common.util.RedisUtil;
import com.spark.bitrade.common.util.RiskSetting;
import com.spark.bitrade.common.util.RiskSetting.ConfigSetting;
import com.spark.bitrade.common.util.RiskSetting.RiskDetailedType;
import com.spark.bitrade.common.vo.RiskDetailedCacheVo;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.RiskDetailed;
import com.spark.bitrade.entity.RiskHighMember;
import com.spark.bitrade.entity.RiskSummary;
import com.spark.bitrade.mapper.RiskDetailedMapper;
import com.spark.bitrade.service.IMemberApiService;
import com.spark.bitrade.service.MemberService;
import com.spark.bitrade.service.RiskDetailedService;
import com.spark.bitrade.service.RiskHighMemberService;
import com.spark.bitrade.service.RiskSummaryService;
import com.spark.bitrade.util.MessageRespResult;

/**
 * <p>
 * 风控出入金明细 服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2020-02-25
 */
@Service
public class RiskDetailedServiceImpl extends ServiceImpl<RiskDetailedMapper, RiskDetailed> implements RiskDetailedService {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Resource
	private RedisUtil redisUtil;
	@Resource
	private RiskSummaryService riskSummaryService;
	@Resource
	private IMemberApiService iMemberApiService;
	@Resource
	private RiskHighMemberService riskHighMemberService;
	@Resource
	private MemberService memberService;
	
	@Override
	public IPage<RiskDetailed> list(Long memberId, String inOut, String type , String unit , String timeStart , String timeEnd ,int pageNum, int size) {
		QueryWrapper<RiskDetailed> queryWrapper = new QueryWrapper<RiskDetailed>();
		queryWrapper.eq("member_id", memberId).eq("in_out", inOut);
		if(StringUtils.isNotBlank(type)) {
			queryWrapper.eq("type_desc", type);
		}
		if(StringUtils.isNotBlank(unit)) {
			queryWrapper.eq("unti", unit);
		}
		if(StringUtils.isNotBlank(timeStart)) {
			try {
				queryWrapper.ge("create_time", sdf.parse(timeStart));
			} catch (ParseException e) {
				log.error("时间格式错误yyyy-MM-dd HH:mm:ss ---->" + timeStart);
			}
		}
		if(StringUtils.isNotBlank(timeEnd)) {
			try {
				queryWrapper.le("create_time", sdf.parse(timeEnd));
			} catch (ParseException e) {
				log.error("时间格式错误yyyy-MM-dd HH:mm:ss ---->" + timeEnd);
			}
		}
		return this.page(new Page<RiskDetailed>(pageNum, size) ,queryWrapper);
	}

	@Transactional
	@Override
	public MessageRespResult<Boolean> addDetailed(Long memberId, String inOut, double money, String desc,
			Long customerId) {
		
		RiskDetailedCacheVo rd = new RiskDetailedCacheVo();
		rd.setAmount(new BigDecimal(money));
		rd.setConvertAmount(new BigDecimal(money));
		rd.setCreateTime(new Date());
		rd.setDetailedDesc(desc);
		rd.setExchange(new BigDecimal(1));
		rd.setExchangeSource("CNYT");
		rd.setInOut(inOut);
		rd.setMemberId(memberId);
		rd.setMessageId("");
		rd.setRfId(customerId);
		rd.setTypeDesc(RiskDetailedType.RISK_ADJUSTMENT);
		rd.setUnti("CNYT");
		rd.setAbstractKey(RiskSetting.getAbstrackKey(rd.getAmount(), rd.getUnti(), rd.getMemberId(), rd.getInOut(), rd.getCreateTime()));
		rd.setWorkNumber(1);
		rd.setCheck(false);
		this.redisUtil.leftPush(RedisKeyGenerator.detailedWorkList(), JSON.toJSONString(rd));
		return MessageRespResult.success("", true);
	}

	@Transactional
	@Override
	public boolean updateDetailed(RiskDetailedCacheVo vo) {
		this.save(vo.toRiskDetailed());//明细
		//汇总
		RiskSummary rs = this.riskSummaryService.getOne(new QueryWrapper<RiskSummary>().eq("member_id", vo.getMemberId()));
		if(rs == null) {
			rs = new RiskSummary();
			rs.setCreateTime(new Date());
			rs.setUpdateTime(rs.getCreateTime());
			rs.setInSum(vo.getInOut().equals("0") ? new BigDecimal(0) : vo.getConvertAmount());
			rs.setOutSum(vo.getInOut().equals("1") ? new BigDecimal(0) : vo.getConvertAmount());
			rs.setMemberId(vo.getMemberId());
			BigDecimal coefficient = rs.getOutSum().subtract(rs.getInSum());
			if(rs.getInSum().compareTo(BigDecimal.ZERO) != 0) {
				coefficient = coefficient.divide(rs.getInSum() , 2 ,BigDecimal.ROUND_HALF_UP );
			}
			Member member = this.memberService.getById(vo.getMemberId());
			rs.setMemberName(member.getRealName());
			rs.setPhone(member.getMobilePhone());
			rs.setCoefficient(coefficient);
			this.riskSummaryService.save(rs);
		}
		else {
			rs.setUpdateTime(new Date());
			if(vo.getInOut().equals("0")) { //出
				rs.setOutSum(rs.getOutSum().add(vo.getConvertAmount()));
			}
			else {
				rs.setInSum(rs.getInSum().add(vo.getConvertAmount()));
			}
			BigDecimal coefficient = rs.getOutSum().subtract(rs.getInSum());
			if(rs.getInSum().compareTo(BigDecimal.ZERO) != 0) {
				coefficient = coefficient.divide(rs.getInSum() , 2 ,BigDecimal.ROUND_HALF_UP );
			}
			rs.setCoefficient(coefficient);
			this.riskSummaryService.updateById(rs);
		}
		BigDecimal par = new BigDecimal(this.redisUtil.getVal(ConfigSetting.RISK_COEFFICIENT_KEY).toString()); //风险阈值
		if(rs.getCoefficient().compareTo(par) == 1 && vo.getInOut().equals("0")) { //如果风险系数大于阈值，且当前为出场,添加高风险用户记录
			RiskHighMember rhm = new RiskHighMember();
			rhm.setCoefficient(rs.getCoefficient());
			rhm.setExamineStatus("0");
			rhm.setInSum(rs.getInSum());
			rhm.setMemberId(rs.getMemberId());
			rhm.setMemberName(rs.getMemberName());
			rhm.setOutSum(rs.getOutSum());
			rhm.setOutTime(new Date());
			rhm.setPhone(rs.getPhone());
			this.riskHighMemberService.save(rhm);
		}
		return true;
	}

	
	
}
