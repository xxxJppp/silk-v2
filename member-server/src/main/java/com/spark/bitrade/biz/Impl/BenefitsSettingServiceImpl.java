package com.spark.bitrade.biz.Impl;

import com.spark.bitrade.biz.IBenefitsSettingService;
import com.spark.bitrade.entity.MemberBenefitsSetting;
import com.spark.bitrade.entity.MemberLevel;
import com.spark.bitrade.service.MemberBenefitsSettingService;
import com.spark.bitrade.service.MemberLevelService;
import com.spark.bitrade.vo.MemberBenefitsSettingVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.19 11:49
 */
@Service
public class BenefitsSettingServiceImpl implements IBenefitsSettingService {

    @Autowired
    private MemberBenefitsSettingService benefitsSettingService;

    @Autowired
    private MemberLevelService levelService;

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<MemberBenefitsSettingVo> getBenefitsSettings() {
        List<MemberBenefitsSettingVo> result = new ArrayList<>();
        List<MemberBenefitsSetting> list = benefitsSettingService.getBenefitsSettingList();
        for (MemberBenefitsSetting setting : list) {
            MemberBenefitsSettingVo vo = new MemberBenefitsSettingVo();
            BeanUtils.copyProperties(setting, vo);
            MemberLevel level = levelService.getById(setting.getLevelId());
            vo.setLevelNameZh(level.getNameZh());
            vo.setLevelNameEn(level.getNameEn());
            vo.setLevelNameHk(level.getNameZhTw());
            vo.setLevelNameKo(level.getNameKo());
            result.add(vo);
            vo.setAgentBuyDiscount(vo.getAgentBuyDiscount().stripTrailingZeros());
            vo.setEntrustSellDiscount(vo.getEntrustSellDiscount().stripTrailingZeros());
            vo.setAgentLockDiscount(vo.getAgentLockDiscount().stripTrailingZeros());
            vo.setEntrustBuyDiscount(vo.getEntrustBuyDiscount().stripTrailingZeros());
        }
        return result;
    }
}
