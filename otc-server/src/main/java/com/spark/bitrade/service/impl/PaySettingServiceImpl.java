package com.spark.bitrade.service.impl;

import com.spark.bitrade.entity.CurrencyManage;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.PaySetting;
import com.spark.bitrade.mapper.CurrencyManageMapper;
import com.spark.bitrade.mapper.MemberMapper;
import com.spark.bitrade.mapper.PaySettingMapper;
import com.spark.bitrade.service.CurrencyManageService;
import com.spark.bitrade.service.PaySettingService;
import com.spark.bitrade.util.MessageRespResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 支付方式配置 服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2020-03-18
 */
@Service
public class PaySettingServiceImpl extends ServiceImpl<PaySettingMapper, PaySetting> implements PaySettingService {

    @Resource
    private CurrencyManageMapper currencyManageMapper;

    @Override
    public MessageRespResult<List<PaySetting>> getAllOpenPaySetting(Long baseId) {
        QueryWrapper<PaySetting> query = new QueryWrapper<PaySetting>().eq("pay_state", "1");
        if (baseId != -1) { //当查询指定法币支付方式
            CurrencyManage cym = this.currencyManageMapper.selectManageById(baseId);
            if (cym != null && StringUtils.isNotBlank(cym.getPaySetting())) {
                Long[] ids = (Long[]) ConvertUtils.convert(cym.getPaySetting().split(","), Long.class);
                query.in("id", new ArrayList<Long>(Arrays.asList(ids)));
            }
        }
        List<PaySetting> settings = this.baseMapper.selectList(query);
        settings.stream().forEach(each -> {
            each.setAppendFile(null);
            each.setCreateId(null);
            each.setCreateTime(null);
            each.setFileJoinField(null);
            each.setPayState(null);
            each.setUpdateId(null);
            each.setUpdateTime(null);
        });

        return MessageRespResult.success("", settings);
    }

    @Override
    public String getPaySettingByCurrency(CurrencyManage cym) {
        QueryWrapper<PaySetting> query = new QueryWrapper<PaySetting>().eq("pay_state", "1");
        if (cym != null && StringUtils.isNotBlank(cym.getPaySetting())) {
            Long[] ids = (Long[]) ConvertUtils.convert(cym.getPaySetting().split(","), Long.class);
            query.in("id", new ArrayList<>(Arrays.asList(ids)));
        }
        List<PaySetting> settings = this.baseMapper.selectList(query);
        StringBuilder builder = new StringBuilder();
        settings.stream().forEach(each -> {
            if(builder.length() > 0){
                builder.append(",");
            }
            builder.append(each.getPayKey());
        });

        return builder.toString();
    }


}
