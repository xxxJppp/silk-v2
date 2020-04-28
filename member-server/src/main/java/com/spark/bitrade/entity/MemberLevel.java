package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 会员等级
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MemberLevel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 会员等级名称-简体中文
     */
    private String nameZh;

    /**
     * 会员等级名称-中文繁体
     */
    private String nameZhTw;

    /**
     * 会员等级名称-英文
     */
    private String nameEn;

    /**
     * 会员等级名称-韩语
     */
    private String nameKo;

    /**
     * 可升级等级范围
     */
    private String upgradeRange;
    

    private Date createTime;

    private Date updateTime;


}
