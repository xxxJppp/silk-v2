package com.spark.bitrade.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.api.vo.CoinWalletVo;
import com.spark.bitrade.api.vo.TransferDirectVo;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.constants.MsgCode;
import com.spark.bitrade.controller.ApiController;
import com.spark.bitrade.entity.Coin;
import com.spark.bitrade.entity.ExchangeWallet;
import com.spark.bitrade.entity.ExchangeWalletWalRecord;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.constants.WalTradeType;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.job.util.DateUtils;
import com.spark.bitrade.service.ExchangeWalletOperations;
import com.spark.bitrade.service.ExchangeWalletService;
import com.spark.bitrade.service.ExchangeWalletWalRecordService;
import com.spark.bitrade.service.IAccountService;
import com.spark.bitrade.service.IOtcServer;
import com.spark.bitrade.trans.Tuple2;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

/**
 * 标准对外接口
 */
@RestController
@RequestMapping("api/v2")
@Api(tags = "币币标准对外接口")
public class ExExposeController extends ApiController {

    private ExchangeWalletService exchangeWalletService;
    private ExchangeWalletOperations exchangeWalletOperations;
    private ExchangeWalletWalRecordService exchangeWalletWalRecordService;

    private IAccountService accountService;
    
    private Log logger = LogFactory.getLog(getClass());
    //@Resource
    //private IOtcServer iOtcServer;

