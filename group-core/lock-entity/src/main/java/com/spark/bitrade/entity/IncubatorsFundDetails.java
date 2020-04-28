package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.spark.bitrade.constant.IncubatorsDetailStatus;
import com.spark.bitrade.constant.IncubatorsLockType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 孵化区-解锁仓明细表
 * </p>
 *
 * @author qiliao
 * @since 2019-08-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class IncubatorsFundDetails implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 自增长ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 申请项目ID（外键，与incubators_basic_information表ID相关联）
     */
    private Long incubatorsId;

    /**
     * 操作类型（0-锁仓；1-解仓）
     */
    private IncubatorsLockType operationType=IncubatorsLockType.LOCK;

    /**
     * 币种
     */
    private String coinId;

    /**
     * 数量
     */
    private BigDecimal num;

    /**
     * 状态（0-初始化锁仓；1-发起加仓；2-失效；3-生效）
     */
    private IncubatorsDetailStatus status=IncubatorsDetailStatus.INIT_LOCK;

    /**
     * 操作人ID
     */
    private Long operationId;

    /**
     * 操作人姓名
     */
    private String operationName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最近一次修改时间
     */
    private Date updateTime;


    public static final String ID = "id";

    public static final String INCUBATORS_ID = "incubators_id";

    public static final String OPERATION_TYPE = "operation_type";

    public static final String COIN_ID = "coin_id";

    public static final String NUM = "num";

    public static final String STATUS = "status";

    public static final String OPERATION_ID = "operation_id";

    public static final String OPERATION_NAME = "operation_name";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

}
