package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.spark.bitrade.constant.SuperAwardType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author qiliao
 * @since 2019-07-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SuperAwardRecord implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 币种
     */
    private String coinUnit;

    /**
     * 奖励数量
     */
    private BigDecimal costAward;

    /**
     * 用户id
     */
    private Long memberId;

    /**
     * 合伙人id
     */
    private Long partnerId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 奖励类型
     */
    private SuperAwardType awardType;
    /**
     * 交易金额
     */
    private BigDecimal totalAmount;


    public static final String ID = "id";

    public static final String COIN_UNIT = "coin_unit";

    public static final String COST_AWARD = "cost_award";

    public static final String MEMBER_ID = "member_id";

    public static final String PARTNER_ID="partner_id";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

    public static final String AWARD_TYPE="award_type";

}
