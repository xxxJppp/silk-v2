package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.spark.bitrade.entity.AmountOfDiscountRecord;
import com.spark.bitrade.entity.CurrencyRuleSetting;
import com.spark.bitrade.enums.OtcExceptionMsg;
import com.spark.bitrade.mapper.AmountOfDiscountRecordMapper;
import com.spark.bitrade.mapper.CurrencyRuleSettingMapper;
import com.spark.bitrade.service.AmountOfDiscountRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.service.IMemberBenefitsService;
import com.spark.bitrade.util.AssertUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 经纪人优惠兑币限额记录(AmountOfDiscountRecord)表服务实现类
 *
 * @author ss
 * @date 2020-04-08 15:58:56
 */
@Service("amountOfDiscountRecordService")
public class AmountOfDiscountRecordServiceImpl extends ServiceImpl<AmountOfDiscountRecordMapper,AmountOfDiscountRecord> implements AmountOfDiscountRecordService {
    @Resource
    private AmountOfDiscountRecordMapper amountOfDiscountRecordMapper;
    @Resource
    private IMemberBenefitsService memberLevelService;
    @Resource
    private CurrencyRuleSettingMapper currencyRuleSettingMapper;


    @Override
    public AmountOfDiscountRecord getByMemberId(Long memberId) {
        //获取用户等级
        AssertUtil.isTrue(memberLevelService.getMemberLevelByMember(memberId).getData(), OtcExceptionMsg.NOT_AGENT);
        AmountOfDiscountRecord amountOfDiscountRecord = amountOfDiscountRecordMapper.selectOne(new LambdaQueryWrapper<AmountOfDiscountRecord>().eq(AmountOfDiscountRecord::getMemberId,memberId));
        if(amountOfDiscountRecord == null){
            //创建优惠限额记录
            amountOfDiscountRecord = new AmountOfDiscountRecord();
            //获取优惠配置
            CurrencyRuleSetting currencyRuleSetting = currencyRuleSettingMapper.selectOne(new LambdaQueryWrapper<CurrencyRuleSetting>().eq(CurrencyRuleSetting::getRuleKey,"AGENT_PAY_USDC_MAX"));
            AssertUtil.notNull(currencyRuleSetting,OtcExceptionMsg.NOT_AGENT_PAY_USDC_MAX);
            //总额度
            amountOfDiscountRecord.setTotalAmountOfDiscount(new BigDecimal(currencyRuleSetting.getRuleValue()));
            //创建时间
            amountOfDiscountRecord.setCreateTime(new Date());
            //memberId
            amountOfDiscountRecord.setMemberId(memberId);
            //剩余额度
            amountOfDiscountRecord.setRemainingAmountOfDiscount(amountOfDiscountRecord.getTotalAmountOfDiscount());
            //已使用额度
            amountOfDiscountRecord.setUsedAmountOfDiscount(BigDecimal.ZERO);
            //更新日期
            amountOfDiscountRecord.setUpdateTime(new Date());
            AssertUtil.isTrue(amountOfDiscountRecordMapper.insert(amountOfDiscountRecord) > 0,OtcExceptionMsg.CREATE_AGENT_PAY_USDC_MAX_FAIL);
        }
        return amountOfDiscountRecord;
    }

    @Override
    public int updateMemberDiscount(Long memberId, BigDecimal discountPart) {
        return amountOfDiscountRecordMapper.updateMemberDiscount(memberId,discountPart,new Date());
    }
}