    /**
     * 钱包列表
     *
     * @param member 会员信息
     * @return resp
     */
    @ApiOperation(value = "钱包列表", notes = "获取所有钱包记录列表")
    @RequestMapping(value = "/wallets", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<List<CoinWalletVo>> wallets(@MemberAccount Member member) {
        // 获取支持的币种
    	logger.error("------------------------------------------------------");
    	long begin = System.currentTimeMillis();
        MessageRespResult<List<Coin>> resp = accountService.getCoins();
        if (!resp.isSuccess()) {
            MsgCode msgCode = CommonMsgCode.of(resp.getCode(), resp.getMessage());
            throw new MessageCodeException(msgCode);
        }
        logger.error("第一段" + (System.currentTimeMillis() - begin));
        begin = System.currentTimeMillis();
        List<Coin> coins = resp.getData();
        if (coins == null || coins.isEmpty()) {
            return failed();
        }
        /*将原有汇率更新为对应法币汇率
        String fbUnit = iOtcServer.getMemberCurrencyUnitById(member.getId()).replaceAll("\"", "");
        if(!StringUtils.isEmpty(fbUnit)) {
        	//当不为空时，使用指定法币的汇率
        	coins.stream().forEach(each -> {
        		MessageRespResult fcResult = iOtcServer.getCurrencyRate(fbUnit, each.getUnit());
        		if(fcResult.isSuccess()) {
        			Double rate = (Double) fcResult.getData();
        			each.setCnyRate(rate.doubleValue());
        		}
        	});
        }*/
        
        
        // 查询用户钱包
        QueryWrapper<ExchangeWallet> query = new QueryWrapper<>();
        query.eq("member_id", member.getId());
        Map<String, ExchangeWallet> map = new HashMap<>();

        for (ExchangeWallet wallet : exchangeWalletService.list(query)) {
            map.put(wallet.getId(), wallet);
        }
        logger.error("第二段" + (System.currentTimeMillis() - begin));
        begin = System.currentTimeMillis();
        // 组合响应对象
        List<CoinWalletVo> collect = coins.stream().map(CoinWalletVo::of).peek(vo -> {
            vo.setId(member.getId() + ":" + vo.getCoinUnit());
            vo.setMemberId(member.getId());

            ExchangeWallet wallet = map.get(vo.getId());
            if (wallet != null) {
                vo.copy(wallet);
            }
        }).collect(Collectors.toList());
        logger.error("第三段" + (System.currentTimeMillis() - begin));
        begin = System.currentTimeMillis();
        return success(collect);
    }

    /**
     * 获取指定币种的可用余额
     *
     * @param member   用户
     * @param coinUnit 币种
     * @return 单条数据
     */
    @ApiOperation(value = "查询余额", notes = "获取指定币种的可用余额数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "币种", name = "coinUnit", dataTypeClass = String.class, required = true)
    })
    @RequestMapping(value = "/balance", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<CoinWalletVo> balance(@MemberAccount Member member, @RequestParam("coinUnit") String coinUnit) {

        MessageRespResult<Coin> resp = accountService.getCoin(coinUnit);

        if (resp.isSuccess()) {
            Coin coin = resp.getData();
            CoinWalletVo vo = CoinWalletVo.of(coin);

            Optional<ExchangeWallet> optional = this.exchangeWalletOperations.balance(member.getId(), coinUnit);
            if (optional.isPresent()) {
                ExchangeWallet wallet = optional.get();
                return success(vo.copy(wallet));
            }
            // 没有值
            vo.setId(member.getId() + ":" + coinUnit);
            vo.setMemberId(member.getId());
            return success(vo);
        }

        return failed(CommonMsgCode.of(resp.getCode(), resp.getMessage()));
    }

    /**
     * 资金明细
     *
     * @param member    会员
     * @param dateRange 日期范围
     * @param coinUnit  币种
     * @param type      类型
     * @param current   当前页
     * @param size      每页记录数
     * @return page
     */
    @ApiOperation(value = "资金明细", notes = "获取钱包账户流水日志")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "日期范围 eg. 2019-10-10~2019-10-29 ，不匹配格式的日期不做处理", name = "dateRange", dataTypeClass = String.class),
            @ApiImplicitParam(value = "币种 eg .BT", name = "coinUnit", dataTypeClass = String.class),
            @ApiImplicitParam(value = "类型 1.资金账户划转 8.币币账户划转 9.法币账户划转", name = "type", dataTypeClass = String.class),
            @ApiImplicitParam(value = "方向 0. 未知，不处理 1.转入 2.转出", name = "direction", dataTypeClass = String.class),
            @ApiImplicitParam(value = "当前页", name = "current", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "每页记录数", name = "size", dataTypeClass = Integer.class, required = true),
    })
    @RequestMapping(value = "/wal/records", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<IPage<ExchangeWalletWalRecord>> records(
            @MemberAccount Member member,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            @RequestParam(value = "coinUnit", required = false) String coinUnit,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "direction", required = false) String direct,
            @RequestParam("current") Integer current,
            @RequestParam("size") Integer size) {

        // 构建查询条件
        QueryWrapper<ExchangeWalletWalRecord> query = new QueryWrapper<>();
        query.eq("member_id", member.getId())/*ZERO值对用户没有实际意义*/.ne("trade_balance", BigDecimal.ZERO);

        Tuple2<Date, Date> tuple2 = DateUtils.parseDateRange(dateRange, "~", "yyyy-MM-dd");
        if (tuple2 != null) {
            Date first = DateUtils.getHeadTailOf(tuple2.getFirst()).getFirst();
            Date second = DateUtils.getHeadTailOf(tuple2.getSecond()).getSecond();
            query.ge("create_time", first).lt("create_time", second);
        }

        if (StringUtils.hasText(coinUnit)) {
            query.eq("coin_unit", coinUnit);
        }

        WalTradeType tradeType = WalTradeType.NONE;
        if (StringUtils.hasText(type)) {
            tradeType = WalTradeType.of(NumberUtils.toInt(type, 0));
            if (tradeType != WalTradeType.NONE) {
                query.eq("trade_type", tradeType);
            }
        } else {
            // 只展示 1 划转，7 推荐返佣奖励，8 币币划转， 9 OTC划转，10 活期宝账户划转,11 闪兑, 12 冻结, 13 释放, 14 币币交易奖励 16 币币交易消耗
            query.in("trade_type", 1, 7, 8, 9, 10, 11, 12, 13, 14, 16);
        }

        // 划转方向
        if (StringUtils.hasText(direct)) {
            TransferDirectVo directVo = TransferDirectVo.of(NumberUtils.toInt(direct, 0));

            if (directVo.isAvailable(tradeType)) {
                // 转入 trade_balance > 0
                if (directVo == TransferDirectVo.IN) {
                    query.gt("trade_balance", BigDecimal.ZERO);
                }
                // 转出 trade_balance < 0
                else {
                    query.lt("trade_balance", BigDecimal.ZERO);
                }
            }
        }

        query.orderByDesc("create_time", "id");

        IPage<ExchangeWalletWalRecord> page = exchangeWalletWalRecordService.page(new Page<>(current, size), query);

        return success(page);
    }


    // ------------------------------------
    // > S E T T E R S
    // ------------------------------------

    @Autowired

    public void setExchangeWalletService(ExchangeWalletService exchangeWalletService) {
        this.exchangeWalletService = exchangeWalletService;
    }

    @Autowired
    public void setExchangeWalletOperations(ExchangeWalletOperations exchangeWalletOperations) {
        this.exchangeWalletOperations = exchangeWalletOperations;
    }

    @Autowired
    public void setExchangeWalletWalRecordService(ExchangeWalletWalRecordService exchangeWalletWalRecordService) {
        this.exchangeWalletWalRecordService = exchangeWalletWalRecordService;
    }

    @Autowired
    public void setAccountService(IAccountService accountService) {
        this.accountService = accountService;
    }
}
