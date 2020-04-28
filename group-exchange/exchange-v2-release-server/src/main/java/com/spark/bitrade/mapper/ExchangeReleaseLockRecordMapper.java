package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.entity.ExchangeReleaseLockRecord;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * 币币交易释放-锁仓明细表(ExchangeReleaseLockRecord)表数据库访问层
 *
 * @author yangch
 * @since 2019-12-16 14:52:16
 */
public interface ExchangeReleaseLockRecordMapper extends BaseMapper<ExchangeReleaseLockRecord> {


    /**
     *  保存锁仓记录
     *
     * @parm  lockAmount  锁仓数量
     * @param coinSymbol 币种
     * @param  refId 关联的充值流水ID
     * @param memberId
     * @return int
     */
    int addLockRecord(@Param("lockAmount") BigDecimal lockAmount, @Param("coinSymbol") String coinSymbol, @Param("refId") String refId, @Param("memberId")Integer memberId);
}