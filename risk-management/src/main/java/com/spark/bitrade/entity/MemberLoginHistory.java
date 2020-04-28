package com.spark.bitrade.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author qiliao
 * @since 2020-03-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MemberLoginHistory implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 区域信息
     */
    private String area;

    /**
     * 登录IP
     */
    private String loginip;

    /**
     * 登录时间
     */
    private Date loginTime;

    /**
     * 会员id
     */
    private Long memberId;

    /**
     * 登录类型（WEB、android、IOS）
     */
    private String type;

    /**
     * 第三方平台标志
     */
    private String thirdMark;

    /**
     * 是否为注册信息，0：表示登录信息;1：表示注册信息
     */
    private String isRegistrate;

    /**
     * 手机是否root或越狱
     */
    private String isRootOrJailbreak;

    /**
     * 设备型号
     */
    private String model;

    /**
     * 厂商
     */
    private String producers;

    /**
     * 系统版本
     */
    private String systemVersion;

    /**
     * 唯一标志码UUID
     */
    private String uuid;


    
}
