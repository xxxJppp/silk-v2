package com.spark.bitrade.api.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.spark.bitrade.repository.entity.BtBankMinerOrder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author ww
 * @time 2019.10.28 17:53
 */
@Data
public class MinerOrderVO extends BtBankMinerOrder {


    /**
     * 交易创建时间
     */
    @TableField(value = "transaction_create_time")
    @ApiModelProperty(value = "交易创建时间")
    private Date transactionCreateTime;
}
