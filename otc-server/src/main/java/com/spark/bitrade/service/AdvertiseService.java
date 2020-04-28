package com.spark.bitrade.service;

import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constants.MsgCode;
import com.spark.bitrade.entity.*;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.vo.AdvertiseVo;
import com.spark.bitrade.entity.vo.MemberAdvertiseDetail;
import com.spark.bitrade.entity.vo.MemberAdvertiseInfo;
import com.spark.bitrade.entity.vo.OtcAdvertise;
import com.spark.bitrade.enums.AdvertiseRankType;
import com.spark.bitrade.pagination.PageResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * (Advertise)表服务接口
 *
 * @author ss
 * @date 2020-03-19 10:22:04
 */
public interface AdvertiseService extends IService<Advertise>{

    /**
     * 是否开法币交易
     * @param memberId
     * @param message
     * @param advertiseType
     */
    void validateOpenExPitTransaction(Long memberId, MsgCode message, Integer advertiseType);

    /**
     * 查询普通用户是否可以发布广告 （false为不可）
     */
    boolean getAdvertiseConfig();


    /**
     * 检查支付方式
     * @param pay
     * @param advertise
     * @param member
     * @param currencyManage
     * @return
     */
    StringBuffer checkPayMode(String[] pay, Advertise advertise, Member member,CurrencyManage currencyManage);

    /**
     * 根据币种unit和用户ID获取钱包信息
     * @param unit
     * @param memberId
     * @return
     */
    MemberWallet getWalletByUnit(String unit, Long memberId);

    /**
     * 最小交易额检查
     * @param advertise
     * @param otcCoin
     * @param member
     */
    void checkAmount(Advertise advertise, OtcCoin otcCoin, Member member);

    /**
     * 根据用户ID获取广告列表
     * @param memberId 用户ID
     * @param page 当前页码 1开始
     * @param pageSize 每页展示数量
     * @return
     */
    PageResult<AdvertiseVo> getAllAdvertiseByMemberId(Long memberId, Integer page, Integer pageSize);

    void checkEditEnable(Advertise advertise);

    /**
     * 判断广告是否可以上架
     * @param advertise
     * @param member
     */
    void checkPutOnEnable(Advertise advertise,Member member);

    /**
     * 失效广告（用于修改默认法币）
     * @param member
     * @param currencyId 法币ID
     */
    void invalidAdvertise(Member member,Long currencyId);

    /**
     * 判断广告是否可以下架
     * @param advertise
     */
    void checkPutOffEnable(Advertise advertise);
    /**
     * 判断广告是否有进行中的订单和上架状态
     * @param memberId 用户ID
     */
    void checkOrderAndPutOn(Long memberId);

    /**
     * 下架广告
     * @param advertise
     * @return
     */
    int putOffShelves(Advertise advertise);

    /**
     * 判断用户是否可以发布广告
     * @param member
     * @param advertise
     */
    void checkMemberOpenEnble(Member member, Advertise advertise);

    /**
     * 修改广告
     * @param advertise
     * @param old
     * @return
     */
    int modifyAdvertise(Advertise advertise, Advertise old);

    /**
     * 根据广告id获取广告详情
     * @param id
     * @return
     */
    MemberAdvertiseDetail getDetail(Long id);

    /**
     *
     * @param pageNo 页码1开始
     * @param pageSize 每页数量
     * @param advertise 查询条件
     * @param marketPrice 法币对应coin价格
     * @param advertiseRankType 精度参数
     * @param isPositive
     * @param coinScale
     * @return
     */
    PageResult<OtcAdvertise> pageAdvertiseRank(Integer pageNo, Integer pageSize, Advertise advertise, BigDecimal marketPrice, AdvertiseRankType advertiseRankType, BooleanEnum isPositive, Integer coinScale);

    /**
     *
     * @param pageNo 页码1开始
     * @param pageSize 每页数量
     * @param advertise 查询条件
     * @param marketPrice 法币对应coin价格
     * @param coinScale
     * @return
     */
    PageResult<OtcAdvertise> pageAdvertise(Integer pageNo, Integer pageSize, Advertise advertise, BigDecimal marketPrice, Integer coinScale);

    /**
     * 获取商家所有广告
     * @param member 商家
     * @return
     */
    MemberAdvertiseInfo getMemberAdvertise(Member member);

    /**
     * 广告上架
     * @param advertise
     * @param member
     */
    void putOnShelves(Advertise advertise, Member member);

    /**
     * 自动下架余额不足的广告
     */
    Map<String, List<Long>> autoPutOffShelvesAdvertise();
}
