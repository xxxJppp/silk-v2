package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.spark.bitrade.constant.BooleanEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 项目方后台总控菜单表
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SupportProjectMenu implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 父级菜单id
     */
    @ApiModelProperty(value = "父级菜单id")
    private Long parentId;

    /**
     * 菜单名字
     */
    @ApiModelProperty(value = "菜单名字")
    private String menuName;

    /**
     * 菜单类别
     */
    @ApiModelProperty(value = "菜单类别")
    private Integer menuType;

    /**
     * 菜单路径
     */
    @ApiModelProperty(value = "菜单路径")
    private String path;

    /**
     * 菜单启用状态 {1:启动, 0:停用}
     */
    @ApiModelProperty(value = "菜单启用状态 {1:启动, 0:停用}")
    private BooleanEnum menuStatus;

    /**
     * 是否删除{0:否,1:是}
     */
    private BooleanEnum deleteFlag;

    /**
     * 创建人
     */
    private Long createUserid;

    /**
     * 修改人
     */
    private Long modifyUserid;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    public static final String MENU_STATUS = "menu_status";

    public static final String DELETE_FLAG = "delete_flag";
}
