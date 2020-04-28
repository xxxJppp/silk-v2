package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.entity.SupportStreamRecords;
import com.spark.bitrade.param.StreamSearchParam;
import com.spark.bitrade.vo.CoinMatchVo;
import com.spark.bitrade.vo.SupportStreamSwitchVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 引流开关记录 Mapper 接口
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
public interface SupportStreamRecordsMapper extends BaseMapper<SupportStreamRecords> {


    List<SupportStreamSwitchVo> findStreamSwitchRecord(@Param("memberId") Long memberId, @Param("param") StreamSearchParam param, @Param("page") IPage page);

    @Select("SELECT symbol AS coinMatchName,min_sell_price AS price FROM exchange_coin WHERE coin_symbol=#{coin} AND enable=1")
    List<CoinMatchVo> findCoinMatch(@Param("coin")String coin);
}
