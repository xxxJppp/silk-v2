package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constant.EquityStatus;
import com.spark.bitrade.constant.SuperRegistration;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 合伙人用户关联表 即社区表
 * </p>
 *
 * @author qiliao
 * @since 2019-07-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SuperPartnerCommunity implements Serializable {

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
     * 社区名称
     */
    private String communityName;

    /**
     * 微信二维码地址
     */
    private String wechatCode;

    /**
     * 微信号
     */
    private String wechatNum;

    /**
     * 社区人数
     */
    private Integer peopleCount;

    /**
     * 社区链接
     */
    private String communityLink;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 退出合伙人时间
     */
    private Date exitTime;

    /**
     * 社区成立时间
     */
    private Date foundingTime;

    /**
     * 是否有效
     */
    private BooleanEnum usable=BooleanEnum.IS_FALSE;

    /**
     * 权益状态
     */
    private EquityStatus equityStatus=EquityStatus.NORMAL;
    /**
     * 备注
     */
    private String remark;
    /**
     * 注册来源 默认0 主动注册
     */
    private SuperRegistration sourceChannel=SuperRegistration.INITIATIVE;
    /**
     *推荐人id
     */
    private Long referrerId;

    public static final String REFERRER_ID="referrer_id";

    public static final String ID = "id";

    public static final String SOURCE_CHANNEL="source_Channel";

    public static final String MEMBER_ID = "member_id";

    public static final String COMMUNITY_NAME = "community_name";

    public static final String WECHAT_CODE = "wechat_code";

    public static final String WECHAT_NUM = "wechat_num";

    public static final String PEOPLE_COUNT = "people_count";

    public static final String COMMUNITY_LINK = "community_link";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

    public static final String EXIT_TIME = "exit_time";

    public static final String FOUNDING_TIME = "founding_time";

    public static final String USABLE = "usable";

    public static final String EQUITY_STATUS = "equity_status";

    public static final String REMARK = "remark";

}
