package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

/**
 * 用户中心配置表
 *
 * @author zhongxj
 * @date 2019年10月21日
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "用户中心配置表")
public class UserConfigurationCenter {
    /**
     * 自增长ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 关联会员ID，与member表id字段关联
     */
    private Long memberId;
    /**
     * 触发事件{0:充值到账提醒,1:新订单创建提醒,2:交易即将过期,3:已付款提醒,4:已释放提醒,5:申诉处理结果提醒}
     */
    private Integer triggeringEvent;
    /**
     * 是否短信通知（0-无此渠道；1-是；2-否，默认2）
     */
    private Integer isSms;
    /**
     * 是否邮件通知（1-是；2-否，默认1）
     */
    private Integer isEmail;
    /**
     * 是否进行离线通知（1-是；2-否，默认1）
     */
    private Integer isApns;
    /**
     * 数据是否可用（1-是；2-否，默认1）
     */
    private Integer usable;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 最近一次修改时间
     */
    private Date updateTime;
}
