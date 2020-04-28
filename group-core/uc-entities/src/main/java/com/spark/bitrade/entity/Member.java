package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spark.bitrade.constant.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * (Member)表实体类
 *
 * @author archx
 * @since 2019-06-11 16:27:06
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "")
public class Member implements Serializable {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "ID", example = "")
    private Long id;
    //@TableId
    @ApiModelProperty(value = "SALT", example = "")
    private String salt;

    /**
     * 支付宝账号
     */
    @ApiModelProperty(value = "支付宝账号", example = "")
    private String aliNo;

    /**
     * 胜诉次数
     */
    @ApiModelProperty(value = "胜诉次数", example = "")
    private Integer appealSuccessTimes;

    /**
     * 申诉次数
     */
    @ApiModelProperty(value = "申诉次数", example = "")
    private Integer appealTimes;

    /**
     * 头像
     */
    @ApiModelProperty(value = "头像", example = "")
    private String avatar;

    /**
     * 银行
     */
    @ApiModelProperty(value = "银行", example = "")
    private String bank;

    /**
     * 银行支行
     */
    @ApiModelProperty(value = "银行支行", example = "")
    private String branch;

    /**
     * 银行卡号
     */
    @ApiModelProperty(value = "银行卡号", example = "")
    private String cardNo;

    /**
     * 认证商家申请时间
     */
    @ApiModelProperty(value = "认证商家申请时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date certifiedBusinessApplyTime;

    /**
     * 认证商家状态,0未认证，1待审核认证，2认证，5取消认证审核中
     */
    @ApiModelProperty(value = "认证商家状态,0未认证，1待审核认证，2认证，5取消认证审核中", example = "")
    private CertifiedBusinessStatus certifiedBusinessStatus;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱", example = "")
    private String email;

    @ApiModelProperty(value = "", example = "")
    private Integer firstLevel;

    /**
     * 身份证号
     */
    @ApiModelProperty(value = "身份证号", example = "")
    private String idNumber;

    /**
     * 邀请者ID
     */
    @ApiModelProperty(value = "邀请者ID", example = "")
    private Long inviterId;

    /**
     * 资金密码
     */
    @ApiModelProperty(value = "资金密码", example = "")
    private String jyPassword;

    /**
     * 最后登录时间
     */
    @ApiModelProperty(value = "最后登录时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastLoginTime;

    /**
     * 城市
     */
    @ApiModelProperty(value = "城市", example = "")
    private String city;

    /**
     * 国家
     */
    @ApiModelProperty(value = "国家", example = "")
    private String country;

    /**
     * 区
     */
    @ApiModelProperty(value = "区", example = "")
    private String district;

    /**
     * 省
     */
    @ApiModelProperty(value = "省", example = "")
    private String province;

    /**
     * 登录次数
     */
    @ApiModelProperty(value = "登录次数", example = "")
    private Integer loginCount;

    /**
     * 会员等级:0=GENERAL(普通),1=REALNAME(实名),2=IDENTIFICATION(认证商家)
     */
    @ApiModelProperty(value = "会员等级:0=GENERAL(普通),1=REALNAME(实名),2=IDENTIFICATION(认证商家)", example = "")
    private MemberLevelEnum memberLevel;

    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码", example = "")
    private String mobilePhone;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码", example = "")
    private String password;

    /**
     * 推广码
     */
    @ApiModelProperty(value = "推广码", example = "")
    private String promotionCode;

    /**
     * 真实姓名
     */
    @ApiModelProperty(value = "真实姓名", example = "")
    private String realName;

    /**
     * 0 审核失败，1 待认证，2审核通过
     */
    @ApiModelProperty(value = "0 审核失败，1 待认证，2审核通过", example = "")
    private RealNameStatus realNameStatus = RealNameStatus.NOT_CERTIFIED;

    /**
     * 注册时间
     */
    @ApiModelProperty(value = "注册时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date registrationTime;

    @ApiModelProperty(value = "", example = "")
    private Integer secondLevel;

    /**
     * 用户状态：0正常，1禁用
     */
    @ApiModelProperty(value = "用户状态：0正常，1禁用", example = "")
    private CommonStatus status = CommonStatus.NORMAL;

    @ApiModelProperty(value = "", example = "")
    private Integer thirdLevel;

    @ApiModelProperty(value = "", example = "")
    private String token;

    /**
     * token预计过期时
     */
    @ApiModelProperty(value = "token预计过期时", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date tokenExpireTime;

    /**
     * C2C交易次数
     */
    @ApiModelProperty(value = "C2C交易次数", example = "")
    private Integer transactions;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名", example = "")
    private String username;

    /**
     * 微信
     */
    @ApiModelProperty(value = "微信", example = "")
    private String wechat;

    /**
     * 国家
     */
    @ApiModelProperty(value = "国家", example = "")
    private String local;

    /**
     * 支付宝收款吗
     */
    @ApiModelProperty(value = "支付宝收款吗", example = "")
    private String qrCodeUrl;

    /**
     * 微信收款码
     */
    @ApiModelProperty(value = "微信收款码", example = "")
    private String qrWeCodeUrl;

    /**
     * 是否缴纳保证金
     */
    @ApiModelProperty(value = "是否缴纳保证金", example = "")
    private String margin;

    /**
     * 谷歌绑定时间
     */
    @ApiModelProperty(value = "谷歌绑定时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date googleDate;

    /**
     * 谷歌key
     */
    @ApiModelProperty(value = "谷歌key", example = "")
    private String googleKey;

    /**
     * 谷歌绑定状态
     */
    @ApiModelProperty(value = "谷歌绑定状态", example = "")
    private BooleanEnum googleState;

    /**
     * 是否允许发布广告：0为不允许；1为允许
     */
    @ApiModelProperty(value = "是否允许发布广告：0为不允许；1为允许", example = "")
    private BooleanEnum publishAdvertise;

    /**
     * 是否允许交易
     */
    @ApiModelProperty(value = "是否允许交易", example = "")
    private BooleanEnum transactionStatus;

    /**
     * 实名认证通过时间
     */
    @ApiModelProperty(value = "实名认证通过时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date applicationTime;

    /**
     * 商家认证通过时间
     */
    @ApiModelProperty(value = "商家认证通过时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date certifiedBusinessCheckTime;

    /**
     * 签到能力
     */
    //@ApiModelProperty(value = "签到能力", example = "")
    //private Boolean signInAbility;

    /**
     * 区域ID
     */
    @ApiModelProperty(value = "区域ID", example = "")
    private String areaId;

    /**
     * 注册IP
     */
    @ApiModelProperty(value = "注册IP", example = "")
    private String ip;

    @ApiModelProperty(value = "", example = "")
    private CertificateType certificateType;

    @ApiModelProperty(value = "", example = "")
    private String wechatNick;


}