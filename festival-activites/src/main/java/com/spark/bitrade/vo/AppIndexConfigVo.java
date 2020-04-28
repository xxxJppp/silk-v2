package com.spark.bitrade.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
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
public class AppIndexConfigVo implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 简介
     */
    @ApiModelProperty(value = "简介")
    private String desc;

    /**
     * 标题
     */
    @ApiModelProperty(value = "标题")
    private String title;

    /**
     * 链接
     */
    @ApiModelProperty(value = "链接")
    private String link;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sorts;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "图标")
    private String icon;

    /**是否内部链接 0:否 1:是*/
    @ApiModelProperty(value = "是否内部链接 0:否 1:是")
    private Integer linkType;
    /**是否登录 0:否 1:是*/
    @ApiModelProperty(value = "是否登录 0:否 1:是")
    private Integer isLogin;
}
