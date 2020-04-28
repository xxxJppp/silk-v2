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
 * 年终活动-广告位项目方配置表
 * </p>
 *
 * @author qiliao
 * @since 2019-12-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class NewYearProjectAdvertisement implements Serializable {

    private static final long serialVersionUID=1L;

    private Long id;

    /**
     * 配置总表id
     */
    private Long configId;

    /**
     * 币种名称
     */
    private String coinNam;

    /**
     * 项目方介绍信息
     */
    private String projectIntroduction;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


}
