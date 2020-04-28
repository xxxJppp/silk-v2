package com.spark.bitrade.trans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constant.WalletChangeType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *  转账实体类
 * 备注：
 * 1、转账是指 账户不同，币种相同的场景
 * 2、实体中的余额表示转账的数量（即代表着接收方的数据）
 *
 * @author yangch
 * @time 2019.02.13 11:29
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "转账实体类")
public class WalletTransferEntity {
    /**
     * 必须，交易类型
     */
    @ApiModelProperty(value = "交易类型", example = "")
    private TransactionType type;

    /**
     * 选填，关联的业务ID
     */
    @ApiModelProperty(value = "关联的业务ID", example = "")
    private String refId;

    /**
     * 资金变更类型
     */
    @ApiModelProperty(value = "资金变更类型", example = "")
    private WalletChangeType changeType = WalletChangeType.TRADE;

    /**
     * 发起方交易信息
     */
    @ApiModelProperty(value = "发起方交易信息", example = "")
    private WalletTradeBaseEntity initiatorTradeInfo;

    /**
     * 接收方交易信息
     */
    @ApiModelProperty(value = "接收方交易信息", example = "")
    private WalletTradeBaseEntity receiveTradeInfo;
}
