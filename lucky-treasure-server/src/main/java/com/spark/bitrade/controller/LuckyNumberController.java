package com.spark.bitrade.controller;


import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.common.LuckyBusinessUtil;
import com.spark.bitrade.controller.param.ListLuckyNumberParam;
import com.spark.bitrade.controller.vo.LuckyNumberListVo;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.service.LuckyJoinInfoService;
import com.spark.bitrade.service.LuckyNumberManagerService;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>
 * 幸运宝幸运号控制器
 * </p>
 *
 * @author qiliao
 * @since 2019-12-16
 */
@Api(value = "幸运数字控制器")
@RestController
@RequestMapping("api/v2/luckyNumber")
public class LuckyNumberController {

	@Resource
	private LuckyJoinInfoService luckyJoinInfoService;
	@Resource
	private LuckyNumberManagerService luckyNumberManagerService;

	/**
	 * 加入幸运欢乐号游戏
	 * @param member
	 * @param payCount
	 * @param gameId
	 * @return
	 * @author zhaopeng
	 * @since 2019年12月16日
	 */
	@ApiOperation(value = "加入游戏", tags = "幸运号加入游戏")
	@RequestMapping(value = "/joinGame" , method = RequestMethod.POST)
	public MessageRespResult<List<String>> memberJoinGame(@ApiIgnore @MemberAccount Member member , 
			@ApiParam(value = "币种") @RequestParam String coinUnit ,
			@ApiParam(value = "支付密码")@RequestParam String moneyPassword,
			@ApiParam(value = "购买数量") @RequestParam(required = false , defaultValue = "0" ,value = "payCount") int payCount ,
			@ApiParam(value = "活动id") @RequestParam(required = false , defaultValue = "0" ,value = "gameId") Long gameId) {
		LuckyBusinessUtil.validatePassword(moneyPassword,member.getJyPassword(),member.getSalt());
		return this.luckyJoinInfoService.joinLuckyNumberGame(member, payCount, gameId ,coinUnit);
	}

	@ApiOperation(value = "游戏目录", tags = "幸运号游戏目录")
	@RequestMapping(value = "no-auth/gameList" ,method = {RequestMethod.POST ,RequestMethod.GET})
	public MessageRespResult<IPage<LuckyNumberListVo>> gameList(ListLuckyNumberParam param){
		return this.luckyNumberManagerService.numberGameList(param);
	}
	
	
	@ApiOperation(value = "指定游戏信息", tags = "幸运号指定游戏信息")
	@RequestMapping(value = "no-auth/gameInfo" ,method = {RequestMethod.POST ,RequestMethod.GET} )
	public MessageRespResult<LuckyNumberListVo> gameInfo(ListLuckyNumberParam param){
		return this.luckyNumberManagerService.numberGameInfo(param);
	}
	
	@ApiOperation(value = "分享后执行", tags = "幸运号分享后执行")
	@RequestMapping(value = "appendWx" ,method = RequestMethod.POST)
	public MessageRespResult<String> appendWx(@ApiIgnore @MemberAccount Member member , ListLuckyNumberParam param) {
		return this.luckyJoinInfoService.appendWx(member ,param);
	}
	
}

