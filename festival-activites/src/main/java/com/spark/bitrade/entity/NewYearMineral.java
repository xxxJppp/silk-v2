package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 矿石表
 * </p>
 *
 * @author qiliao
 * @since 2019-12-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class NewYearMineral implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 矿石ID
     */
    private Long id;

    private Long configId;

    /**
     * 矿石名称
     */
    private String mineralName;

    /**
     * 投放数量
     */
    private Integer total;

    private Integer mineralType;

    /**
     * 已挖矿石数量
     */
    private Integer cost;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


}
