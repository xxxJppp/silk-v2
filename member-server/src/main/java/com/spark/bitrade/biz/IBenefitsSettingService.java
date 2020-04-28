package com.spark.bitrade.biz;

import com.spark.bitrade.vo.MemberBenefitsSettingVo;

import java.util.List;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.19 11:47
 */
public interface IBenefitsSettingService {
    /**
     * 获取会员权益数据
     */
    List<MemberBenefitsSettingVo> getBenefitsSettings();


}
