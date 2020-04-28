package com.spark.bitrade.mapper;

import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.entity.Advertise;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.entity.vo.AdvertiseVo;
import com.spark.bitrade.entity.vo.MemberAdvertiseDetail;
import com.spark.bitrade.entity.vo.OtcAdvertise;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * (Advertise)表数据库访问层
 *
 * @author ss
 * @date 2020-03-19 10:22:03
 */
public interface AdvertiseMapper extends BaseMapper<Advertise>{

    /**
     * 根据用户ID获取广告列表
     * @param memberId
     * @param start
     * @param pageSize
     * @return
     */
    List<AdvertiseVo> getAllAdvertiseByMemberId(@Param("memberId") Long memberId, @Param("start")int start, @Param("pageSize")Integer pageSize);

    /**
     * 根据用户ID获取广告列表数量
     * @param memberId
     * @return
     */
    Long getAllAdvertiseNumByMemberId(@Param("memberId") Long memberId);

    /**
     * 根据广告id获取广告详情
     * @param id
     * @return
     */
    MemberAdvertiseDetail getDetail(@Param("id") Long id);

    /**
     * 获取广告列表
     * @param start 开始位置
     * @param pageSize 数量
     * @param advertise 广告
     * @param marketPrice 法币价格
     * @param advertiseRankType 精度参数
     * @param isPositive
     * @param coinScale
     * @return
     */
    List<OtcAdvertise> getPageAdvertiseRank(@Param("start") int start, @Param("pageSize")int pageSize,
                                            @Param("advertise")Advertise advertise, @Param("marketPrice")BigDecimal marketPrice,
                                            @Param("advertiseRankType")Integer advertiseRankType, @Param("isPositive")int isPositive,
                                            @Param("coinScale")Integer coinScale);

    /**
     *
     * @param advertise 广告
     * @return
     */
    Long getPageAdvertiseNum( @Param("advertise")Advertise advertise);

    /**
     *
     * @param start 开始位置
     * @param pageSize 数量
     * @param advertise 广告
     * @param marketPrice
     * @param coinScale
     * @return
     */
    List<OtcAdvertise> getPageAdvertise(@Param("start") int start, @Param("pageSize")int pageSize,
                                        @Param("advertise")Advertise advertise, @Param("marketPrice")BigDecimal marketPrice,
                                        @Param("coinScale")Integer coinScale);

    /**
     * 下架广告
     * @param id
     * @param remainAmount
     * @return
     */
    int putOffAdvertise(@Param("id") Long id, @Param("remainAmount") BigDecimal remainAmount);

    /**
     * 更改广告
     * @param memberId 用户ID
     * @param oldType 旧的广告状态
     * @param newType 新的广告状态
     * @param currencyId 新的法币ID
     * @return
     */
    int updateMemberAdvertise(@Param("memberId") Long memberId, @Param("oldType")Integer oldType, @Param("newType")Integer newType, @Param("currencyId")Long currencyId);
    /**
     * 更改广告
     * @param memberId 用户ID
     * @param oldType 旧的广告状态
     * @param newType 新的广告状态
     * @param currencyId 新的法币ID
     * @return
     */
    int reUpdateMemberAdvertise(@Param("memberId") Long memberId, @Param("oldType")Integer oldType, @Param("newType")Integer newType, @Param("currencyId")Long currencyId);

    /**
     * 获取出售类型自动下架的广告
     * @param coinId
     * @param marketPrice
     * @param jyRate
     * @return
     */
    List<Advertise> selectSellAutoOffShelves(@Param("coinId") long coinId, @Param("marketPrice")BigDecimal marketPrice, @Param("jyRate")BigDecimal jyRate, @Param("currencyId")Long currencyId);

    /**
     * 获取购买类型自动下架的广告
     * @param coinId
     * @param marketPrice
     * @return
     */
    List<Advertise> selectBuyAutoOffShelves(@Param("coinId")long coinId,  @Param("marketPrice")BigDecimal marketPrice, @Param("currencyId")Long currencyId);
}
