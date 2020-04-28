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
public class Member implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 支付宝账号
     */
    private String aliNo;

    /**
     * 胜诉次数
     */
    private Integer appealSuccessTimes;

    /**
     * 申诉次数
     */
    private Integer appealTimes;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 银行
     */
    private String bank;

    /**
     * 银行支行
     */
    private String branch;

    /**
     * 银行卡号
     */
    private String cardNo;

    /**
     * 认证商家申请时间
     */
    private Date certifiedBusinessApplyTime;

    /**
     * 认证商家状态,0未认证，1待审核认证，2认证，5取消认证审核中
     */
    private Integer certifiedBusinessStatus;

    /**
     * 邮箱
     */
    private String email;

    private Integer firstLevel;

    /**
     * 身份证号
     */
    private String idNumber;

    /**
     * 邀请者ID
     */
    private Long inviterId;

    /**
     * 资金密码
     */
    private String jyPassword;

    /**
     * 最后登录时间
     */
    private Date lastLoginTime;

    /**
     * 城市
     */
    private String city;

    /**
     * 国家
     */
    private String country;

    /**
     * 区
     */
    private String district;

    /**
     * 省
     */
    private String province;

    /**
     * 登录次数
     */
    private Integer loginCount;

    /**
     * 会员等级:0=GENERAL(普通),1=REALNAME(实名),2=IDENTIFICATION(认证商家)
     */
    private Integer memberLevel;

    /**
     * 手机号码
     */
    private String mobilePhone;

    /**
     * 密码
     */
    private String password;

    /**
     * 推广码
     */
    private String promotionCode;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 0 审核失败，1 待认证，2审核通过
     */
    private Integer realNameStatus;

    /**
     * 注册时间
     */
    private Date registrationTime;

    private String salt;

    private Integer secondLevel;

    /**
     * 用户状态：0正常，1禁用
     */
    private Integer status;

    private Integer thirdLevel;

    private String token;

    /**
     * token预计过期时
     */
    private Date tokenExpireTime;

    /**
     * C2C交易次数
     */
    private Integer transactions;

    /**
     * 用户名
     */
    private String username;

    /**
     * 微信
     */
    private String wechat;

    /**
     * 国家
     */
    private String local;

    /**
     * 支付宝收款吗
     */
    private String qrCodeUrl;

    /**
     * 微信收款码
     */
    private String qrWeCodeUrl;

    /**
     * 是否缴纳保证金
     */
    private String margin;

    /**
     * 谷歌绑定时间
     */
    private Date googleDate;

    /**
     * 谷歌key
     */
    private String googleKey;

    /**
     * 谷歌绑定状态
     */
    private Integer googleState;

    /**
     * 是否允许发布广告：0为不允许；1为允许
     */
    private Integer publishAdvertise;

    /**
     * 是否允许交易
     */
    private Integer transactionStatus;

    /**
     * 实名认证通过时间
     */
    private Date applicationTime;

    /**
     * 商家认证通过时间
     */
    private Date certifiedBusinessCheckTime;



    
}
