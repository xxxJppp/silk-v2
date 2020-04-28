package com.spark.bitrade.api.vo;

import com.spark.bitrade.repository.entity.BtBankMinerOrderTransaction;
import lombok.Data;

import java.util.List;

/**
 * @author Administrator
 * @time 2019.10.25 20:09
 */
@Data
public class MinerOrderTransactionsVO {
    List<BtBankMinerOrderTransaction> content;
    Long totalElements;
}
