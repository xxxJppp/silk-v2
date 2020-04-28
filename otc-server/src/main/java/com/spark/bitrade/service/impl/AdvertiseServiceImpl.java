package com.spark.bitrade.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Maps;
import com.spark.bitrade.common.PriceUtil;
import com.spark.bitrade.constant.*;
import com.spark.bitrade.constants.MsgCode;
import com.spark.bitrade.entity.*;
import com.spark.bitrade.entity.vo.AdvertiseVo;
import com.spark.bitrade.entity.vo.MemberAdvertiseDetail;
import com.spark.bitrade.entity.vo.MemberAdvertiseInfo;
import com.spark.bitrade.entity.vo.OtcAdvertise;
import com.spark.bitrade.enums.*;
import com.spark.bitrade.enums.OrderStatus;
import com.spark.bitrade.exception.InconsistencyException;
import com.spark.bitrade.mapper.AdvertiseMapper;
import com.spark.bitrade.mapper.CurrencyManageMapper;
import com.spark.bitrade.mapper.OtcCoinMapper;
import com.spark.bitrade.mapper.OtcOrderMapper;
import com.spark.bitrade.pagination.PageResult;
import com.spark.bitrade.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.BigDecimalUtils;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.spark.bitrade.constant.BooleanEnum.IS_FALSE;
import static com.spark.bitrade.constant.BooleanEnum.IS_TRUE;
import static com.spark.bitrade.util.BigDecimalUtils.*;

/**
 * (Advertise)表服务实现类
 *
 * @author ss
 * @date 2020-03-19 10:22:05
 */
@Slf4j
@Service("advertiseService")
public class AdvertiseServiceImpl extends ServiceImpl<AdvertiseMapper, Advertise> implements AdvertiseService {
    @Resource
    private AdvertiseMapper advertiseMapper;
    @Resource
    private IMemberApiService iMemberApiService;
    @Resource
    private IMemberWalletApiService memberWalletService;
    @Resource
    private ISilkDataDistApiService iSilkDataDistService;
    @Resource
    private PaySettingService paySettingService;
    @Resource
    private MemberCapSettingService memberCapSettingService;
    @Resource
    private OtcOrderMapper otcOrderMapper;
    @Resource
    private OtcCoinMapper otcCoinMapper;
    @Resource
    private CurrencyManageMapper currencyManageMapper;
    @Resource
    private CurrencyRateService currencyRateService;


    /**
     * 查询普通用户是否可以发布广告 （false为不可）
     */
    @Override
    public boolean getAdvertiseConfig() {
        MessageRespResult<SilkDataDist> silkDataDist = iSilkDataDistService.findOne("ad_config", "normal_user_publish");
        SilkDataDist silkData = silkDataDist.getData();
        if (silkData == null) {
            return false;
        }
        if ("true".equalsIgnoreCase(silkData.getDictVal()) || "1".equals(silkData.getDictVal())) {
            return true;
        }
        return false;
    }

    /**
     * 是否开法币交易
     *
     * @param memberId
     * @param message
     * @param advertiseType
     */
    @Override
    public void validateOpenExPitTransaction(Long memberId, MsgCode message, Integer advertiseType) {
        MemberSecuritySet set = getMemberSecuritySet(memberId);
        if (set != null) {
            //买
            if (AdvertiseType.BUY.getCode().equals(advertiseType)) {
                BooleanStringEnum bool = set.getIsOpenExPitTransaction();
                AssertUtil.isTrue(bool.isIs(), message);
            }
            //卖
            if (AdvertiseType.SELL.getCode().equals(advertiseType)) {
                BooleanStringEnum bool = set.getIsOpenExPitSellTransaction();
                AssertUtil.isTrue(bool.isIs(), message);
            }
        }
    }

