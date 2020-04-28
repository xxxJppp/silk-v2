package com.spark.bitrade.dto;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.spark.bitrade.constant.SlpReleaseType;
import com.spark.bitrade.constant.SlpStatus;
import com.spark.bitrade.util.BigDecimalUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * SlpReleaseRecordDto
 *
 * @author Archx[archx@foxmail.com]
 * at 2019-07-16 07:03
 */
@Data
@AllArgsConstructor
public class SlpReleaseRecordDto {

    /**
     * 记录ID
     */
    private Long id;

    /**
     * 计划ID
     */
    private Long planId;

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 释放类型
     */
    private SlpReleaseType type;

    /**
     * 理论释放
     */
    private BigDecimal releaseAmount;

    /**
     * 理论奖池释放
     */
    private BigDecimal jackpotAmount;

    /**
     * 分配比例
     */
    private BigDecimal proportion;

    /**
     * 原有备注信息
     */
    private String comment;

    /**
     * 获取总额
     *
     * @return total
     */
    public BigDecimal getTotalAmount() {
        return releaseAmount.add(jackpotAmount);
    }

    /**
     * 重新分配
     *
     * @param totalAmount 总额
     */
    public void redistribution(BigDecimal totalAmount) {
        this.releaseAmount = totalAmount.multiply(proportion);
        this.jackpotAmount = totalAmount.multiply(BigDecimal.ONE.subtract(proportion));
    }

    /**
     * 获取更新包装器
     *
     * @param coinInUnit 币种
     * @param rate       汇率
     * @param comment    备注
     * @return wrapper
     */
    public <T> UpdateWrapper<T> newUpdateWrapper(String coinInUnit, BigDecimal rate, String comment) {
        UpdateWrapper<T> update = new UpdateWrapper<>();

        Date now = Calendar.getInstance().getTime();

        if (StringUtils.hasText(this.comment)) {
            comment = this.comment + "," + comment;
        }

        update.eq("id", getId()).eq("status", SlpStatus.NOT_PROCESSED)
                .set("coin_in_unit", coinInUnit)
                .set("release_in_rate", rate)
                .set("release_in_amount", BigDecimalUtil.div2down(getReleaseAmount(), rate))
                .set("jackpot_in_amount", BigDecimalUtil.div2down(getJackpotAmount(), rate))
                .set("comment", comment)
                .set("status", SlpStatus.PROCESSED)
                .set("update_time", now)
                .set("release_time", now);

        return update;
    }
}
