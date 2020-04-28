package com.spark.bitrade.api.vo;

import com.spark.bitrade.repository.entity.BtBankMinerBalance;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @author Administrator
 * @time 2019.10.25 20:09
 */
@ApiModel("用户余额列表")
@Data
public class MinerBalancesVO {
    List<BtBankMinerBalance> content;
    Integer totalElements;
}