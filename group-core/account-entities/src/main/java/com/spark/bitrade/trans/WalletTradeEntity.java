package com.spark.bitrade.trans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constant.WalletChangeType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *  钱包账户交易实体类
 * 备注：单个账户的交易信息，可根据该交易信息处理账户、交易流水、手续费
 *
 * @author yangch
 * @time 2019.01.22 16:57
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "钱包账户交易实体类")
public class WalletTradeEntity extends WalletTradeBaseEntity {

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
}
