package com.spark.bitrade.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.entity.CurrencyManage;
import com.spark.bitrade.entity.OtcCoin;
import com.spark.bitrade.enums.OtcExceptionMsg;
import com.spark.bitrade.mapper.OtcCoinMapper;
import com.spark.bitrade.service.CurrencyManageService;
import com.spark.bitrade.service.CurrencyRateService;
import com.spark.bitrade.service.OtcCoinService;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.MessageRespResult;

/**
 * (OtcCoin)表服务实现类
 *
 * @author ss
 * @date 2020-03-19 10:23:48
 */
@Service("otcCoinService")
public class OtcCoinServiceImpl extends ServiceImpl<OtcCoinMapper,OtcCoin> implements OtcCoinService {
    @Resource
    private OtcCoinMapper otcCoinMapper;
    @Resource
    private CurrencyManageService currencyManageService;

	@Override
	public MessageRespResult<List<Map<String, Object>>> selectCoinForCurrency() {
		List<Map<String, Object>> resultList = new ArrayList<>();

		List<OtcCoin> openUnits = this.otcCoinMapper.selectOpenOtcCoin();
		for(OtcCoin u : openUnits) {
//			if(u.getUnit().equals("DCC")) {continue;}
			Map<String, Object> result = new HashMap<>();
			List<CurrencyManage> cms = new ArrayList<CurrencyManage>();
			AssertUtil.notNull(u, OtcExceptionMsg.INVALID_UNIT);
			AssertUtil.hasText(u.getCurrencyId(), OtcExceptionMsg.MEMBER_HAS_NO_BASE_CURRENCY);
			String[] ids = u.getCurrencyId().split(",");
			if(ids.length == 1) {
				cms.add(currencyManageService.getById(Long.parseLong(ids[0])));
			}
			else {
				cms = currencyManageService.list(new QueryWrapper<CurrencyManage>().in("id", new ArrayList<Long>(
						Arrays.stream(ids)
	                    .map(s -> Long.parseLong(s.trim()))
	                    .collect(Collectors.toList())

						)));
			}
			result.put("currencyList", cms);
			result.put("sellMinAmount",u.getSellMinAmount());
			result.put("buyMinAmount",u.getBuyMinAmount());
			result.put("sort",u.getSort());
			result.put("id",u.getId());
			result.put("nameCn",u.getNameCn());
			result.put("coinScale",u.getCoinScale());
			result.put("unit",u.getUnit());
			resultList.add(result);
		}

		return MessageRespResult.success(null,resultList);
	}

	@Override
	public MessageRespResult<Map<String, List<OtcCoin>>> selectCurrencyForCoin(String[] baseId) {
		Map<String, List<OtcCoin>> result = new HashMap<String, List<OtcCoin>>();
		for(String id : baseId) {
			List<OtcCoin> allDatas = this.list(new QueryWrapper<OtcCoin>().like("currency_id", baseId));
			if(CollectionUtils.isNotEmpty(allDatas)) {//过滤可能产生的类似于  baseId = 1  currency_id = 11 之类的数据
				allDatas = allDatas.stream().filter(filter -> new ArrayList<String>(Arrays.asList(filter.getCurrencyId().split(","))).contains(baseId)).collect(Collectors.toList());
				result.put(id, allDatas);
			}
			else {
				result.put(id, new ArrayList<OtcCoin>());
			}
		}

		return MessageRespResult.success("", result);
	}

	@Override
	public List<Map<String, Object>> getAllNormalCoinAndBalance(Long id) {
		List<Map<String, Object>> rowslist = otcCoinMapper.getAllNormalCoinAndBalance(id);
		return rowslist;
	}


}
