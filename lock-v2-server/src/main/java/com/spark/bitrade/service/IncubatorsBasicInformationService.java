package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.dto.IncubatorsBasicInformationDto;
import com.spark.bitrade.dto.IncubatorsEntranceDto;
import com.spark.bitrade.entity.IncubatorsBasicInformation;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.form.UpCoinForm;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 孵化区-上币申请表 服务类
 * </p>
 *
 * @author qiliao
 * @since 2019-08-30
 */
public interface IncubatorsBasicInformationService extends IService<IncubatorsBasicInformation> {
    /**
     * 申请上币
     *
     * @param member
     * @param form
     * @param coinUnit
     * @param lockAmount
     */
    void upCoinApply(Member member, UpCoinForm form, String coinUnit, BigDecimal lockAmount);


    /**
     * 退出上币
     *
     * @param member
     * @param reason
     * @param information
     */
    void exitCoinApply(Member member, String reason, IncubatorsBasicInformation information);

    /**
     * 审核详情
     *
     * @param memberId 会员信息
     * @return
     */
    IncubatorsBasicInformationDto getIncubatorsBasicInformationByMemberId(Long memberId);

    /**
     * 入口
     *
     * @param memberId 会员信息
     * @return
     */
    IncubatorsEntranceDto getMemberStatus(Long memberId);

    /**
     * 修改孵化区申请表锁仓数量
     *
     * @param id  id
     * @param num 锁仓数量
     * @return
     */
    Integer updateIncubatorsBasicInformation(Long id, BigDecimal num);
}
