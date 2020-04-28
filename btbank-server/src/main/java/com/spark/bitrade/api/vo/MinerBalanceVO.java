package com.spark.bitrade.api.vo;

import com.spark.bitrade.repository.entity.BtBankMinerBalance;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Administrator
 * @time 2019.10.25 20:09
 */

@ApiModel("用户帐户信息")

@Data
public class MinerBalanceVO extends BtBankMinerBalance {

    @ApiModelProperty("昨日收益统计")
    BigDecimal yestodayRewardSum;

    @ApiModelProperty("累计收益")
    BigDecimal totalRewardSum;

}
