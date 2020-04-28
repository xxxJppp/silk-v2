package com.spark.bitrade.api.vo;

import com.spark.bitrade.constant.WalletType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * TransferVo
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/11/21 15:31
 */
@Data
@ApiModel(description = "转账实体")
public class TransferVo {

    /**
     * 会员ID
     */
    @ApiModelProperty(value = "会员ID", example = "")
    private Long memberId;

    /**
     * 币种
     */
    @ApiModelProperty(value = "币种, eg. BTC | ETH", example = "")
    private String coinUnit;

    /**
     * 转出方
     */
    @ApiModelProperty(value = "转出方", example = "")
    private WalletType from;

    /**
     * 接收方
     */
    @ApiModelProperty(value = "接收方", example = "")
    private WalletType to;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量", example = "")
    private BigDecimal amount;
}
