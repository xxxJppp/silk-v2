package com.spark.bitrade.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 风控出入金明细
 * </p>
 *
 * @author qiliao
 * @since 2020-02-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RiskDetailed implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户编号
     */
    private Long memberId;

    /**
     * 出入类型0出1入
     */
    private String inOut;

    /**
     * 出入分类备注
     */
    private String typeDesc;

    /**
     * 出入币种
     */
    private String unti;

    /**
     * 币种数量
     */
    private BigDecimal amount;

    /**
     * 汇率转换币种
     */
    private String exchangeSource;

    /**
     * 计算汇率
     */
    private BigDecimal exchange;

    /**
     * 转换后金额
     */
    private BigDecimal convertAmount;

    /**
     * 备注
     */
    private String detailedDesc;

    /**
     * 出入时间
     */
    private Date createTime;

    /**
     * 唯一id
     */
    private Long rfId;

    /**
     * 消息id
     */
    private String messageId;

    /**
     * 摘要签名
     */
    private String abstractKey;

    /**
     * 更新时间
     */
    private Date updateTime;


}
