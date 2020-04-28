package com.spark.bitrade.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.spark.bitrade.common.LuckyGameRedisUtil;
import com.spark.bitrade.entity.Coin;
import com.spark.bitrade.entity.LuckyNumberManager;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.service.IAccountService;
import com.spark.bitrade.service.LuckyJoinInfoService;
import com.spark.bitrade.service.LuckyNumberManagerService;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



/**
 * 公共控制器 查询列表 等
 */
@Api(value = "幸运宝公共控制")
@RestController
@RequestMapping("api/v2/luckyCommon")
public class LuckyCommonController {
	@Resource
	private LuckyJoinInfoService luckyJoinInfoService;
	@Resource
	private LuckyNumberManagerService luckyNumberManagerService;
	@Resource
	private IAccountService iAccountService;

	@Autowired
	private LuckyGameRedisUtil luckyGameRedisUtil;
	
	/**
	 * 活动记录 红点信息
	 * 包含一级活动记录/ 我的活动 红点
	 * @param member
	 * @return
	 * @author zhaopeng
	 * @since 2019年12月16日
	 */
	@RequestMapping(value = "/activityRecord")
	public MessageRespResult<Map<String , Object>> activityRecord(@MemberAccount Member member) {
		return MessageRespResult.success4Data(this.luckyJoinInfoService.activityRecord(member.getId()));
	}
	
	@ApiOperation(value = "获取全部币种", tags = "币种")
	@RequestMapping(value = "no-auth/coinList")
	public MessageRespResult<List<String>> coinList() {
		return MessageRespResult.success4Data(iAccountService.getCoins().getData().stream().map(Coin::getName).collect(Collectors.toList()));
	}
	
	/**                                         定时任务使用的接口                                           **/
	

	@ApiOperation(value = "幸运宝定时任务", tags = "幸运宝定时任务")
	@GetMapping(value = "/luckyGameSchedule")
	public MessageRespResult<String> luckyGameSchedule() {
		return luckyNumberManagerService.luckyGameSchedule();
	}

	@ApiOperation(value = "弹框请求", tags = "弹框请求")
	@RequestMapping(value = "findMyLastestLucky")
	public MessageRespResult findLastestLucky(@MemberAccount Member member){
		LuckyNumberManager lastSettleLucky = luckyNumberManagerService.findLastSettleLucky(member.getId());
		if (lastSettleLucky==null){
			return MessageRespResult.success();
		}
		String key = String.format("lucky:member:tankuang:%s:actId:%s", member.getId(),lastSettleLucky.getId());

		if (luckyGameRedisUtil.redisKeyExist(key)){
			return MessageRespResult.success();
		}
		luckyGameRedisUtil.setKey(key,lastSettleLucky.getId().toString());
		JSONObject o=new JSONObject();
		o.put("actId",lastSettleLucky.getId());
		o.put("actType",lastSettleLucky.getActType()+1);

		return MessageRespResult.success4Data(o);
	}
}
