package com.spark.bitrade.entity;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.spark.bitrade.constant.LockStatus;
import com.spark.bitrade.constant.SmsSendStatus;
import com.spark.bitrade.util.DateUtil;
import com.spark.bitrade.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * 
 * </p>
 *
 * @author xiaoxianming
 * @since 2019-12-02
 */
@TableName("lock_coin_detail")
public class LockCoinDetail extends Model<LockCoinDetail> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 撤销时间
     */
    @TableField("cancle_time")
    @JsonDeserialize(using = DataDeserialize.class)
    private Date cancleTime;

    /**
     * 活动币种
     */
    @TableField("coin_unit")
    private String coinUnit;

    /**
     * 锁仓价格相对USDT
     */
    @TableField("lock_price")
    private BigDecimal lockPrice;

    /**
     * 锁仓时间
     */
    @TableField("lock_time")
    @JsonDeserialize(using = DataDeserialize.class)
    private Date lockTime;

    /**
     * 会员id
     */
    @TableField("member_id")
    private Long memberId;

    /**
     * 预计收益
     */
    @TableField("plan_income")
    private BigDecimal planIncome;

    /**
     * 计划解锁时间
     */
    @TableField("plan_unlock_time")
    @JsonDeserialize(using = DataDeserialize.class)
    private Date planUnlockTime;

    /**
     * 关联活动ID
     */
    @TableField("ref_activitie_id")
    private Long refActivitieId;

    /**
     * 剩余锁仓币数
     */
    @TableField("remain_amount")
    private BigDecimal remainAmount;

    /**
     * 状态（已锁定、已解锁、已撤销）
     */
    private Integer status;

    /**
     * 总锁仓币数
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 锁仓类型（商家保证金-0、手动锁仓-1、锁仓活动-2、理财锁仓-3、SLU节点产品-10、布朗计划-11、超级合伙人-12、UTT锁仓）
     */
    private Integer type;

    /**
     * 解锁时间
     */
    @TableField("unlock_time")
    @JsonDeserialize(using = DataDeserialize.class)
    private Date unlockTime;

    /**
     * 锁仓总金额（CNY）
     */
    private BigDecimal totalcny;

    /**
     * USDT价格（CNY）
     */
    @TableField("usdt_pricecny")
    private BigDecimal usdtPricecny;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态（不返佣、未返佣、已返佣）
     */
    @TableField("lock_reward_satus")
    private LockStatus lockRewardSatus;

    /**
     * 短信发送状态(0:未发送,1:已发送,2:发送失败)
     */
    @TableField("sms_send_status")
    private SmsSendStatus smsSendStatus;

    /**
     * 开始释放
     */
    @TableField("begin_days")
    private Integer beginDays;

    /**
     * 每期天数
     */
    @TableField("cycle_days")
    private Integer cycleDays;

    /**
     * 周期比例
     */
    @TableField("cycle_ratio")
    private String cycleRatio;

    /**
     * 锁仓期数
     */
    @TableField("lock_cycle")
    private Integer lockCycle;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Date getCancleTime() {
        return cancleTime;
    }

    public void setCancleTime(Date cancleTime) {
        this.cancleTime = cancleTime;
    }
    public String getCoinUnit() {
        return coinUnit;
    }

    public void setCoinUnit(String coinUnit) {
        this.coinUnit = coinUnit;
    }
    public BigDecimal getLockPrice() {
        return lockPrice;
    }

    public void setLockPrice(BigDecimal lockPrice) {
        this.lockPrice = lockPrice;
    }
    public Date getLockTime() {
        return lockTime;
    }

    public void setLockTime(Date lockTime) {
        this.lockTime = lockTime;
    }
    public Long getMemberId() {
        return memberId;
    }

    public LockStatus getLockRewardSatus() {
        return lockRewardSatus;
    }

    public void setLockRewardSatus(LockStatus lockRewardSatus) {
        this.lockRewardSatus = lockRewardSatus;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
    public BigDecimal getPlanIncome() {
        return planIncome;
    }

    public void setPlanIncome(BigDecimal planIncome) {
        this.planIncome = planIncome;
    }
    public Date getPlanUnlockTime() {
        return planUnlockTime;
    }

    public void setPlanUnlockTime(Date planUnlockTime) {
        this.planUnlockTime = planUnlockTime;
    }
    public Long getRefActivitieId() {
        return refActivitieId;
    }

    public void setRefActivitieId(Long refActivitieId) {
        this.refActivitieId = refActivitieId;
    }
    public BigDecimal getRemainAmount() {
        return remainAmount;
    }

    public void setRemainAmount(BigDecimal remainAmount) {
        this.remainAmount = remainAmount;
    }
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
    public Date getUnlockTime() {
        return unlockTime;
    }

    public void setUnlockTime(Date unlockTime) {
        this.unlockTime = unlockTime;
    }
    public BigDecimal getTotalcny() {
        return totalcny;
    }

    public void setTotalcny(BigDecimal totalcny) {
        this.totalcny = totalcny;
    }
    public BigDecimal getUsdtPricecny() {
        return usdtPricecny;
    }

    public void setUsdtPricecny(BigDecimal usdtPricecny) {
        this.usdtPricecny = usdtPricecny;
    }
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public SmsSendStatus getSmsSendStatus() {
        return smsSendStatus;
    }

    public void setSmsSendStatus(SmsSendStatus smsSendStatus) {
        this.smsSendStatus = smsSendStatus;
    }

    public Integer getBeginDays() {
        return beginDays;
    }

    public void setBeginDays(Integer beginDays) {
        this.beginDays = beginDays;
    }
    public Integer getCycleDays() {
        return cycleDays;
    }

    public void setCycleDays(Integer cycleDays) {
        this.cycleDays = cycleDays;
    }
    public String getCycleRatio() {
        return cycleRatio;
    }

    public void setCycleRatio(String cycleRatio) {
        this.cycleRatio = cycleRatio;
    }
    public Integer getLockCycle() {
        return lockCycle;
    }

    public void setLockCycle(Integer lockCycle) {
        this.lockCycle = lockCycle;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "LockCoinDetail{" +
        "id=" + id +
        ", cancleTime=" + cancleTime +
        ", coinUnit=" + coinUnit +
        ", lockPrice=" + lockPrice +
        ", lockTime=" + lockTime +
        ", memberId=" + memberId +
        ", planIncome=" + planIncome +
        ", planUnlockTime=" + planUnlockTime +
        ", refActivitieId=" + refActivitieId +
        ", remainAmount=" + remainAmount +
        ", status=" + status +
        ", totalAmount=" + totalAmount +
        ", type=" + type +
        ", unlockTime=" + unlockTime +
        ", totalcny=" + totalcny +
        ", usdtPricecny=" + usdtPricecny +
        ", remark=" + remark +
        ", lockRewardSatus=" + lockRewardSatus +
        ", smsSendStatus=" + smsSendStatus +
        ", beginDays=" + beginDays +
        ", cycleDays=" + cycleDays +
        ", cycleRatio=" + cycleRatio +
        ", lockCycle=" + lockCycle +
        "}";
    }

    static class DataDeserialize extends JsonDeserializer<String> {
        @Override
        public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            String text = jsonParser.getText();
            if (StringUtils.isNotBlank(text)) {
                DateUtil.stringToDate(text, "yyyy-MM-dd HH:mm:ss");
            }
            return null;
        }
    }
}
