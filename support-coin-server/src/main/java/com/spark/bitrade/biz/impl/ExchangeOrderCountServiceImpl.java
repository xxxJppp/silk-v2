package com.spark.bitrade.biz.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.biz.IExchangeOrderCountService;
import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constant.ExchangeOrderType;
import com.spark.bitrade.constant.SupportCoinMsgCode;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.SupportUpCoinApply;
import com.spark.bitrade.param.ExchangeOrderParam;
import com.spark.bitrade.service.*;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.DateUtil;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.vo.ExchangeOrderListVo;
import com.spark.bitrade.vo.ExchangeOrderStaticsVo;
import com.spark.bitrade.vo.ExchangeOrderStats;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.13 09:29
 */
@Service
@Slf4j
public class ExchangeOrderCountServiceImpl implements IExchangeOrderCountService {

    @Autowired
    private IBuyAndSellExchangeOrderCountService buyAndSellExchangeOrderCountService;

    @Autowired
    private SupportUpCoinApplyService upCoinApplyService;
    @Autowired
    private IMemberApiService memberApiService;
    @Autowired
    private ICoinExchange coinExchange;
    @Autowired
    private SupportCoinMatchService supportCoinMatchService;


    private Long[] timeArr(String startTime,String endTime){
        Long start=null;
        Long end=null;
        if (StringUtils.isNotBlank(startTime)){
            Date s = DateUtil.stringToDate(startTime, "yyyy-MM-dd");
            start=s.getTime();
        }
        if (StringUtils.isNotBlank(endTime)){
            Date e = DateUtil.stringToDate(endTime, "yyyy-MM-dd");
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(e);
            calendar.set(Calendar.HOUR_OF_DAY,23);
            calendar.set(Calendar.MINUTE,59);
            calendar.set(Calendar.SECOND,59);
            end=calendar.getTime().getTime();
        }
        return new Long[]{start,end};
    }

    @Override
    public IPage<ExchangeOrderListVo> exchangeOrders(Member member, ExchangeOrderParam param) {
        Long[] times = timeArr(param.getStartTime(), param.getEndTime());
        SupportUpCoinApply apply = upCoinApplyService.findApprovedUpCoinByMember(member.getId());
        IPage<ExchangeOrderListVo> page = new Page<>(param.getPage(), param.getPageSize());
        MessageRespResult orderInfo = null;
        try {
            orderInfo=buyAndSellExchangeOrderCountService.exchangeHistoryOrders(param.getPage(), param.getPageSize(),null,apply.getCoin(),
                    param.getDirection(),param.getStatus(),times[0],times[1]);
        }catch (Exception e){
            log.error("调用接口失败:{}",e.getMessage());
            orderInfo=MessageRespResult.error("调用接口失败");
        }
        log.info("=============结果为:{}============", JSON.toJSONString(orderInfo));
        if(!orderInfo.isSuccess()){
            return new Page<>();
        }
        Page remotePage = JSON.parseObject(JSON.toJSONString(orderInfo.getData()), Page.class);
        page.setCurrent(remotePage.getCurrent());
        page.setTotal(remotePage.getTotal());
        page.setPages(remotePage.getPages());
        page.setSize(remotePage.getSize());
        List<ExchangeOrderListVo> resList=new ArrayList<>();
        List<ExchangeOrder> exchangeOrders = JSONArray.parseArray(JSON.toJSONString(remotePage.getRecords()), ExchangeOrder.class);
        String buyType = param.getDirection() == ExchangeOrderDirection.BUY ? "买单" : "卖单";
        if(!CollectionUtils.isEmpty(exchangeOrders)){
            List<Long> ids = exchangeOrders.stream().map(e -> e.getMemberId()).collect(Collectors.toList());
            Map<Long,String> map=new HashMap<>();
            try {
                MessageRespResult<List<Member>> listMessageRespResult = memberApiService.listMembersByIds(ids);
                List<Member> data = listMessageRespResult.getData();
                for (Member m:data){
                    map.put(m.getId(),m.getUsername());
                }
            }catch (Exception e){
                log.error("调用会员接口失败");
            }

            ExchangeOrderListVo vo;
            for (ExchangeOrder order:exchangeOrders){
                vo=new ExchangeOrderListVo();
                vo.setMemberId(order.getMemberId());
                vo.setUserName(map.get(order.getMemberId()));
                vo.setCoinMatch(order.getSymbol());
                ExchangeOrderType type = order.getType();
                if (type==ExchangeOrderType.LIMIT_PRICE){
                    vo.setDeityType(String.format("%s%s","限价",buyType));
                }
                if (type==ExchangeOrderType.MARKET_PRICE){
                    vo.setDeityType(String.format("%s%s","市价",buyType));
                }

                vo.setDeityTime(order.getTime());
                vo.setDeityPrice(order.getPrice());
                vo.setDealTime(order.getCompletedTime());
                BigDecimal tradedAmount = order.getTradedAmount();
                BigDecimal turnover = Optional.ofNullable(order.getTurnover()).orElse(BigDecimal.ZERO);
                if(tradedAmount!=null&&tradedAmount.compareTo(BigDecimal.ZERO)>0){
                    vo.setDealPrice(turnover.divide(tradedAmount,4, RoundingMode.HALF_UP));
                }
                vo.setDealNum(order.getTradedAmount());
                vo.setNoDealNum(order.getAmount().subtract(order.getTradedAmount()));
                resList.add(vo);
            }
        }
        page.setRecords(resList);
        return page;
    }


