package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.entity.ExchangeReleaseAwardTotal;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * 币币交易-推荐人累计买入奖励累计表(ExchangeReleaseAwardTotal)表数据库访问层
 *
 * @author yangch
 * @since 2020-02-05 17:31:07
 */
public interface ExchangeReleaseAwardTotalMapper extends BaseMapper<ExchangeReleaseAwardTotal> {
    /**
     * 累计直推用户的买币数量
     *
     * @param memberId  推荐人ID
     * @param buyAmount 待累计的卖币数量
     * @return
     */
    int addTotalBuyAmount(@Param("memberId") long memberId, @Param("buyAmount") BigDecimal buyAmount);

    /**
     * 更新奖励
     *
     * @param memberId          推荐人ID
     * @param buyAmount         待累计的卖币数量
     * @param awardAmount       奖励数量
     * @param awardTimes        条件：奖励次数
     * @param minTotalBuyAmount 条件：满足的最低累计数量
     * @return
     */
    int updateAward(@Param("memberId") long memberId,
                    @Param("buyAmount") BigDecimal buyAmount,
                    @Param("awardAmount") BigDecimal awardAmount,
                    @Param("awardTimes") int awardTimes,
                    @Param("minTotalBuyAmount") BigDecimal minTotalBuyAmount);
}