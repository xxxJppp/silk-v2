package com.spark.bitrade.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constant.AuditStatusEnum;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constant.SupportCoinMsgCode;
import com.spark.bitrade.entity.SupportUpCoinApply;
import com.spark.bitrade.mapper.SupportUpCoinApplyMapper;
import com.spark.bitrade.param.PageParam;
import com.spark.bitrade.service.IBuyAndSellExchangeOrderCountService;
import com.spark.bitrade.service.ICoinApiService;
import com.spark.bitrade.service.ICoinExchange;
import com.spark.bitrade.service.SupportUpCoinApplyService;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.vo.StatisExchangeOrderDto;
import com.spark.bitrade.vo.WidthRechargeStaticsVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 扶持上币项目方主表 服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
@Service
@Slf4j
public class SupportUpCoinApplyServiceImpl extends ServiceImpl<SupportUpCoinApplyMapper, SupportUpCoinApply> implements SupportUpCoinApplyService {

    @Autowired
    private ICoinExchange coinExchange;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ICoinApiService coinApiService;
    @Value("${support.recharge.amount:10000}")
    private BigDecimal rechargeAmount;
    @Value("${support.exchange.amount:100}")
    private BigDecimal exchangeAmount;
    @Autowired
    private IBuyAndSellExchangeOrderCountService countService;
    @Override
    public SupportUpCoinApply findApprovedUpCoinByMember(Long memberId) {
        QueryWrapper<SupportUpCoinApply> qw = new QueryWrapper<>();
        qw.lambda().eq(SupportUpCoinApply::getMemberId, memberId)
                .eq(SupportUpCoinApply::getDeleteFlag, BooleanEnum.IS_FALSE)
                .eq(SupportUpCoinApply::getAuditStatus, AuditStatusEnum.APPROVED);
        List<SupportUpCoinApply> list = this.list(qw);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(list), SupportCoinMsgCode.IS_NOT_PRJECT_PARTNER);
        SupportUpCoinApply one = list.get(0);
        return one;
    }

    @Override
    public Map<String,String> findUpCoinText() {
        return baseMapper.findUpCoinText();
    }

    @Override
    public IPage<WidthRechargeStaticsVo> widthDrawToAuditList(Page page, PageParam pageParam, String coinId) {
        pageParam.transTime();
        List<WidthRechargeStaticsVo> p=baseMapper.widthDrawToAuditList(page,pageParam,coinId);
        page.setRecords(p);
        return page;
    }

    @Override
    public BigDecimal widthDrawToAuditTotal(String coinId, PageParam param) {
        return this.baseMapper.widthDrawToAuditTotal(coinId,param);
    }

    @Override
    public Integer widthDrawToAuditPersonCount(String coinId, PageParam param) {
        return this.baseMapper.widthDrawToAuditPersonCount(coinId,param);
    }

    @Override
    public BigDecimal withRechargeTotal(Integer type, String coin, PageParam param) {
        return this.baseMapper.withRechargeTotal(type,coin,param );
    }

    @Override
    public Integer withPersonCount(Integer type, String coin, PageParam param) {
        return this.baseMapper.withPersonCount(type,coin,param);
    }

    @Override
    public Integer validPersonCount(String coinUnit) {
        String key="SUPPORT:VALIDPERSON:COUNT:" + coinUnit;
        ValueOperations<String,Integer> op = redisTemplate.opsForValue();
        Integer v = op.get(key);
        if(v!=null){
            return v;
        }
        Integer count = getValidPersons(coinUnit).size();
        op.set(key,count,12, TimeUnit.HOURS);
        return count;
    }

    /**
     * 获取有效用户ids
     * @return
     */
    private Set<Long> getValidPersons(String coinUnit){
//        QueryWrapper<SupportUpCoinApply> aq=new QueryWrapper<>();
//        aq.lambda().eq(SupportUpCoinApply::getCoin,coinUnit)
//                .eq(SupportUpCoinApply::getAuditStatus,AuditStatusEnum.APPROVED)
//                .eq(SupportUpCoinApply::getDeleteFlag,BooleanEnum.IS_FALSE);
//        List<SupportUpCoinApply> apply = this.list(aq);
        //币种审核通过时间
//        Date createTime=new Date();
//        Long memberId=0L;
//        if(!CollectionUtils.isEmpty(apply)){
//            createTime = apply.get(0).getCreateTime();
//            memberId=apply.get(0).getMemberId();
//        }
        MessageRespResult<BigDecimal> rate = coinExchange.getUsdExchangeRate(coinUnit);
        AssertUtil.isTrue(rate.isSuccess(),SupportCoinMsgCode.HUILV_FIND_FAILED);
        //充值有效用户 去掉充值拉新的
//        List<ValidPersonVo> rechargePersons = baseMapper.rechargePerson(coinUnit, rechargeAmount);
        //币币交易有效用户
        Set<Long> exMeIds = new HashSet<>();
        if(rate.getData().compareTo(BigDecimal.ZERO)>0){
            BigDecimal exchangeValue = exchangeAmount.divide(rate.getData(),8, RoundingMode.HALF_UP);
            exMeIds=exchangeValidPersons(coinUnit,exchangeValue);
        }
        //去重
//        Set<Long> reMeIds = rechargePersons.stream().map(r -> r.getMemberId()).collect(Collectors.toSet());
//        reMeIds.addAll(exMeIds);
//        reMeIds.remove(memberId);
//        reMeIds.removeAll(Collections.singleton(null));
        return exMeIds;
    }

    private Set<Long> exchangeValidPersons(String coinUnit,BigDecimal exchangeAmount){
        Object data=null;
        try {
            MessageRespResult messageRespResult = countService.orderStats(coinUnit,null,null,null);
            data = messageRespResult.getData();
        }catch (Exception e){
            log.error("调用币币交易报错,{}", ExceptionUtils.getStackTrace(e));
        }
        List<StatisExchangeOrderDto> dtos = JSONArray.parseArray(JSON.toJSONString(data), StatisExchangeOrderDto.class);
        Map<Long,BigDecimal> dds=new HashMap<>();
        Set<Long> memberIds=new HashSet<>();
        if(!CollectionUtils.isEmpty(dtos)){
            for (StatisExchangeOrderDto dto:dtos){
                BigDecimal decimal = Optional.ofNullable(dds.get(dto.getMemberId())).orElse(BigDecimal.ZERO);
                decimal=decimal.add(Optional.ofNullable(dto.getTradeTurnover()).orElse(BigDecimal.ZERO));
                dds.put(dto.getMemberId(),decimal);
            }
            Set<Map.Entry<Long, BigDecimal>> entries = dds.entrySet();
            for (Map.Entry<Long, BigDecimal> entry:entries){
                Long m = entry.getKey();
                BigDecimal total = entry.getValue();
                if (total.compareTo(exchangeAmount)>=0){
                    memberIds.add(m);
                }
            }
        }
        return memberIds;

    }

    public static void main(String[] args) {
        List<StatisExchangeOrderDto> dtos = JSONArray.parseArray(JSON.toJSONString(null), StatisExchangeOrderDto.class);
    }

    /**
     * 有效用户持仓币数
     * @param coinUnit
     * @return
     */
    @Override
    @Cacheable(cacheNames = "SUPPORT",key = "'support:validHoldCoinCount:'+#coinUnit")
    public BigDecimal validHoldCoinCount(String coinUnit) {
        Set<Long> validPersons = getValidPersons(coinUnit);
        if(CollectionUtils.isEmpty(validPersons)){
            return BigDecimal.ZERO;
        }
        MessageRespResult<String> coinNameByUnit = coinApiService.getCoinNameByUnit(coinUnit);
        String coinId = coinNameByUnit.getData();
        return baseMapper.validHoldCoinCount(coinId,validPersons);
    }

    @Override
    @Cacheable(cacheNames = "SUPPORT",key = "'support:hasEntrance:'+#memberId")
    public Integer hasEntrance(Long memberId) {
        Integer en=baseMapper.hasEntrance(memberId);
        Integer res = Optional.ofNullable(en).orElse(0);
        return res;
    }


}























