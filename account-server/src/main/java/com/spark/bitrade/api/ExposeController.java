package com.spark.bitrade.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.spark.bitrade.api.vo.AssetsVo;
import com.spark.bitrade.api.vo.TransferVo;
import com.spark.bitrade.constant.WalletType;
import com.spark.bitrade.constants.AcctMsgCode;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.controller.ApiController;
import com.spark.bitrade.entity.Coin;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.MemberWallet;
import com.spark.bitrade.service.CoinService;
import com.spark.bitrade.service.IExchangeV2Service;
import com.spark.bitrade.service.IOtcServer;
import com.spark.bitrade.service.MemberWalletService;
import com.spark.bitrade.support.RateManager;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.BigDecimalUtil;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.vo.WalletAssetsVo;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * ExposeController
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/11/21 15:17
 */
@RestController
@RequestMapping("api/v2")
@Api(tags = "ACT标准对外接口")
public class ExposeController extends ApiController {

    private CoinService coinService;
    private RateManager rateManager;
    private MemberWalletService memberWalletService;

    private IExchangeV2Service exchangeV2Service;
   // @Resource
   // private IOtcServer iOtcServer;

    /**
     * 转账接口
     * <p>
     * 该接口为中转接口
     *
     * @param member     会员信息
     * @param transferVo 转账信息
     * @return resp refId
     */
    @ApiOperation(value = "转账接口", notes = "提供账户间的资金划转")
    @ApiImplicitParam(value = "实体对象", name = "TransferVo", dataTypeClass = TransferVo.class)
    @RequestMapping(value = "/transfer", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<String> transfer(@MemberAccount Member member, TransferVo transferVo) {

        // 账户不匹配
        if (member == null || !member.getId().equals(transferVo.getMemberId())) {
            throw AcctMsgCode.TRANS_ACCOUNT_NOT_MATCH.asException();
        }

        // 必须正数
        AssertUtil.isTrue(BigDecimalUtil.gt0(transferVo.getAmount()), AcctMsgCode.ILLEGAL_TRANS_AMOUNT);

        // 精度 8
        AssertUtil.isTrue(transferVo.getAmount().scale() <= 8, AcctMsgCode.ILLEGAL_TRANS_AMOUNT);

        // 方向
        WalletType from = transferVo.getFrom();
        WalletType to = transferVo.getTo();

        // 币币账户划转
        if (from == WalletType.EXCHANGE || to == WalletType.EXCHANGE) {
            MessageRespResult<String> transfer = exchangeV2Service.transfer(member.getId(), transferVo.getCoinUnit(), from, to, transferVo.getAmount());
            if (transfer.isSuccess()) {
                return success(transfer.getData());
            }
            return failed(CommonMsgCode.of(transfer.getCode(), transfer.getMessage()));
        }

        // TODO 其他账户不支持
        return failed(CommonMsgCode.FAILURE);
    }

    
    
    @ApiOperation(value = "资产接口", notes = "提供账户间资产汇总列表")
    @RequestMapping(value = "/assets", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<List<AssetsVo>> assets(@MemberAccount Member member) {

        List<AssetsVo> list = new ArrayList<>();

        //String fbUnit = iOtcServer.getMemberCurrencyUnitById(member.getId()).replaceAll("\"", "");
        // 获取币币账户资产
        MessageRespResult<List<WalletAssetsVo>> exResp = exchangeV2Service.assets(member.getId());
        if (exResp.isSuccess()) {
            List<WalletAssetsVo> data = exResp.getData();

            AssetsVo ex = new AssetsVo(WalletType.EXCHANGE, "币币交易账户", BigDecimal.ZERO, BigDecimal.ZERO);

            if (data != null) {
                for (WalletAssetsVo datum : data) {
                    String coinUnit = datum.getCoinUnit();
                    RateManager.CoinRate coinRate = rateManager.get(coinUnit);

                    BigDecimal usdt = datum.getTotal().multiply(coinRate.getUsd()).setScale(8, BigDecimal.ROUND_DOWN);
                    BigDecimal cny = datum.getTotal().multiply(coinRate.getCny()).setScale(8, BigDecimal.ROUND_DOWN);
                  /*将原有汇率更新为对应法币汇率
                    if(!StringUtils.isEmpty(fbUnit)) {
                    	MessageRespResult fcResult = iOtcServer.getCurrencyRate(fbUnit, coinUnit);
                		if(fcResult.isSuccess()) {
                			Double rate = (Double) fcResult.getData();
                			cny = new BigDecimal(rate);
                		}
                    }*/
                    ex.add(usdt).cny(cny);
                }
            }
            list.add(ex);
        }


        // 统计资金账户资产

        Map<String, Coin> coinMap = new HashMap<>();
        for (Coin coin : coinService.list()) {
            coinMap.put(coin.getName(), coin);
        }
        final AssetsVo fund = new AssetsVo(WalletType.FUND, "资金账户", BigDecimal.ZERO, BigDecimal.ZERO);
        QueryWrapper<MemberWallet> query = new QueryWrapper<>();
        query.eq("member_id", member.getId());
        memberWalletService.list(query).stream().map(wallet -> {
            WalletAssetsVo vo = new WalletAssetsVo();
            vo.setMemberId(wallet.getMemberId());
            vo.setCoinUnit(coinMap.get(wallet.getCoinId()).getUnit());
            vo.setBalance(wallet.getBalance());
            vo.setFrozen(wallet.getFrozenBalance());
            vo.setLocked(wallet.getLockBalance());

            return vo;
        }).forEach(datum -> {
            String coinUnit = datum.getCoinUnit();
            RateManager.CoinRate coinRate = rateManager.get(coinUnit);

            BigDecimal cny = datum.getTotal().multiply(coinRate.getCny()).setScale(8, BigDecimal.ROUND_DOWN);
          /*将原有汇率更新为对应法币汇率
            if(!StringUtils.isEmpty(fbUnit)) {
            	MessageRespResult fcResult = iOtcServer.getCurrencyRate(fbUnit, coinUnit);
        		if(fcResult.isSuccess()) {
        			Double rate = (Double) fcResult.getData();
        			cny = new BigDecimal(rate);
        		}
            }*/
            BigDecimal usdt = datum.getTotal().multiply(coinRate.getUsd()).setScale(8, BigDecimal.ROUND_DOWN);
            fund.cny(cny).add(usdt);
        });
        list.add(fund);


        return success(list);
    }

    @ApiOperation(value = "汇率接口", notes = "返回当前缓存的币种对应汇率")
    @RequestMapping(value = "/rates", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<Map<String, RateManager.CoinRate>> rate() {
        return success(rateManager.rate());
    }

    @Autowired
    public void setCoinService(CoinService coinService) {
        this.coinService = coinService;
    }

    @Autowired
    public void setRateManager(RateManager rateManager) {
        this.rateManager = rateManager;
    }

    @Autowired
    public void setMemberWalletService(MemberWalletService memberWalletService) {
        this.memberWalletService = memberWalletService;
    }

    @Autowired
    public void setExchangeV2Service(IExchangeV2Service exchangeV2Service) {
        this.exchangeV2Service = exchangeV2Service;
    }

}
