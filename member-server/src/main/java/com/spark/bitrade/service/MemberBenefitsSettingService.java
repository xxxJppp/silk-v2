package com.spark.bitrade.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.MemberBenefitsSetting;

/**
 * <p>
 * 会员权益表 服务类
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-18
 */
public interface MemberBenefitsSettingService extends IService<MemberBenefitsSetting> {

    /**
     * 查询权益列表
     * @return
     */
    List<MemberBenefitsSetting> getBenefitsSettingList();

	MemberBenefitsSetting getBenefitsSettingByMemberLevel(int levelId);
}
