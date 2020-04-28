package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.spark.bitrade.constant.IncubatorsBasicStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 孵化区-上币申请表
 * </p>
 *
 * @author qiliao
 * @since 2019-08-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class IncubatorsBasicInformation implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 自增长ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 项目名称
     */
    private String proName;

    /**
     * 社区名称
     */
    private String communityName;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 项目简介
     */
    private String proDesc;

    /**
     * 联系电话
     */
    private String telPhone;

    /**
     * 微信号
     */
    private String wechatNum;

    /**
     * 上传微信二维码地址
     */
    private String wechatCode;

    /**
     * 总锁仓（SLU）
     */
    private BigDecimal lockUpNum;

    /**
     * 申请会员ID
     */
    private Long memberId;

    /**
     * 状态（0-上币待审核；1-上币审核通过；2-上币审核拒绝；3-退出待审核；4-退出审核通过；5-退出审核拒绝；6-已关闭）
     */
    private IncubatorsBasicStatus status=IncubatorsBasicStatus.UP_COIN_PENDING;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最近一次修改时间
     */
    private Date updateTime;


    public static final String ID = "id";

    public static final String PRO_NAME = "pro_name";

    public static final String COMMUNITY_NAME = "community_name";

    public static final String CONTACT_PERSON = "contact_person";

    public static final String PRO_DESC = "pro_desc";

    public static final String TEL_PHONE = "tel_phone";

    public static final String WECHAT_NUM = "wechat_num";

    public static final String WECHAT_CODE = "wechat_code";

    public static final String LOCK_UP_NUM = "lock_up_num";

    public static final String MEMBER_ID = "member_id";

    public static final String STATUS = "status";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

}