    /**
     * 检查支付方式
     *
     * @param pay
     * @param advertise
     * @param member
     * @return
     */
    @Override
    public StringBuffer checkPayMode(String[] pay, Advertise advertise, Member member,CurrencyManage currencyManage) {
        MessageRespResult<List<PaySetting>> allowPayModeResult = paySettingService.getAllOpenPaySetting(advertise.getCurrencyId());
        List<PaySetting> allowPayModes = allowPayModeResult.getData();
        MessageRespResult<JSONObject> memberCapSettingResult = memberCapSettingService.queryCapSettingByMember(member);
        if("CNY".equalsIgnoreCase(currencyManage.getUnit())){
            //如果是人民币，必须要绑定银行卡
            AssertUtil.isTrue(1 == memberCapSettingResult.getData().getJSONObject("bankInfo").getInteger("verified"), OtcExceptionMsg.BANK_NOT_SETTING);
        }
        StringBuffer payMode = new StringBuffer();
        Arrays.stream(pay).forEach(x -> {
            AtomicBoolean flag1 = new AtomicBoolean(false);
            allowPayModes.stream().forEach(p -> {
                if (x.equals(p.getPayKey())) {
                    flag1.set(true);
                    //用户没有设置该支付方式，请前往个人中心完善支付方式设置
                    if (AdvertiseType.SELL.getCode().equals(advertise.getAdvertiseType())) {
                        AssertUtil.isTrue(1 == memberCapSettingResult.getData().getJSONObject(p.getPayKey()).getInteger("verified"), OtcExceptionMsg.PAY_MODE_NOT_SETTING);
                    }
                    payMode.append(p.getPayName() + ",");
                    log.info("payMode:" + payMode.toString());
                }
            });
            // 支付方式不支持，请检查支付方式
            AssertUtil.isTrue(flag1.get(), OtcExceptionMsg.PAY_MODE_ERR);
        });
        return payMode.length() > 0 ? payMode.deleteCharAt(payMode.length() - 1) : payMode;
    }

    /**
     * 获取用户钱包
     *
     * @param unit
     * @param memberId
     * @return
     */
    @Override
    public MemberWallet getWalletByUnit(String unit, Long memberId) {
        return memberWalletService.getWalletByUnit(memberId, unit).getData();
    }

    /**
     * 最小交易额检查
     *
     * @param advertise
     * @param otcCoin
     * @param member
     */
    @Override
    public void checkAmount(Advertise advertise, OtcCoin otcCoin, Member member) {
        //edit by ss 原因:限额由otcCoin中取改为从currencyManager里面取
//        if (AdvertiseType.SELL.getCode().equals(advertise.getAdvertiseType())) {
//            Assert.isTrue(compare(advertise.getNumber(), otcCoin.getSellMinAmount()), "出售数量必须大于" + otcCoin.getSellMinAmount());
//            MemberWallet memberWallet = getWalletByUnit(otcCoin.getUnit(), member.getId());
//            Assert.isTrue(compare(memberWallet.getBalance(), advertise.getNumber()), OtcExceptionMsg.INSUFFICIENT_BALANCE.getMessage());
//        } else {
//            Assert.isTrue(compare(advertise.getNumber(), otcCoin.getBuyMinAmount()), "购买数量必须大于" + otcCoin.getBuyMinAmount());
//        }
        if (AdvertiseType.SELL.getCode().equals(advertise.getAdvertiseType())) {
            AssertUtil.isTrue(compare(advertise.getNumber(), otcCoin.getSellMinAmount()), OtcExceptionMsg.SELL_NUM_TOO_LITTLE);
            MemberWallet memberWallet = getWalletByUnit(otcCoin.getUnit(), member.getId());
            AssertUtil.isTrue(compare(memberWallet.getBalance(), advertise.getNumber()), OtcExceptionMsg.INSUFFICIENT_BALANCE);
        } else {
            Assert.isTrue(compare(advertise.getNumber(), otcCoin.getBuyMinAmount()), "购买数量必须大于");
        }
    }