    @Cacheable(cacheNames = "supportExchangeOrdersCount",key = "'SUPPORT:exchangeOrdersCount:member:'+#memberId+':time:'+#startTime+#endTime")
    public ExchangeOrderStaticsVo exchangeOrdersCount(Long memberId, String startTime, String endTime){
        ExchangeOrderStaticsVo exchangeOrderStaticsVo = new ExchangeOrderStaticsVo();
        SupportUpCoinApply apply = upCoinApplyService.findApprovedUpCoinByMember(memberId);
        Integer integer = upCoinApplyService.validPersonCount(apply.getCoin());
        //有效用户
        exchangeOrderStaticsVo.setValidPersons(integer);
        Long[] times = timeArr(startTime, endTime);
        //买卖盘总量 单位已换算为USDT[0] 买盘总量 [1] 卖盘总量
        BigDecimal[] totals = platTotal(apply.getCoin());
        //市价统计 已成交的
        marketPrice(apply.getCoin(),exchangeOrderStaticsVo,times);
        //限价统计 已成交的
        limitPrice(apply.getCoin(),exchangeOrderStaticsVo,times);
        exchangeOrderStaticsVo.setSellLimitNoTradedTotal(totals[1].setScale(4,RoundingMode.HALF_UP));
        exchangeOrderStaticsVo.setBuyLimitNoTradedTotal(totals[0].setScale(4,RoundingMode.HALF_UP));

        return exchangeOrderStaticsVo;
    }

