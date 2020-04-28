package com.spark.bitrade.trans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 *  TCC交易撤销实体类
 *
 * @author young
 * @time 2019.11.27 16:10
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "TCC交易撤销实体类")
public class TradeTccCancelEntity {
    /**
     * 用户ID
     */
    private Long memberId;

    /**
     * 资金变更流水记录ID
     */
    private Long walletChangeRecordId;

    public TradeTccCancelEntity(long memberId, long walletChangeRecordId) {
        this.memberId = memberId;
        this.walletChangeRecordId = walletChangeRecordId;
    }
}
