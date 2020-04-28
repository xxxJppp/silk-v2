package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.spark.bitrade.constant.BooleanEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 扶持上币配置KEY-VALUE
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SupportConfigList implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 字典KEY
     */
    private String dictKey;

    /**
     * 字典值
     */
    private String dictValue;

    /**
     * 配置KEY(support_config表中的key)
     */
    private String configKey;

    /**
     * 备注
     */
    private String remark;

    /**
     * 更新人ID
     */
    private Long createBy;

    /**
     * 创建人ID
     */
    private Long updateBy;

    /**
     * 是否删除{0:否,1:是}
     */
    private BooleanEnum deleteFlag;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


}