    /**
     *
     * @param coin
     * @param exchangeOrderStaticsVo
     * @return
     */
    private ExchangeOrderStaticsVo marketPrice(String coin,ExchangeOrderStaticsVo exchangeOrderStaticsVo,Long[] times){
        List<ExchangeOrderStats> stats=remoteOrderStatsList(coin,ExchangeOrderType.MARKET_PRICE,times);

        if(CollectionUtils.isEmpty(stats)){
            return exchangeOrderStaticsVo;
        }
        //买盘已成交
        Map<String, BigDecimal> buyMas=new HashMap<>();
        //卖盘已成交
        Map<String, BigDecimal> sellMas=new HashMap<>();
        for (ExchangeOrderStats st:stats){
            String baseSymbol = st.getBaseSymbol();
            BigDecimal tu2 = Optional.ofNullable(st.getTradeTurnover()).orElse(BigDecimal.ZERO);
            if(st.getDirection()==ExchangeOrderDirection.BUY){
                BigDecimal tu = buyMas.get(baseSymbol);
                tu=tu==null?BigDecimal.ZERO:tu;
                buyMas.remove(baseSymbol);
                buyMas.put(baseSymbol,tu.add(tu2));
            }
            if(st.getDirection()==ExchangeOrderDirection.SELL){
                BigDecimal se = sellMas.get(baseSymbol);
                se=se==null?BigDecimal.ZERO:se;
                sellMas.remove(baseSymbol);
                sellMas.put(baseSymbol,se.add(tu2));
            }

        }
        //买盘已成交 换算USDT
        BigDecimal buyTotal=BigDecimal.ZERO;
        //卖盘已成交 换算USDT
        BigDecimal sellTotal=BigDecimal.ZERO;
        Set<Map.Entry<String, BigDecimal>> buyEntries = buyMas.entrySet();
        for (Map.Entry<String,BigDecimal> entry:buyEntries){
            String key = entry.getKey();
            BigDecimal value = entry.getValue();
            MessageRespResult<BigDecimal> usdExchangeRate = coinExchange.getUsdExchangeRate(key);
            AssertUtil.isTrue(usdExchangeRate.isSuccess(), SupportCoinMsgCode.HUILV_FIND_FAILED);
            AssertUtil.isTrue(usdExchangeRate.getData().compareTo(BigDecimal.ZERO)>0,SupportCoinMsgCode.USDT_RATE_GET_FAILED);
            buyTotal=buyTotal.add(value.multiply(usdExchangeRate.getData()));
        }

        Set<Map.Entry<String, BigDecimal>> sellEntries = sellMas.entrySet();
        for (Map.Entry<String,BigDecimal> entry:sellEntries){
            String key = entry.getKey();
            BigDecimal value = entry.getValue();
            MessageRespResult<BigDecimal> usdExchangeRate = coinExchange.getUsdExchangeRate(key);
            AssertUtil.isTrue(usdExchangeRate.isSuccess(), SupportCoinMsgCode.HUILV_FIND_FAILED);
            AssertUtil.isTrue(usdExchangeRate.getData().compareTo(BigDecimal.ZERO)>0,SupportCoinMsgCode.USDT_RATE_GET_FAILED);
            sellTotal=sellTotal.add(value.multiply(usdExchangeRate.getData()));
        }
        exchangeOrderStaticsVo.setBuyMarketTradedTotal(buyTotal.setScale(4,RoundingMode.HALF_UP));
        exchangeOrderStaticsVo.setSellMarketTradedTotal(sellTotal.setScale(4,RoundingMode.HALF_UP));
        return exchangeOrderStaticsVo;

    }

    /**
     * 限价
     * @param coin
     * @param exchangeOrderStaticsVo
     * @return
     */
    private ExchangeOrderStaticsVo limitPrice(String coin,ExchangeOrderStaticsVo exchangeOrderStaticsVo,Long[] times){

        List<ExchangeOrderStats> stats=remoteOrderStatsList(coin,ExchangeOrderType.LIMIT_PRICE,times);
        if(CollectionUtils.isEmpty(stats)){
            return exchangeOrderStaticsVo;
        }
        //买盘已成交
        Map<String, BigDecimal> buyMas=new HashMap<>();
        //卖盘已成交
        Map<String, BigDecimal> sellMas=new HashMap<>();
        for (ExchangeOrderStats st:stats){
            String baseSymbol = st.getBaseSymbol();
            BigDecimal tover = Optional.ofNullable(st.getTradeTurnover()).orElse(BigDecimal.ZERO);
            if(st.getDirection()==ExchangeOrderDirection.BUY){
                BigDecimal tu = buyMas.get(baseSymbol);
                tu=tu==null?BigDecimal.ZERO:tu;
                buyMas.remove(baseSymbol);
                buyMas.put(baseSymbol,tu.add(tover));
            }
            if(st.getDirection()==ExchangeOrderDirection.SELL){
                BigDecimal se = sellMas.get(baseSymbol);
                se=se==null?BigDecimal.ZERO:se;
                sellMas.remove(baseSymbol);
                sellMas.put(baseSymbol,se.add(tover));
            }

        }
        //买盘已成交 换算USDT
        BigDecimal buyTotal=BigDecimal.ZERO;
        //卖盘已成交 换算USDT
        BigDecimal sellTotal=BigDecimal.ZERO;
        Set<Map.Entry<String, BigDecimal>> buyEntries = buyMas.entrySet();
        for (Map.Entry<String,BigDecimal> entry:buyEntries){
            String key = entry.getKey();
            BigDecimal value = entry.getValue();
            MessageRespResult<BigDecimal> usdExchangeRate = coinExchange.getUsdExchangeRate(key);
            AssertUtil.isTrue(usdExchangeRate.isSuccess(), SupportCoinMsgCode.HUILV_FIND_FAILED);
            AssertUtil.isTrue(usdExchangeRate.getData().compareTo(BigDecimal.ZERO)>0,SupportCoinMsgCode.USDT_RATE_GET_FAILED);
            buyTotal=buyTotal.add(value.multiply(usdExchangeRate.getData()));
        }

        Set<Map.Entry<String, BigDecimal>> sellEntries = sellMas.entrySet();
        for (Map.Entry<String,BigDecimal> entry:sellEntries){
            String key = entry.getKey();
            BigDecimal value = entry.getValue();
            MessageRespResult<BigDecimal> usdExchangeRate = coinExchange.getUsdExchangeRate(key);
            AssertUtil.isTrue(usdExchangeRate.isSuccess(), SupportCoinMsgCode.HUILV_FIND_FAILED);
            AssertUtil.isTrue(usdExchangeRate.getData().compareTo(BigDecimal.ZERO)>0,SupportCoinMsgCode.USDT_RATE_GET_FAILED);
            sellTotal=sellTotal.add(value.multiply(usdExchangeRate.getData()));
        }
        exchangeOrderStaticsVo.setBuyLimitTradedTotal(buyTotal.setScale(4,RoundingMode.HALF_UP));
        exchangeOrderStaticsVo.setSellLimitTradedTotal(sellTotal.setScale(4,RoundingMode.HALF_UP));
        return exchangeOrderStaticsVo;
    }

