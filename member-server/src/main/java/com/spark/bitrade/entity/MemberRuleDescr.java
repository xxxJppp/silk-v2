package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 会员规则
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MemberRuleDescr<Date> implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 规则类型
     */
    @ApiModelProperty(value = "规则类型")
    private String ruleType;

    /**
     * 内容-中文
     */
    @ApiModelProperty(value = "内容-中文")
    private String contentZh;

    /**
     * 内容-中文繁体
     */
    @ApiModelProperty(value = "内容-中文繁体")
    private String contentZhTw;

    /**
     * 内容-英文
     */
    @ApiModelProperty(value = "内容-英文")
    private String contentEn;

    /**
     * 内容-韩文
     */
    @ApiModelProperty(value = "内容-韩文")
    private String contentKo;

    private Date createTime;

    private Date updateTime;

    public static String TYPE = "type";
}
