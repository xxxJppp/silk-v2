package com.spark.bitrade.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@ApiModel(value = "com-spark-bitrade-repository-entity-BtBankDataDict")
@Data
@TableName(value = "bt_bank_data_dict")
public class BtBankDataDict implements Serializable {
    /**
     * 配置编号
     */
    @TableId(value = "dict_id", type = IdType.INPUT)
    @ApiModelProperty(value = "配置编号")
    private String dictId;

    /**
     * 配置KEY
     */
    @TableId(value = "dict_key", type = IdType.INPUT)
    @ApiModelProperty(value = "配置KEY")
    private String dictKey;

    /**
     * 配置VALUE
     */
    @TableField(value = "dict_val")
    @ApiModelProperty(value = "配置VALUE")
    private String dictVal;

    /**
     * 数据类型
     */
    @TableField(value = "dict_type")
    @ApiModelProperty(value = "数据类型")
    private String dictType;

    /**
     * 描述
     */
    @TableField(value = "remark")
    @ApiModelProperty(value = "描述")
    private String remark;

    /**
     * 状态：0-失效 1-生效
     */
    @TableField(value = "status")
    @ApiModelProperty(value = "状态：0-失效 1-生效")
    private Integer status;

    /**
     * 排序
     */
    @TableField(value = "sort")
    @ApiModelProperty(value = "排序")
    private Integer sort;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    /**
     * 添加时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value = "添加时间")
    private Date createTime;

    private static final long serialVersionUID = 1L;
}