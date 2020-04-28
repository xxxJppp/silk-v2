package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.entity.ExchangeWallet;
import com.spark.bitrade.vo.MembertVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 用户币币账户 Mapper 接口
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-26
 */
public interface ExchangeWalletMapper extends BaseMapper<ExchangeWallet> {

    BigDecimal selectCountByCoinUnit(@Param("memberId")Long memberId,@Param("coinUnit") String coinUint, @Param("start") BigDecimal start, @Param("end") BigDecimal end);


    List<MembertVo> findExchangeWalletChicangMembers(@Param("memberId")Long memberId,
                                                     @Param("coinUnit") String coinUnit,
                                                     @Param("start") BigDecimal start,
                                                     @Param("end") BigDecimal end,
                                                     @Param("page") IPage page);
}
