package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * app首页快捷入口
 * </p>
 *
 * @author qiliao
 * @since 2020-01-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AppIndexConfig implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 简介US
     */
    private String descUs;

    /**
     * 标题US
     */
    private String titleUs;

    /**
     * 标题KR
     */
    private String titleKr;

    /**
     * 简介KR
     */
    private String descKr;

    /**
     * 标题HK
     */
    private String titleHk;

    /**
     * 简介HK
     */
    private String descHk;

    /**
     * 标题CN
     */
    private String titleCn;

    /**
     * 简介CN
     */
    private String descCn;

    /**
     * 链接
     */
    private String link;

    /**
     * 排序
     */
    private Integer sorts;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private String icon;
    /**是否内部链接 0:否 1:是*/
    private Integer linkType;
    /**是否登录 0:否 1:是*/
    private Integer isLogin;
}
