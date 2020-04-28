package com.spark.bitrade.trans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spark.bitrade.constant.BooleanEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *  钱包账户更新实体
 *  备注：更新接口仅能更新指定的属性
 * @author yangch
 * @time 2019.02.14 14:26
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "钱包账户更新实体")
public class WalletUpdateEntity {
    /**
     * 更新条件：用户ID
     */
    @ApiModelProperty(value = "更新条件：用户ID", example = "")
    long memberId;

    /**
     * 更新条件：币种ID
     */
    @ApiModelProperty(value = "更新条件：币种ID", example = "")
    private String coinId;

    /**
     * 更新字段：充值地址
     */
    @ApiModelProperty(value = "更新字段：充值地址", example = "")
    private String address;

    /**
     * 更新字段：钱包锁定状态，0=不锁定，1=锁定
     */
    @ApiModelProperty(value = "更新字段：钱包锁定状态，0=不锁定，1=锁定", example = "")
    private BooleanEnum isLock;

    /**
     * 更新字段：钱包充值状态，0=禁用/1=启用
     */
    @ApiModelProperty(value = "更新字段：钱包充值状态，0=禁用/1=启用", example = "")
    private BooleanEnum enabledIn;

    /**
     * 更新字段：钱包提币状态，0=禁用/1=启用
     */
    @ApiModelProperty(value = "更新字段：钱包提币状态，0=禁用/1=启用", example = "")
    private BooleanEnum enabledOut;
}
