package com.spark.bitrade.trans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constant.WalletChangeType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *  钱包账户币种转换实体类
 *  备注：
 *      1、转换是指 同一账户中，不同币种之间的转换的场景（如：币币交易，减少一种币数，增加另外一种币数）
 *      2、source代表了转换前的币种，target代表了转换后的币种
 *
 * @author yangch
 * @time 2019.01.22 16:57
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "钱包账户币种转换实体类")
public class WalletExchangeEntity {
    /**
     * 必填，用户ID
     */
    @ApiModelProperty(value = "用户ID", example = "")
    private long memberId;

    /**
     * 必填，交易类型
     */
    @ApiModelProperty(value = "交易类型", example = "")
    private TransactionType type;

    /**
     * 选填，关联的业务ID
     */
    @ApiModelProperty(value = "关联的业务ID", example = "")
    private String refId;

    /**
     * 选填，资金变更类型
     */
    @ApiModelProperty(value = "资金变更类型", example = "")
    private WalletChangeType changeType = WalletChangeType.TRADE;

    /**
     * 必填，转换前的币种及账信息，一般情况下需要指定余额的符号为“负号”，代表为减少的币数
     */
    @ApiModelProperty(value = "转换前的币种及账信息", example = "")
    private WalletBaseEntity source;

    /**
     * 必填，转换后的币种及账信息，一般情况下不指定余额的符号，代表为增加的币数
     */
    @ApiModelProperty(value = "转换后的币种及账信息", example = "")
    private WalletBaseEntity target;

}
