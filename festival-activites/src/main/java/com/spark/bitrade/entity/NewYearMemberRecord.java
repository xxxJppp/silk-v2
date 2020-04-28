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
 * 矿石表
 * </p>
 *
 * @author qiliao
 * @since 2019-12-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class NewYearMemberRecord implements Serializable {

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
     * 挖到的矿石id
     */
    private Long mineralId;

    /**
     * 挖到的矿石名称
     */
    private String mineralName;

    /**
     * 数量
     */
    private Integer count;

    /**
     * 操作类型{1:挖矿,2:送出,3:获得,4:合成钥匙}
     */
    private Integer opType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


}
