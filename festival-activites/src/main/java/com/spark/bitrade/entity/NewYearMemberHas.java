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
 * 用户矿石持有表
 * </p>
 *
 * @author qiliao
 * @since 2019-12-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class NewYearMemberHas implements Serializable {

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
     * 矿石ID
     */
    private Long mineralId;

    /**
     * 矿石名称
     */
    private String mineralName;

    /**
     * 广告语
     */
    private String sponsorDescription;

    private Long refId;

    /**
     * 赠送人ID
     */
    private Long fromMemberId;

    /**
     * 矿石来源{0:挖矿,1:赠送}
     */
    private Integer fromWhere;

    /**
     * 矿石状态{0:失效,1:有效}
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private Integer direction;


}