    /**
     * [0] 买盘总量
     * [1] 卖盘总量
     * @param coin
     * @return
     */
    private BigDecimal[] platTotal(String coin){
        //相对于USDT的费率
        MessageRespResult<BigDecimal> usdExchangeRate = coinExchange.getUsdExchangeRate(coin);
        AssertUtil.isTrue(usdExchangeRate.isSuccess(), SupportCoinMsgCode.HUILV_FIND_FAILED);
        AssertUtil.isTrue(usdExchangeRate.getData().compareTo(BigDecimal.ZERO)>0,SupportCoinMsgCode.USDT_RATE_GET_FAILED);
        BigDecimal fee=usdExchangeRate.getData();
        List<String> coinMath = supportCoinMatchService.findByCoinUnit(coin);
        //买盘总的 单位项目币种
        BigDecimal buyTotal=BigDecimal.ZERO;
        //卖盘总的 单位项目币种
        BigDecimal sellTotal=BigDecimal.ZERO;
        Set<String> keySet = new HashSet<>(coinMath);
        for (String set:keySet){
            MessageRespResult resp = null;
            try {
                resp = coinExchange.tradePlateTotal(set);
            }catch (Exception e){
                log.info("调用盘口数据失败");
                resp=MessageRespResult.error("调用盘口数据失败");
            }
            if(resp.isSuccess()){
                JSONObject object = JSON.parseObject(JSON.toJSONString(resp.getData()));
                Object askTotal = object.get("askTotal");
                Object bidTotal = object.get("bidTotal");
                if(askTotal!=null){
                    sellTotal=sellTotal.add(new BigDecimal(askTotal.toString()));
                }
                if(bidTotal!=null){
                    buyTotal=buyTotal.add(new BigDecimal(bidTotal.toString()));
                }
            }
        }
        //usdt换算


        return new BigDecimal[]{buyTotal.multiply(fee),sellTotal.multiply(fee)};
    }

    /**
     * 调用统计接口
     * @param coin
     * @return
     */
    private List<ExchangeOrderStats> remoteOrderStatsList(String coin,ExchangeOrderType type,Long[] times){
        MessageRespResult messageRespResult =null;
        try {
            messageRespResult = buyAndSellExchangeOrderCountService.orderStats(coin,type,times[0],times[1]);
        }catch (Exception e){
            log.info("调用统计接口失败");
            messageRespResult=MessageRespResult.error("调用接口失败");
        }
        //调用失败直接返回
        if(!messageRespResult.isSuccess()){
            return new ArrayList<>();
        }
        List<ExchangeOrderStats> stats = JSONArray.parseArray(JSON.toJSONString(messageRespResult.getData()), ExchangeOrderStats.class);
        return stats;
    }
}

