    /**
     * 根据用户ID获取广告列表
     *
     * @param memberId 用户ID
     * @param page     当前页码 1开始
     * @param pageSize 每页展示数量
     * @return
     */
    @Override
    public PageResult<AdvertiseVo> getAllAdvertiseByMemberId(Long memberId, Integer page, Integer pageSize) {
        if (page == null) {
            page = 1;
        }
        if (pageSize == null || 0 == pageSize) {
            pageSize = 10;
        }
        Long total = advertiseMapper.getAllAdvertiseNumByMemberId(memberId);
        List<AdvertiseVo> list = advertiseMapper.getAllAdvertiseByMemberId(memberId, (page - 1) * pageSize, pageSize);
        return new PageResult<>(list, page, pageSize, total);
    }

    /**
     * TODO 检查是否可编辑
     *
     * @param advertise
     */
    @Override
    public void checkEditEnable(Advertise advertise) {

    }

    /**
     * TODO 检查是否可上架
     *
     * @param advertise
     * @param member
     */
    @Override
    public void checkPutOnEnable(Advertise advertise, Member member) {

    }

    @Override
    public void invalidAdvertise(Member member, Long currencyId) {
        //失效不是currencyId的广告
        advertiseMapper.updateMemberAdvertise(member.getId(), AdvertiseControlStatus.PUT_OFF_SHELVES.getCode(), AdvertiseControlStatus.FAILURE.getCode(), currencyId);
        //currencyId的广告重新生效
        advertiseMapper.reUpdateMemberAdvertise(member.getId(), AdvertiseControlStatus.FAILURE.getCode(), AdvertiseControlStatus.PUT_OFF_SHELVES.getCode(), currencyId);
    }

    /**
     * 检查是否可下架
     *
     * @param advertise
     */
    @Override
    public void checkPutOffEnable(Advertise advertise) {
        //查询该广告是否有未完成的订单
        Integer count = otcOrderMapper.selectCount(new LambdaQueryWrapper<OtcOrder>()
                .eq(OtcOrder::getAdvertiseId, advertise.getId())
                .ne(OtcOrder::getStatus, OrderStatus.CANCELLED.getCode())
                .ne(OtcOrder::getStatus, OrderStatus.CLOSE.getCode())
                .ne(OtcOrder::getStatus, OrderStatus.COMPLETED.getCode()));
        AssertUtil.isTrue(count == 0, OtcExceptionMsg.HAVE_ORDER_ON);
    }

    /**
     * 检查是否有上架状态的广告
     *
     * @param memberId
     */
    @Override
    public void checkOrderAndPutOn(Long memberId) {
        //获取用户上架中的广告数量
        int count = advertiseMapper.selectCount(new LambdaQueryWrapper<Advertise>()
                .eq(Advertise::getMemberId, memberId)
                .eq(Advertise::getStatus, AdvertiseControlStatus.PUT_ON_SHELVES.getCode()));
        AssertUtil.isTrue(count == 0, OtcExceptionMsg.AFTER_OFF_ALL_SHELVES);
        //获取交易中订单数量
        count = otcOrderMapper.selectCount(new LambdaQueryWrapper<OtcOrder>()
                .or(o -> o.eq(OtcOrder::getCustomerId, memberId).or(o1 -> o1.eq(OtcOrder::getMemberId,memberId)))
                .ne(OtcOrder::getStatus, OrderStatus.CANCELLED.getCode())
                .ne(OtcOrder::getStatus, OrderStatus.CLOSE.getCode())
                .ne(OtcOrder::getStatus, OrderStatus.COMPLETED.getCode()));
        AssertUtil.isTrue(count == 0, OtcExceptionMsg.HAVE_ORDER_ON);
    }

