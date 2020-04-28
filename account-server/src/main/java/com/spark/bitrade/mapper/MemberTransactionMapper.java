package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.entity.MemberTransaction;
import com.spark.bitrade.vo.WidthRechargeStaticsVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * (MemberTransaction)表数据库访问层
 *
 * @author yangch
 * @since 2019-06-15 16:27:30
 */
public interface MemberTransactionMapper extends BaseMapper<MemberTransaction> {

    List<WidthRechargeStaticsVo> widthRechargeStaticsVo(@Param("type") TransactionType type, @Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("coin") String coin, @Param("page") IPage page);
}