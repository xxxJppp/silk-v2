package com.spark.bitrade.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * <p>
 * 推荐层次折扣配置
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class MemberRecommendCommisionSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer levelId;

    /**
     * 推荐层次
     * 0-不限层次
     */
    private Integer recommendLevel;

    /**
     * 折扣率
     */
    private BigDecimal commisionRatio;

    private transient  LocalDateTime createTime;

    private transient  LocalDateTime updateTime;


}
