package com.spark.bitrade.controller;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.biz.MyOreService;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.NewYearMemberRecord;
import com.spark.bitrade.util.HttpRequestUtil;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 我的矿石相关控制
 * </p>
 *
 * @author zhaopeng 
 * @since 2019年12月30日
 */

@Api(value = "我的矿石相关")
@RestController
@RequestMapping("api/v2/myOre")
public class MyOreController {

	@Resource
	private MyOreService myOreService;
	
	@ApiOperation(tags = "矿石收集" ,value = "收集完成人数")
	@RequestMapping(value = "/completedMemberCount",method = {RequestMethod.POST,RequestMethod.GET})
	public MessageRespResult<Long> completedMemberCount() {
		return MessageRespResult.success("", this.myOreService.completedMemberCount());
	}
	
	@ApiOperation(tags = "矿石收集" ,value = "我的矿石收集明细")
	@RequestMapping(value ="/myOreCount",method = {RequestMethod.POST,RequestMethod.GET})
	public MessageRespResult myOreCount(@ApiParam(value = "个人登录信息")@MemberAccount Member member) {
		return MessageRespResult.success("" ,this.myOreService.myOreCount(member));
	}
	
	@ApiOperation(tags = "矿石收集" ,value = "合成钥匙")
	@RequestMapping(value ="/synthesisOre",method = {RequestMethod.POST,RequestMethod.GET})
	public MessageRespResult<Map<String,String>> synthesisOre(@ApiParam(value = "个人登录信息")@MemberAccount Member member) {
		return this.myOreService.synthesisOre(member);
	}
	
	@ApiOperation(tags = "矿石收集" ,value = "我的矿石流水 ， 时间倒序")
	@RequestMapping(value ="/myOreHistory",method = {RequestMethod.POST,RequestMethod.GET})
	public MessageRespResult<IPage<NewYearMemberRecord>> myOreHistory(@ApiParam(value = "个人登录信息")@MemberAccount Member member ,
																													   @ApiParam(value = "页码")int pageNum , 
																													   @ApiParam(value = "页数据量")int size) {
		return MessageRespResult.success("", this.myOreService.myOreHistory(member, pageNum, size));
	}
	

	
	@ApiOperation(tags = "矿石收集" ,value = "最新令牌合成记录/仅返回最新10条   0-10条")
	@RequestMapping(value ="/synthesisNewest",method = {RequestMethod.POST,RequestMethod.GET})
	public MessageRespResult<List<String>> synthesisNewest() {
		return this.myOreService.synthesisNewest();
	}
	
	@ApiOperation(tags = "矿石收集" ,value = "我的矿石赠送")
	@RequestMapping(value ="/giveMyOre",method = {RequestMethod.POST,RequestMethod.GET})
	public MessageRespResult<String> giveMyOre(@ApiParam(value = "个人登录信息")@MemberAccount Member member ,
											   @ApiParam(value = "矿石名称")String oreName ,
											   @ApiParam(value = "赠送数量")int count ,
											   @ApiParam(value = "被增送人编号")Long customerId,
											   HttpServletRequest request) {
		return this.myOreService.giveMyOre(member, oreName, count, customerId,request);
	}
	
	@ApiOperation(tags = "矿石收集" ,value = "开奖")
	@RequestMapping(value ="/lottery",method = {RequestMethod.POST,RequestMethod.GET})
	public MessageRespResult<List<String>> lottery(@ApiParam(value = "个人登录信息")@MemberAccount Member member) {
		return this.myOreService.lottery(member , Integer.parseInt(HttpRequestUtil.getAppId()));
	}
	
	@ApiOperation(tags = "矿石收集" ,value = "开奖结果")
	@RequestMapping(value ="/lotteryList",method = {RequestMethod.POST,RequestMethod.GET})
	public MessageRespResult<List<String>> lotteryList(@ApiParam(value = "个人登录信息")@MemberAccount Member member) {
		return this.myOreService.lotteryList(member);
	}
	
	@ApiOperation(tags = "矿石收集" ,value = "是否已经开奖")
	@RequestMapping(value ="/hasLottery",method = {RequestMethod.POST,RequestMethod.GET})
	public MessageRespResult<Boolean> isOpen(@ApiParam(value = "个人登录信息")@MemberAccount Member member) {
		return this.myOreService.isOpen(member);
	}
	
	@ApiOperation(tags = "矿石收集" ,value = "实时实际开奖人数")
	@RequestMapping(value ="/lotterCount",method = {RequestMethod.POST,RequestMethod.GET})
	public MessageRespResult<Integer> lotterCount() {
		return this.myOreService.lotterCount();
	}
	
	@ApiOperation(tags = "矿石收集" ,value = "奖励释放任务")
	@RequestMapping(value ="/timeFree",method = {RequestMethod.POST})
	public MessageRespResult<Boolean> timeFree() {
		this.myOreService.timeFree();
		return MessageRespResult.success("", true);
	}
}

