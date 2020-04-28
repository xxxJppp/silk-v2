package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constant.ModuleType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 扶持上币支付记录
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SupportPayRecords implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 申请人ID
     */
    private Long memberId;

    /**
     * 上币申请ID
     */
    private Long upCoinId;

    /**
     * 模块名称{0:引流交易码管理,1:交易对管理,2:转版管理}
     */
    private ModuleType moduleType;

    /**
     * 支付类型{0:支付,1,支付返还}
     */
    private Integer payType;

    /**
     * 支付币种
     */
    private String payCoin;

    /**
     * 支付数量
     */
    private BigDecimal payAmount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否删除{0:否,1:是}
     */
    private BooleanEnum deleteFlag;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 模块申请记录id
     */
    private Long applyId;
}
