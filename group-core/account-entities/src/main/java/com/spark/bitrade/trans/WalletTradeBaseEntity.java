package com.spark.bitrade.trans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *  交易的基本实体
 *
 * @author yangch
 * @time 2019.02.13 11:31
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "交易的基本实体类")
public class WalletTradeBaseEntity extends WalletBaseEntity {
    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID", example = "")
    long memberId;
}
