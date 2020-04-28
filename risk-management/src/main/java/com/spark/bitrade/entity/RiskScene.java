package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 风控场景配置
 * </p>
 *
 * @author qiliao
 * @since 2020-02-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RiskScene implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 监控表名
     */
    private String tableName;

    /**
     * 表名适应0匹配1模糊
     */
    private String tablePattern;

    /**
     * 监控事件(insert,update)
     */
    private String events;

    /**
     * 匹配参数
     */
    private String parameter;

    /**
     * 出入类型0出1入
     */
    private String inOut;

    /**
     * 汇率转换对象币种
     */
    private String exchangeSource;

    /**
     * 币种数量匹配
     */
    private String amountFormatter;

    /**
     * 币种匹配
     */
    private String unitFormatter;

    /**
     * 用户编号匹配
     */
    private String memberFormatter;

    /**
     * 监控备注键
     */
    private String sceneDesc;

    /**
     * 是否启用0启1停
     */
    private String openScene;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private Long createId;

    /**
     * 更新时间
     */
    private Date updateTime;


}
