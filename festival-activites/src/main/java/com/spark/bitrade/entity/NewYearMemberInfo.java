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
 * 用户矿石表
 * </p>
 *
 * @author qiliao
 * @since 2019-12-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class NewYearMemberInfo implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long memberId;

    /**
     * 合成令牌时间
     */
    private Date tokenTime;

    /**
     * 令牌合成时的广告语
     */
    private String tokenSponsorDescription;

    /**
     * 令牌ID(合成时生成)
     */
    private String token;

    /**
     * 挖矿次数
     */
    private Integer digTimes;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


}
