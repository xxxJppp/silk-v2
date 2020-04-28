package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.BtBankDataDict;

public interface BtBankDataDictService extends IService<BtBankDataDict> {

    BtBankDataDict findFirstByDictIdAndDictKey(String dictId, String dictKey);

}
