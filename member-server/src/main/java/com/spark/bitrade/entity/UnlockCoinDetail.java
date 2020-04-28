package com.spark.bitrade.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

/**
 * <p>
 * 
 * </p>
 *
 * @author xiaoxianming
 * @since 2019-12-02
 */
@TableName("unlock_coin_detail")
public class UnlockCoinDetail extends Model<UnlockCoinDetail> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 解锁币数
     */
    private BigDecimal amount;

    /**
     * 解锁时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 关联锁仓记录ID
     */
    @TableField("lock_coin_detail_id")
    private Long lockCoinDetailId;

    /**
     * 解锁价格（相对USDT）
     */
    private BigDecimal price;

    /**
     * 剩余锁仓币数
     */
    @TableField("remain_amount")
    private BigDecimal remainAmount;

    /**
     * 结算类型
     */
    @TableField("settlement_type")
    private String settlementType;

    /**
     * USDT价格（CNY）
     */
    @TableField("usdt_pricecny")
    private BigDecimal usdtPricecny;

    /**
     * 结算币数
     */
    @TableField("settlement_amount")
    private BigDecimal settlementAmount;

    /**
     * 收益类型
     */
    @TableField("income_type")
    private String incomeType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public Long getLockCoinDetailId() {
        return lockCoinDetailId;
    }

    public void setLockCoinDetailId(Long lockCoinDetailId) {
        this.lockCoinDetailId = lockCoinDetailId;
    }
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public BigDecimal getRemainAmount() {
        return remainAmount;
    }

    public void setRemainAmount(BigDecimal remainAmount) {
        this.remainAmount = remainAmount;
    }
    public String getSettlementType() {
        return settlementType;
    }

    public void setSettlementType(String settlementType) {
        this.settlementType = settlementType;
    }
    public BigDecimal getUsdtPricecny() {
        return usdtPricecny;
    }

    public void setUsdtPricecny(BigDecimal usdtPricecny) {
        this.usdtPricecny = usdtPricecny;
    }
    public BigDecimal getSettlementAmount() {
        return settlementAmount;
    }

    public void setSettlementAmount(BigDecimal settlementAmount) {
        this.settlementAmount = settlementAmount;
    }
    public String getIncomeType() {
        return incomeType;
    }

    public void setIncomeType(String incomeType) {
        this.incomeType = incomeType;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "UnlockCoinDetail{" +
        "id=" + id +
        ", amount=" + amount +
        ", createTime=" + createTime +
        ", lockCoinDetailId=" + lockCoinDetailId +
        ", price=" + price +
        ", remainAmount=" + remainAmount +
        ", settlementType=" + settlementType +
        ", usdtPricecny=" + usdtPricecny +
        ", settlementAmount=" + settlementAmount +
        ", incomeType=" + incomeType +
        "}";
    }
}
