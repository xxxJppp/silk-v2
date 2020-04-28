package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constant.InCommunityStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author qiliao
 * @since 2019-07-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SuperMemberCommunity implements Serializable {

    private static final long serialVersionUID=1L;
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 用户id
     */
    private Long memberId;
    /**
     * 社区id
     */
    private Long communityId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 状态 0:在社区中  1:已退出
     */
    private InCommunityStatus status;

    /**
     * 发放奖励状态 0未发放 1已发放
     */
    private BooleanEnum giveStatus;

    /**
     * 是否活跃用户
     */
    private BooleanEnum isActive;

    public static final String ID = "id";

    public static final String MEMBER_ID = "member_id";

    public static final String COMMUNITY_ID = "community_id";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

    public static final String STATUS = "status";

}
