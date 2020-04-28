package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.entity.BtBankDataDict;
import com.spark.bitrade.repository.mapper.BtBankDataDictMapper;
import com.spark.bitrade.repository.service.BtBankDataDictService;
import org.springframework.stereotype.Service;

@Service
public class BtBankDataDictServiceImpl extends ServiceImpl<BtBankDataDictMapper, BtBankDataDict> implements BtBankDataDictService {

    @Override
    public BtBankDataDict findFirstByDictIdAndDictKey(String dictId, String dictKey) {
        return baseMapper.findFirstByDictIdAndDictKey(dictId, dictKey);
    }

}
