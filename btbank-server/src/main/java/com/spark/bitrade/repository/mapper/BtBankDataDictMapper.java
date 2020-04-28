package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.BtBankDataDict;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BtBankDataDictMapper extends BaseMapper<BtBankDataDict> {
    BtBankDataDict findFirstByDictIdAndDictKey(@Param("dictId") String dictId, @Param("dictKey") String dictKey);
}