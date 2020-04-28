package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.entity.SupportUpCoinApply;
import com.spark.bitrade.param.PageParam;
import com.spark.bitrade.vo.ValidPersonVo;
import com.spark.bitrade.vo.WidthRechargeStaticsVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 扶持上币项目方主表 Mapper 接口
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
public interface SupportUpCoinApplyMapper extends BaseMapper<SupportUpCoinApply> {

    @Select("select content,content_ko contentKo,content_zh_tw contentZhTw,content_en contentEn from agreement where id=28")
    Map<String,String> findUpCoinText();

    /**
     * 查询提币审核列表
     * @param page
     * @param pageParam
     * @param coinId
     * @return
     */
    List<WidthRechargeStaticsVo> widthDrawToAuditList(@Param("page") Page page,
                                                      @Param("pageParam") PageParam pageParam,
                                                      @Param("coinId")String coinId);

    /**
     * 提现审核中总币数
     * @param coinId
     * @return
     */
    BigDecimal widthDrawToAuditTotal(@Param("coinId")String coinId, @Param("pageParam") PageParam param);
    /**
     * 提现审核中总人数
     * @param coinId
     * @return
     */
    Integer widthDrawToAuditPersonCount(@Param("coinId")String coinId, @Param("pageParam") PageParam pageParam);
    /**
     *充值或提现总数
     */
    BigDecimal withRechargeTotal(@Param("type") Integer type, @Param("coin") String coin, @Param("pageParam") PageParam param);
    /**
     *已提现或已充值总人数
     */
    Integer withPersonCount(@Param("type")Integer type, @Param("coin")String coin, @Param("pageParam") PageParam pageParam);


    @Select(" SELECT * FROM ( " +
            " SELECT " +
            " SUM(ABS(m.amount)) totalAmount, " +
            " m.member_id " +
            "  " +
            " FROM " +
            " member_transaction m " +
            " WHERE " +
            " m.type =0 " +
            " AND m.symbol = #{coinUnit} " +
            " GROUP BY " +
            " m.member_id, " +
            " m.type) a WHERE a.totalAmount>#{rechargeAmount}")
    List<ValidPersonVo> rechargePerson(@Param("coinUnit") String coinUnit, @Param("rechargeAmount") BigDecimal rechargeAmount);


    /**
     * 有效用户持仓币数 只统计币币账户
     * @param coinId
     * @param memberIds
     * @return
     */
    BigDecimal validHoldCoinCount(@Param("coinId") String coinId, @Param("memberIds") Set<Long> memberIds);

    @Select("select switch_status from support_coin_entrance where member_id=#{memberId}")
    Integer hasEntrance(@Param("memberId") Long memberId);
}


















