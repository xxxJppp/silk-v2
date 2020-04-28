package com.spark.bitrade.mapper;

import com.spark.bitrade.entity.AmountOfDiscountRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 经纪人优惠兑币限额记录(AmountOfDiscountRecord)表数据库访问层
 *
 * @author ss
 * @date 2020-04-08 15:58:56
 */
public interface AmountOfDiscountRecordMapper extends BaseMapper<AmountOfDiscountRecord>{

    /**
     * 减经纪人优惠额度
     * @param memberId
     * @param discountPart
     * @return
     */
    int updateMemberDiscount(@Param("memberId") Long memberId, @Param("account") BigDecimal discountPart, @Param("date")Date date);
}