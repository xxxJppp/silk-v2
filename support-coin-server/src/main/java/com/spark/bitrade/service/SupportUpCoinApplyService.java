package com.spark.bitrade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.SupportUpCoinApply;
import com.spark.bitrade.param.PageParam;
import com.spark.bitrade.vo.WidthRechargeStaticsVo;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 扶持上币项目方主表 服务类
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
public interface SupportUpCoinApplyService extends IService<SupportUpCoinApply> {


    SupportUpCoinApply findApprovedUpCoinByMember(Long memberId);

    Map<String,String> findUpCoinText();

    IPage<WidthRechargeStaticsVo> widthDrawToAuditList(Page page, PageParam pageParam, String coinId);

    /**
     * 提现审核中总币数
     * @param coinId
     * @param param
     * @return
     */
    BigDecimal widthDrawToAuditTotal(String coinId, PageParam param);
    /**
     * 提现审核中总人数
     * @param coinId
     * @param param
     * @return
     */
    Integer widthDrawToAuditPersonCount(String coinId, PageParam param);
    /**
     *充值或提现总数
     */
    BigDecimal withRechargeTotal(Integer type, String coin, PageParam param);
    /**
     *已提现或已充值总人数
     */
    Integer withPersonCount(Integer type, String coin, PageParam param);
    /**
     *查询有效用户数
     */
    Integer validPersonCount(String coinUnit);

    /**
     * 有效用户持仓币数
     * @param coinUnit
      * @return
     */
    BigDecimal validHoldCoinCount(String coinUnit);

    Integer hasEntrance(Long id);
}














