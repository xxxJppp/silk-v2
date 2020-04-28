package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.api.dto.UnlockDTO;
import com.spark.bitrade.repository.entity.BtBankMinerOrderTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface BtBankMinerOrderTransactionMapper extends BaseMapper<BtBankMinerOrderTransaction> {

    /**
     * 可解锁记录
     *
     * @param time
     * @return true
     * @author shenzucai
     * @time 2019.10.24 22:42
     */
    List<UnlockDTO> listUnlockRecords(@Param("time") Date time);

    /**
     * 可解锁记录
     *
     * @param time
     * @return true
     * @author shenzucai
     * @time 2019.10.24 22:42
     */
    List<UnlockDTO> listUnlockRecordsCopy(@Param("time") Date time);

    int insertGrabOrDepatchOrder(@Param("tr") BtBankMinerOrderTransaction minerOrderTransaction, @Param("timeSpan") Integer timeSpan);
}