    /**
     * 下架广告
     *
     * @param advertise
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = {"otcAdvertise"}, allEntries = true) //add by tansitao 时间： 2018/11/20 原因：清空OTC广告缓存
    public int putOffShelves(Advertise advertise) {
        checkPutOffEnable(advertise);
        if (advertise.getAdvertiseType().equals(AdvertiseType.SELL.getCode())) {
            OtcCoin otcCoin = otcCoinMapper.selectById(advertise.getCoinId());
            WalletTradeEntity reveive = new WalletTradeEntity();
            reveive.setMemberId(advertise.getMemberId());
            reveive.setType(TransactionType.PUT_OFF_SHELVES_FROZEN);
            reveive.setCoinUnit(otcCoin.getUnit());
            reveive.setTradeFrozenBalance(advertise.getRemainAmount().multiply(new BigDecimal(-1)));
            reveive.setTradeBalance(advertise.getRemainAmount());
            reveive.setComment(advertise.getMemberId() + "广告下架");
            MessageRespResult<Boolean> result = memberWalletService.trade(reveive);
            AssertUtil.notNull(result.getData(), OtcExceptionMsg.ACCOUNT_FROZEN_BALANCE_INSUFFICIENT);
        }
        advertise.setUpdateTime(new Date());
        advertise.setStatus(AdvertiseControlStatus.PUT_OFF_SHELVES.getCode());
        return advertiseMapper.putOffAdvertise(advertise.getId(),advertise.getRemainAmount());
    }

    /**
     * publishAdvertise=1 transactionStatus
     *
     * @param member
     * @param advertise
     */
    @Override
    public void checkMemberOpenEnble(Member member, Advertise advertise) {

        //add by zyj：publishAdvertise为0时禁止发布广告
        AssertUtil.isTrue(StringUtils.isEmpty(member.getTransactionStatus()) || member.getTransactionStatus() == BooleanEnum.IS_TRUE, OtcExceptionMsg.NO_ALLOW_TRANSACT);
        MsgCode message;
        if (AdvertiseType.BUY.getCode().equals(advertise.getAdvertiseType())) {
            message = OtcExceptionMsg.NO_ALLOW_TRANSACT_BUY;
        } else {
            message = OtcExceptionMsg.NO_ALLOW_TRANSACT_SELL;
        }
        log.info("广告类型为:{}", AdvertiseType.find(advertise.getAdvertiseType()).getMsg());
        validateOpenExPitTransaction(member.getId(), message, advertise.getAdvertiseType());
        AssertUtil.isTrue(StringUtils.isEmpty(member.getPublishAdvertise()) || member.getPublishAdvertise() == BooleanEnum.IS_TRUE, OtcExceptionMsg.NOT_ADVERTISING);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int modifyAdvertise(Advertise advertise, Advertise old) {
        if (PriceType.MUTATIVE.getCode().equals(advertise.getPriceType())) {
            //变化的
            old.setPriceType(PriceType.MUTATIVE.getCode());
            old.setPremiseRate(advertise.getPremiseRate());
        } else {
            //固定的
            old.setPriceType(PriceType.REGULAR.getCode());
            old.setPrice(advertise.getPrice());
        }
        if (BooleanEnum.IS_TRUE.getOrdinal() == advertise.getAuto()) {
            old.setAuto(BooleanEnum.IS_TRUE.getOrdinal());
            old.setAutoword(advertise.getAutoword());
        } else {
            old.setAuto(BooleanEnum.IS_FALSE.getOrdinal());
        }
        old.setMinLimit(advertise.getMinLimit());
        old.setMaxLimit(advertise.getMaxLimit());
        old.setTimeLimit(advertise.getTimeLimit());
        old.setRemark(advertise.getRemark());
        old.setPayMode(advertise.getPayMode());
        old.setNumber(advertise.getNumber());
        old.setRemainAmount(advertise.getNumber());
        //变更为下架状态
        old.setStatus(AdvertiseControlStatus.PUT_OFF_SHELVES.getCode());

        //add|edit|del by tansitao 时间： 2018/11/1 原因：交易平台1.3需求
        old.setNeedBindPhone(advertise.getNeedBindPhone());
        old.setNeedPutonDiscount(advertise.getNeedPutonDiscount());
        old.setNeedRealname(advertise.getNeedRealname());
        old.setNeedTradeTimes(advertise.getNeedTradeTimes());
        old.setMaxTradingOrders(advertise.getMaxTradingOrders()); //add by tansitao 时间： 2018/11/21 原因：增加同时最大交易数
        return advertiseMapper.updateById(old);

    }

    @Override
    public MemberAdvertiseDetail getDetail(Long id) {
        return advertiseMapper.getDetail(id);
    }

    @Override
//    @Cacheable(cacheNames = "otcAdvertise", key = "'entity:otcAdvertise:' + #coinId + '_' + #advertiseType + '_' + #advertiseRankType.getOrdinal() + '_' + #isPositive.getOrdinal() + '_' + #pageNo")
    public PageResult<OtcAdvertise> pageAdvertiseRank(Integer pageNo, Integer pageSize, Advertise advertise, BigDecimal marketPrice, AdvertiseRankType advertiseRankType, BooleanEnum isPositive, Integer coinScale) {
        Long total = advertiseMapper.getPageAdvertiseNum(advertise);
        List<OtcAdvertise> adList = advertiseMapper.getPageAdvertiseRank((pageNo - 1) * pageSize, pageSize, advertise, marketPrice, advertiseRankType.getCode(), isPositive.getOrdinal(), coinScale);
        return new PageResult<>(adList, pageNo, pageSize, total);
    }

    @Override
//    @Cacheable(cacheNames = "otcAdvertise", key = "'entity:otcAdvertise:' + #coinId + '_' + #advertiseType + '_' + #pageNo")
    public PageResult<OtcAdvertise> pageAdvertise(Integer pageNo, Integer pageSize, Advertise advertise, BigDecimal marketPrice, Integer coinScale) {
        Long total = advertiseMapper.getPageAdvertiseNum(advertise);
        List<OtcAdvertise> adList = advertiseMapper.getPageAdvertise((pageNo - 1) * pageSize, pageSize, advertise, marketPrice, coinScale);
        return new PageResult<>(adList, pageNo, pageSize, total);
    }

    /**
     * 获取商家所有广告
     *
     * @param member 商家
     * @return
     */
    @Override
    public MemberAdvertiseInfo getMemberAdvertise(Member member) {
        Advertise a = new Advertise();
        a.setMemberId(member.getId());
        a.setAdvertiseType(AdvertiseType.BUY.getCode());
        List<OtcAdvertise> buy = advertiseMapper.getPageAdvertise(0, 1000, a, BigDecimal.ONE, 1);
        a.setAdvertiseType(AdvertiseType.SELL.getCode());
        List<OtcAdvertise> sell = advertiseMapper.getPageAdvertise(0, 1000, a, BigDecimal.ONE, 1);
        return MemberAdvertiseInfo.builder()
                .createTime(member.getRegistrationTime())
                .emailVerified(StringUtils.isEmpty(member.getEmail()) ? IS_FALSE : IS_TRUE)
                .phoneVerified(StringUtils.isEmpty(member.getMobilePhone()) ? IS_FALSE : IS_TRUE)
                .realVerified(StringUtils.isEmpty(member.getRealName()) ? IS_FALSE : IS_TRUE)
                .transactions(member.getTransactions())
                .username(member.getUsername())
                .avatar(member.getAvatar())
                .memberLevel(member.getMemberLevel())
                .buy(buy.stream().map(advertise -> {
                    OtcCoin otcCoin = otcCoinMapper.selectById(advertise.getCoinId());
                    advertise.setUnit(otcCoin.getUnit());
                    if (!advertise.getPriceType().equals(PriceType.REGULAR.getCode())) {
                        BigDecimal markerPrice = currencyRateService.getCurrencyRate(advertise.getCurrencyUnit(), advertise.getUnit());
                        advertise.setPrice(mulRound(markerPrice, rate(advertise.getPremiseRate()), otcCoin.getCoinScale()));
                    }
                    return advertise;
                }).collect(Collectors.toList()))
                .sell(sell.stream().map(advertise -> {
                    OtcCoin otcCoin = otcCoinMapper.selectById(advertise.getCoinId());
                    advertise.setUnit(otcCoin.getUnit());
                    if (!advertise.getPriceType().equals(PriceType.REGULAR.getCode())) {
                        BigDecimal markerPrice = currencyRateService.getCurrencyRate(advertise.getCurrencyUnit(), advertise.getUnit());
                        advertise.setPrice(mulRound(markerPrice, rate(advertise.getPremiseRate()), otcCoin.getCoinScale()));
                    }
                    return advertise;
                }).collect(Collectors.toList()))
                .build();
    }

    /**
     * 广告上架
     *
     * @param advertise
     * @param member
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = {"otcAdvertise"}, allEntries = true) //add by tansitao 时间： 2018/11/20 原因：清空OTC广告缓存
    public void putOnShelves(Advertise advertise, Member member) {
        OtcCoin otcCoin = otcCoinMapper.selectById(advertise.getCoinId());
        //edit by tansitao 时间： 2018/10/25 原因：非商家广告需要扣除手续费
//        if (!member.getCertifiedBusinessStatus().equals(CertifiedBusinessStatus.VERIFIED)) {
//            //TODO 手续费归集
//            //普通用户开启折扣，并且币种开启折扣， 扣去普通用户需要收取手续费
//            if (advertise.getNeedPutonDiscount() == BooleanEnum.IS_TRUE.getOrdinal() && otcCoin.getGeneralDiscountRate().compareTo(BigDecimal.ZERO) > 0) {
//                //用户选择使用折扣优惠
//                //获取费率币种、折扣币种的当前法币价格
//                BigDecimal source = currencyRateService.getCurrencyPrice(advertise.getCurrencyId(), otcCoin.getGeneralFeeCoinUnit());
//                BigDecimal target = currencyRateService.getCurrencyPrice(advertise.getCurrencyId(), otcCoin.getGeneralDiscountCoinUnit());
//
//                log.info("======================手续费币种法币价格{}----{}=========================", source, otcCoin.getGeneralFeeCoinUnit());
//                log.info("======================手续费折扣币种法币价格{}----{}=========================", target, otcCoin.getGeneralDiscountCoinUnit());
//                AssertUtil.isTrue(target.compareTo(BigDecimal.ZERO) > 0, OtcExceptionMsg.PRICE_ERROR);
//
//                //使用优惠折扣,计算折扣后的数量
//                BigDecimal discountFreeBefor = PriceUtil.toRate(otcCoin.getGeneralFee(), otcCoin.getGeneralDiscountCoinScale(), source, target);
//                BigDecimal discountFreeAfter = discountFreeBefor.multiply(otcCoin.getGeneralDiscountRate()).setScale(otcCoin.getGeneralDiscountCoinScale(), BigDecimal.ROUND_DOWN);
//                log.info("======================打折过后的手续费{}{}=========================", discountFreeAfter, otcCoin.getGeneralFeeCoinUnit());
//
//                //操作用户钱包，扣除打折后的手续费,
//                BigDecimal realDiscountFree = BigDecimal.ZERO;
//                MessageRespResult<MemberWallet> discountMemberWalletResult = memberWalletService.getWalletByUnit(member.getId(), otcCoin.getGeneralDiscountCoinUnit());
//                MemberWallet discountMemberWallet = discountMemberWalletResult.getData();
//                //折扣币种余额足够，扣除折扣币种
//                if (discountMemberWallet.getBalance().compareTo(discountFreeAfter) >= 0) {
//                    realDiscountFree = discountFreeAfter;
//                } else {
//                    //折扣币种余额不足，进行混合扣除
//                    realDiscountFree = discountMemberWallet.getBalance();
//                    //edit by tansitao 时间： 2018/11/14 原因：修改进度处理
////                    BigDecimal notEnoughFree = otcCoin.getGeneralFee().subtract(realDiscountFree.multiply(target).divide(otcCoin.getGeneralDiscountRate()).setScale(otcCoin.getGeneralDiscountCoinScale(), BigDecimal.ROUND_UP));
//                    //add|edit|del by tansitao 时间： 2018/11/26 原因：修复bug，计算规则需要除以之前的价格------手续费-（钱包币种余额*价格/折扣率/源币种价格）
//                    BigDecimal notEnoughFree = otcCoin.getGeneralFee().subtract(realDiscountFree.multiply(target).divide(otcCoin.getGeneralDiscountRate(), otcCoin.getGeneralDiscountCoinScale(), BigDecimal.ROUND_UP).divide(source, otcCoin.getCoinScale(), BigDecimal.ROUND_UP));
//                    //add by tansitao 时间： 2018/12/11 原因：手续费为0则不记录资金记录
//                    if (notEnoughFree.compareTo(BigDecimal.ZERO) > 0) {
//                        log.info("======================折扣币种余额不足，进行混合扣除{}{}=========================", notEnoughFree, otcCoin.getGeneralFeeCoinUnit());
//                        //记录不享受折扣资金交易流水
//                        WalletTradeEntity reveive = new WalletTradeEntity();
//                        reveive.setType(TransactionType.ADVERTISE_FEE);
//                        reveive.setTradeBalance(notEnoughFree.multiply(new BigDecimal(-1)));
//                        reveive.setChangeType(WalletChangeType.TRADE);
//                        reveive.setMemberId(member.getId());
//                        reveive.setCoinUnit(otcCoin.getGeneralFeeCoinUnit());
//                        reveive.setRefId(advertise.getId() + "");
//                        reveive.setComment("用户" + member.getId() + "上架广告：" + advertise.getId() + " 收取手续费");
//
//                        //扣除用户不享受折扣的币种数量
//                        AssertUtil.isTrue(memberWalletService.tradeTccTry(reveive).isSuccess(), OtcExceptionMsg.INSUFFICIENT_BALANCE);
//
//                    }
//                }
//
//                //add by tansitao 时间： 2018/12/11 原因：手续费为0则不记录资金记录
//                if (realDiscountFree.compareTo(BigDecimal.ZERO) > 0) {
//                    //记录享受折扣的资金交易流水
//                    WalletTradeEntity reveive = new WalletTradeEntity();
//                    reveive.setType(TransactionType.ADVERTISE_FEE);
//                    reveive.setTradeBalance(realDiscountFree.multiply(new BigDecimal(-1)));
//                    reveive.setChangeType(WalletChangeType.TRADE);
//                    reveive.setMemberId(member.getId());
//                    reveive.setCoinUnit(otcCoin.getGeneralDiscountCoinUnit());
//                    reveive.setRefId(advertise.getId() + "");
//                    reveive.setComment("实时价格=" + target + "USDT");
//
//                    //扣除用户享受折扣的币种数量
//                    AssertUtil.isTrue(memberWalletService.tradeTccTry(reveive).isSuccess(), OtcExceptionMsg.INSUFFICIENT_BALANCE);
//                }
//
//            } else {
//                //用户不使用折扣优惠
//
//                //add by tansitao 时间： 2018/12/11 原因：手续费为0则不记录资金记录
//                if (otcCoin.getGeneralFee().compareTo(BigDecimal.ZERO) > 0) {
//                    //记录资金交易流水
//                    WalletTradeEntity reveive = new WalletTradeEntity();
//                    reveive.setType(TransactionType.ADVERTISE_FEE);
//                    reveive.setTradeBalance(otcCoin.getGeneralFee().multiply(new BigDecimal(-1)));
//                    reveive.setChangeType(WalletChangeType.TRADE);
//                    reveive.setMemberId(member.getId());
//                    reveive.setCoinUnit(otcCoin.getGeneralFeeCoinUnit());
//                    reveive.setRefId(advertise.getId() + "");
//                    reveive.setComment("上架广告扣除手续费");
//
//                    //操作用户钱包，扣除打折后的手续费
//                    AssertUtil.isTrue(memberWalletService.tradeTccTry(reveive).isSuccess(), OtcExceptionMsg.INSUFFICIENT_BALANCE);
//                }
//            }
//        }

        //操作用户用户，进行广告余额操作
        if (advertise.getAdvertiseType().equals(AdvertiseType.SELL.getCode())) {
            MessageRespResult<MemberWallet> memberWalletResult = memberWalletService.getWalletByUnit(member.getId(), otcCoin.getUnit());
            MemberWallet memberWallet = memberWalletResult.getData();
            AssertUtil.isTrue(BigDecimalUtils.compare(memberWallet.getBalance(), advertise.getNumber()), OtcExceptionMsg.INSUFFICIENT_BALANCE);
            AssertUtil.isTrue(advertise.getNumber().compareTo(otcCoin.getSellMinAmount()) >= 0, OtcExceptionMsg.SELL_NUM_TOO_LITTLE);

            //冻结钱包
            WalletTradeEntity reveive = new WalletTradeEntity();
            reveive.setMemberId(advertise.getMemberId());
            reveive.setType(TransactionType.PUT_ON_SHELVES_FROZEN);
            reveive.setCoinUnit(otcCoin.getUnit());
            reveive.setTradeFrozenBalance(advertise.getNumber());
            reveive.setTradeBalance(advertise.getRemainAmount().multiply(new BigDecimal(-1)));
            reveive.setComment(advertise.getMemberId() + "广告上架");
            MessageRespResult<Boolean> result = memberWalletService.trade(reveive);
            AssertUtil.isTrue(result.getData(), OtcExceptionMsg.INSUFFICIENT_BALANCE);
        } else {
            AssertUtil.isTrue(advertise.getNumber().compareTo(otcCoin.getBuyMinAmount()) >= 0, OtcExceptionMsg.BUY_NUM_TOO_LITTLE);
        }
        advertise.setRemainAmount(advertise.getNumber());
        advertise.setStatus(AdvertiseControlStatus.PUT_ON_SHELVES.getCode());
        updateById(advertise);
    }

    /**
     * 自动下架余额不足的广告
     * @return
     */
    @Override
    public Map<String, List<Long>> autoPutOffShelvesAdvertise() {
        Map<String,List<Long>> result = Maps.newHashMap();
        List<Long> success = new ArrayList<>();
        List<Long> fail = new ArrayList<>();
        //获取所有法币和交易币
        List<CurrencyManage> currencyManageList = currencyManageMapper.selectList(new LambdaQueryWrapper<CurrencyManage>().eq(CurrencyManage::getCurrencyState,1));
        List<OtcCoin> coinList = otcCoinMapper.selectOpenOtcCoin();
        coinList.stream().forEach(coin ->
            currencyManageList.stream().forEach(currencyManage -> {
                //获取法币价格
                BigDecimal rage = currencyRateService.getCurrencyRate(currencyManage.getUnit(),coin.getUnit());
                //根据法币价格查询应该下架的出售广告
                List<Advertise> adList = selectSellAutoOffShelves(coin.getId(),rage,coin.getJyRate(),currencyManage.getId());
                //根据法币价格查询应该下架的购买广告
                adList.addAll(selectBuyAutoOffShelves(coin.getId(),rage,currencyManage.getId()));
                //下架所有需要下架的广告
                adList.stream().forEach(advertise -> {
                    log.info("自动下架广告：{}",advertise.getId());
                    //下架广告
                    int a = putOffShelves(advertise);
                    if(a <= 0){
                        fail.add(advertise.getId());
                    }else{
                        success.add(advertise.getId());
                    }
                });
            }));
        result.put("success",success);
        result.put("fail",fail);
        return result;
    }


    /**
     * 得到出售类型自动下架的广告
     *
     * @param coinId
     * @param marketPrice
     * @return
     */
    public List<Advertise> selectSellAutoOffShelves(long coinId, BigDecimal marketPrice, BigDecimal jyRate,Long currencyId){

        return advertiseMapper.selectSellAutoOffShelves(coinId,marketPrice,jyRate,currencyId);
    }

    /**
     * 得到购买类型自动下架的广告
     *
     * @param coinId
     * @param marketPrice
     * @return
     */
    public List<Advertise> selectBuyAutoOffShelves(long coinId, BigDecimal marketPrice,Long currencyId){

        return advertiseMapper.selectBuyAutoOffShelves(coinId,marketPrice,currencyId);
    }

    /**
     * 获取用户安全控制
     *
     * @param memberId
     * @return
     */
    public MemberSecuritySet getMemberSecuritySet(Long memberId) {
        MessageRespResult<MemberSecuritySet> result = iMemberApiService.getMemberSecuritySet(memberId);
        return result.getData();
    }
}
