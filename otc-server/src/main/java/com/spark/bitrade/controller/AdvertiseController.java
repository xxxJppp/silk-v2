package com.spark.bitrade.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.spark.bitrade.common.OrderComparator;
import com.spark.bitrade.common.RedisUtil;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constant.MemberLevelEnum;
import com.spark.bitrade.constant.SysConstant;
import com.spark.bitrade.entity.*;
import com.spark.bitrade.entity.vo.*;
import com.spark.bitrade.enums.*;
import com.spark.bitrade.pagination.PageResult;
import com.spark.bitrade.service.*;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.BindingResultUtil;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.util.MessageResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.spark.bitrade.util.BigDecimalUtils.mulRound;


/**
 * (Advertise)表控制层
 *
 * @author ss
 * @date 2020-03-19 10:22:05
 */
@RestController
@RequestMapping("api/v2/advertise")
@Api(tags = "广告相关接口")
@Slf4j
public class AdvertiseController extends ApiController {

    @Resource
    private AdvertiseService advertiseService;
    @Resource
    private OtcCoinService otcCoinService;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private OtcOrderService otcOrderService;
    @Resource
    private CurrencyRateService rateService;
    @Resource
    private CurrencyManageService currencyManageService;
    @Resource
    private IMemberApiService memberApiService;


    /**
     * 创建广告
     *
     * @param advertise
     * @param bindingResult
     * @param member
     * @param pay
     * @param jyPassword
     * @return
     * @throws Exception
     */
    @PostMapping(value = "create")
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(value = "广告发布接口", notes = "广告发布接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "交易方式，用逗号隔开(payKey)", name = "pay[]", required = true),
            @ApiImplicitParam(value = "资金密码", name = "jyPassword", required = true)
    })
    public MessageRespResult create(@Valid Advertise advertise, BindingResult bindingResult, @MemberAccount Member member,
                                    @RequestParam(value = "pay[]") String[] pay, String jyPassword) {
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return failed(result.getMessage());
        }
        OtcCoin otcCoin;
        if(advertise.getCoinId() == null){
            otcCoin = otcCoinService.getOne(new LambdaQueryWrapper<OtcCoin>().eq(OtcCoin::getUnit,advertise.getCoinUnit()));
        }else{
            otcCoin = otcCoinService.getById(advertise.getCoinId());
        }
        adCheck(advertise,member,pay,otcCoin,jyPassword);
        advertiseService.checkAmount(advertise, otcCoin, member);
        advertise.setLevel(AdvertiseLevel.ORDINARY.getCode());
        advertise.setCoinUnit(otcCoin.getUnit());
        advertise.setStatus(AdvertiseControlStatus.PUT_OFF_SHELVES.getCode());
        advertise.setCoinId(otcCoin.getId());
        advertise.setRemainAmount(advertise.getNumber());
        advertise.setMemberId(member.getId());
        advertise.setCreateTime(new Date());
        advertise.setDealAmount(BigDecimal.ZERO);
        advertise.setUpdateTime(new Date());
        //TODO
        advertise.setVersion(1L);
        boolean ad = advertiseService.save(advertise);
        if (ad) {
            return success(OtcExceptionMsg.CREATE_SUCCESS.getMessage());
        } else {
            return failed(OtcExceptionMsg.CREATE_FAILED);
        }
    }


    /**
     * 修改广告
     *
     * @param advertise 广告{@link Advertise}
     * @return {@link MessageResult}
     */
    @ApiOperation(value = "修改广告", notes = "修改广告")
    @PostMapping(value = "update")
    @Transactional(rollbackFor = Exception.class)
    @ApiImplicitParams({
            @ApiImplicitParam(value = "交易方式，用逗号隔开(payKey)", name = "pay[]", required = true),
            @ApiImplicitParam(value = "资金密码", name = "jyPassword", required = true)
    })
    public MessageRespResult update(
            @Valid Advertise advertise,
            BindingResult bindingResult, @MemberAccount Member member,
            @RequestParam(value = "pay[]") String[] pay, String jyPassword) {
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return failed(result.getMessage());
        }
        OtcCoin newotcCoin;
        if(advertise.getCoinId() == null){
            newotcCoin = otcCoinService.getOne(new LambdaQueryWrapper<OtcCoin>().eq(OtcCoin::getUnit,advertise.getCoinUnit()));
        }else{
            newotcCoin = otcCoinService.getById(advertise.getCoinId());
        }
        adCheck(advertise,member,pay,newotcCoin,jyPassword);
        Advertise old = advertiseService.getById(advertise.getId());
        AssertUtil.notNull(old, OtcExceptionMsg.UPDATE_FAILED);
        AssertUtil.isTrue(AdvertiseControlStatus.PUT_OFF_SHELVES.getCode().equals(old.getStatus()), OtcExceptionMsg.AFTER_OFF_SHELVES);
        OtcCoin otcCoin = otcCoinService.getById(old.getCoinId());
        //TODO 是否不允许修改广告类型？？？
        advertiseService.checkAmount(advertise, otcCoin, member);
        advertise.setUpdateTime(new Date());
        int ad = advertiseService.modifyAdvertise(advertise, old);
        if (ad == 0) {
            return failed(OtcExceptionMsg.UPDATE_FAILED);
        } else {
            return success(OtcExceptionMsg.UPDATE_SUCCESS);
        }
    }

    public void adCheck(Advertise advertise, Member member, String[] pay, OtcCoin otcCoin,String jyPassword) {
        //判断普通用户是否可以发布广告
        AssertUtil.isTrue (!(member.getMemberLevel() != MemberLevelEnum.IDENTIFICATION && !advertiseService.getAdvertiseConfig()),OtcExceptionMsg.CAN_NOT_PUBLISH);
        //add by tansitao 时间： 2018/11/14 原因：最大交易额限制
        if (PriceType.REGULAR.getCode().equals(advertise.getPriceType()) && AdvertiseType.SELL.getCode().equals(advertise.getAdvertiseType())) {
            BigDecimal maxLimit = advertise.getNumber().multiply(advertise.getPrice());
            AssertUtil.isTrue(maxLimit.compareTo(advertise.getMaxLimit()) >= 0, OtcExceptionMsg.max_trade_limit);

        }
        AssertUtil.isTrue(advertise.getMinLimit().compareTo(advertise.getMaxLimit()) <= 0, OtcExceptionMsg.max_trade_limit);
        //判断发布广告法币与默认法币是否一致，如果是CNY必须绑定银行卡
        MessageRespResult<CurrencyManage> baseCurrency = currencyManageService.getMemberPaySetting(member);
        CurrencyManage base = baseCurrency.getData();
        AssertUtil.notNull(base,OtcExceptionMsg.MEMBER_HAS_NO_BASE_CURRENCY);
        AssertUtil.notNull(advertise.getCurrencyId(),OtcExceptionMsg.MISSING_CURRENCY_ID);
        AssertUtil.isTrue(base.getId().equals(advertise.getCurrencyId()),OtcExceptionMsg.CURRENCY_NOT_EQUALS_BASE_CURRENCY);

        AssertUtil.isTrue(Arrays.asList(otcCoin.getCurrencyId().split(",")).contains(advertise.getCurrencyId().toString()),OtcExceptionMsg.BASE_CURRENCY_NOT_SUPPORT_OTC_COIN);
        AssertUtil.notNull(otcCoin,OtcExceptionMsg.OTC_COIN_NOT_EXIST);
        //add by tansitao 时间： 2018/11/12 原因：如果等于cnyt则价格必须唯一
        //edit by ss 时间： 2020/03/31 原因：如果等于EURT则价格必须唯一
        if ("CNYT".equals(otcCoin.getUnit()) || "CNYT".equals(otcCoin.getUnit())) {
            //add by tansitao 时间： 2018/12/14 原因：CNYT不能为溢价
            AssertUtil.isTrue(!PriceType.MUTATIVE.getCode().equals(advertise.getPriceType()), OtcExceptionMsg.MUST_CHANGE);
            AssertUtil.isTrue(advertise.getPrice() != null && advertise.getPrice().compareTo(BigDecimal.ONE) == 0, OtcExceptionMsg.CNYT_MUST_EQ_ONE);
        }
        //priceType=0时，premise_rate设置为0
        if(advertise.getPriceType().equals(PriceType.REGULAR)){
            advertise.setPremiseRate(BigDecimal.ZERO);
        }

        AssertUtil.notEmpty(pay, OtcExceptionMsg.MISSING_PAY);
        AssertUtil.hasText(jyPassword, OtcExceptionMsg.MISSING_JYPASSWORD);
        //判断用户发布广告的权限是否满足
        advertiseService.checkMemberOpenEnble(member, advertise);
        //资金密码验证
        String mbPassword = member.getJyPassword();
        AssertUtil.hasText(mbPassword, OtcExceptionMsg.NO_SET_JYPASSWORD);
        String jyPass = new SimpleHash("md5", jyPassword, member.getSalt(), 2).toHex().toLowerCase();
        AssertUtil.isTrue(jyPass.equals(mbPassword), OtcExceptionMsg.ERROR_JYPASSWORD);

        if (AdvertiseType.BUY.getCode().equals(advertise.getAdvertiseType())) {
            //add by tansitao 时间： 2018/11/1 原因：买币广告增加默认数量
            advertise.setNumber(BigDecimal.valueOf(1000000L));
            //add by tansitao 时间： 2018/11/6 原因：增加发布广告限制
            if (!member.getMemberLevel().equals(MemberLevelEnum.IDENTIFICATION)) {
                MemberWallet memberWallet = advertiseService.getWalletByUnit(otcCoin.getUnit(), member.getId());
                AssertUtil.isTrue(memberWallet.getBalance().compareTo(otcCoin.getGeneralBuyMinBalance()) >= 0, OtcExceptionMsg.INSUFFICIENT_BALANCE);
            }
        }
        StringBuffer payMode = advertiseService.checkPayMode(pay, advertise, member,base);
        advertise.setPayMode(payMode.toString());
    }


    /**
     * 我的广告列表
     *
     * @param member
     * @param pageNo   页码1开始
     * @param pageSize 每页数量
     * @return
     */
    @PostMapping(value = "all")
    @ApiOperation(value = "我的广告", notes = "我的广告")
    @ApiImplicitParams({@ApiImplicitParam(name = "pageNo", required = true, dataType = "int", paramType = "query", value = "页码1开始"),
            @ApiImplicitParam(name = "pageSize", required = true, dataType = "int", paramType = "query", value = "每页数量")})
    public MessageRespResult<PageResult<AdvertiseVo>> allNormal(@MemberAccount Member member, @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
                                                                @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        PageResult<AdvertiseVo> result = advertiseService.getAllAdvertiseByMemberId(member.getId(), pageNo, pageSize);
        List<AdvertiseVo> list = result.getContent();
        //add|edit|del by tansitao 时间： 2018/5/19 原因：修改我的广告当价格为溢价时，显示错误的问题
        if (list != null) {
            Map<String, LinkedList<CurrencyRateData>> marketPriceResult = rateService.getCurrencyRateList();
            BigDecimal marketPrice = BigDecimal.ZERO;
            for (AdvertiseVo memberAdvertise : list) {
                if (memberAdvertise != null) {
                    OtcCoin otcCoin = otcCoinService.getById(memberAdvertise.getCoinId());
                    memberAdvertise.setCoinUnit(otcCoin.getUnit());
                    if (PriceType.MUTATIVE.getCode().equals(memberAdvertise.getPriceType())) {
                        List<CurrencyRateData> linkedList = marketPriceResult.get(memberAdvertise.getCoinUnit());
                        for(CurrencyRateData item : linkedList){
                            if(item.getSymbol().equals(memberAdvertise.getCurrencyUnit())){
                                marketPrice = item.getPrice();
                                break;
                            }
                        }
                        BigDecimal premiseRate = memberAdvertise.getPremiseRate().divide(new BigDecimal(100), otcCoin.getCoinScale(), BigDecimal.ROUND_HALF_UP);
                        if (AdvertiseType.SELL.getCode().equals(memberAdvertise.getAdvertiseType())) {
                            BigDecimal price = mulRound(BigDecimal.ONE.add(premiseRate), marketPrice, 2);
                            memberAdvertise.setPrice(price);
                        } else {
                            BigDecimal price = mulRound(BigDecimal.ONE.subtract(premiseRate), marketPrice, 2);
                            memberAdvertise.setPrice(price);
                        }
                    }
                }

            }
        }
        return success(result);
    }

    /**
     * 获取商家所有广告
     * @param userId
     * @return
     */
    @ApiOperation(value = "获取商家所有广告")
    @RequestMapping(value = "/no-auth/member", method = RequestMethod.POST)
    @ApiImplicitParam(value = "商家ID",required = true,name = "userId", dataType = "long", paramType = "query")
    public MessageRespResult<MemberAdvertiseInfo> memberAdvertises(Long userId) {
        MessageRespResult<Member> memberResult = memberApiService.getMember(userId);
        Member member = memberResult.getData();
        if (member != null) {

            MemberAdvertiseInfo memberAdvertise = advertiseService.getMemberAdvertise(member);
            // TODO 临时过滤BT和DCC币种
            List<String> exclude = Collections.singletonList("DCC");
            List<OtcAdvertise> buy = memberAdvertise.getBuy().stream().filter(i -> !exclude.contains(i.getUnit())).collect(Collectors.toList());
            List<OtcAdvertise> sell = memberAdvertise.getSell().stream().filter(i -> !exclude.contains(i.getUnit())).collect(Collectors.toList());
            memberAdvertise.setBuy(buy);
            memberAdvertise.setSell(sell);
            return success(memberAdvertise);
        } else {
            return failed(OtcExceptionMsg.MEMBER_NOT_EXISTS);
        }
    }


    /**
     * 广告上架
     *
     * @param id
     * @return
     */
    @PostMapping(value = "/on/shelves")
    @ApiOperation(value = "广告上架", notes = "广告上架")
    public MessageRespResult putOnShelves(long id, @MemberAccount Member member) {
        Advertise advertise = advertiseService.getOne(new LambdaQueryWrapper<Advertise>().eq(Advertise::getId, id).eq(Advertise::getMemberId, member.getId()));
        //判断广告是否存在
        AssertUtil.isTrue(advertise != null, OtcExceptionMsg.PUT_ON_SHELVES_FAILED);

        //判断广告原来状态是否为下架
        AssertUtil.isTrue(AdvertiseControlStatus.PUT_OFF_SHELVES.getCode().equals(advertise.getStatus()), OtcExceptionMsg.PUT_ON_SHELVES_FAILED);

        //判断用户的默认法币跟广告是否对应
        AssertUtil.isTrue(currencyManageService.getMemberPaySetting(member).getData().getId().equals(advertise.getCurrencyId()),OtcExceptionMsg.BASE_CURRENCY_UPDATED);

        //判断普通用户是否可以发布广告
        if (member.getMemberLevel() != MemberLevelEnum.IDENTIFICATION && !advertiseService.getAdvertiseConfig()) {
            return failed(OtcExceptionMsg.CAN_NOT_PUBLISH);
        }

        //判断用户发布广告的权限是否满足
        advertiseService.checkMemberOpenEnble(member, advertise);
        //TODO 判断默认法币是否匹配，若为CNY必须绑定实名银行卡
        if (AdvertiseType.BUY.getCode().equals(advertise.getAdvertiseType())) {
            //add by tansitao 时间： 2018/11/1 原因：买币广告增加默认数量
            advertise.setNumber(BigDecimal.valueOf(1000000L));
            //add by tansitao 时间： 2018/11/6 原因：增加发布广告限制
            if (!member.getMemberLevel().equals(MemberLevelEnum.IDENTIFICATION)) {
                OtcCoin otcCoin = otcCoinService.getById(advertise.getCoinId());
                MemberWallet memberWallet = advertiseService.getWalletByUnit(otcCoin.getUnit(), member.getId());
                AssertUtil.isTrue(memberWallet.getBalance().compareTo(otcCoin.getGeneralBuyMinBalance()) >= 0, OtcExceptionMsg.INSUFFICIENT_BALANCE);
            }
        }

        //edit by tansitao 时间： 2018/10/25 原因：非商家广告需要扣除手续费

        advertiseService.putOnShelves(advertise, member);

        return success(OtcExceptionMsg.PUT_ON_SHELVES_SUCCESS);
    }

    /**
     * 广告下架
     *
     * @param id
     * @return
     */
    @PostMapping(value = "/off/shelves")
    @ApiOperation(value = "广告下架", notes = "广告下架")
    @ApiImplicitParam(value = "广告ID",required = true,name = "id", dataType = "long", paramType = "query")
    public MessageRespResult putOffShelves(long id, @MemberAccount Member member) throws Exception {
        Advertise advertise = advertiseService.getOne(new LambdaQueryWrapper<Advertise>().eq(Advertise::getId, id).eq(Advertise::getMemberId, member.getId()));
        //判断广告是否存在
        AssertUtil.isTrue(advertise != null, OtcExceptionMsg.PUT_OFF_SHELVES_FAILED);
        //判断广告原来状态是否为上架
        AssertUtil.isTrue(AdvertiseControlStatus.PUT_ON_SHELVES.getCode().equals(advertise.getStatus()), OtcExceptionMsg.PUT_OFF_SHELVES_FAILED);
        //下架广告逻辑
        int ret = advertiseService.putOffShelves(advertise);
        AssertUtil.isTrue(ret > 0, OtcExceptionMsg.PUT_OFF_SHELVES_FAILED);
        return success(OtcExceptionMsg.PUT_OFF_SHELVES_SUCCESS);
    }


    /**
     * 删除广告
     *
     * @param id
     * @return
     */
    @PostMapping(value = "delete")
    @ApiOperation(value = "删除广告", notes = "删除广告")
    @Transactional(rollbackFor = Exception.class)
    @ApiImplicitParam(value = "广告ID",required = true,name = "id", dataType = "long", paramType = "query")
    public MessageRespResult delete(Long id, @MemberAccount Member member) {
        Advertise advertise = advertiseService.getOne(new LambdaQueryWrapper<Advertise>().eq(Advertise::getId, id).eq(Advertise::getMemberId, member.getId()));
        AssertUtil.notNull(advertise, OtcExceptionMsg.DELETE_ADVERTISE_FAILED);
        //add by tansitao 时间： 2018/11/12 原因：取消判断用户是否为商家状态
//        Assert.isTrue(advertise.getMember().getCertifiedBusinessStatus().equals(CertifiedBusinessStatus.VERIFIED) , msService.getMessage("DELETE_ADVERTISE_FAILED"));
        AssertUtil.isTrue(advertise.getStatus().equals(AdvertiseControlStatus.PUT_OFF_SHELVES.getCode()) || advertise.getStatus().equals(AdvertiseControlStatus.FAILURE.getCode()),OtcExceptionMsg.DELETE_AFTER_OFF_SHELVES);
        advertise.setStatus(AdvertiseControlStatus.TURNOFF.getCode());
        advertise.setUpdateTime(new Date());

        return advertiseService.updateById(advertise) ? success(OtcExceptionMsg.DELETE_ADVERTISE_SUCCESS) : failed(OtcExceptionMsg.DELETE_ADVERTISE_FAILED);
    }

    /**
     * 广告详情
     *
     * @param id
     * @return
     */
    @PostMapping(value = "detail")
    @ApiOperation(value = "广告详情", notes = "广告详情")
    @ApiImplicitParam(name = "id", required = true, dataType = "long", paramType = "query", value = "广告ID")
    public MessageRespResult<MemberAdvertiseDetail> detail(Long id) {
        MemberAdvertiseDetail advertise = advertiseService.getDetail(id);
        AssertUtil.notNull(advertise,OtcExceptionMsg.AD_NOT_EXIST);
        BigDecimal marketPrice = rateService.getCurrencyRate(advertise.getCurrencyUnit(), advertise.getCoinUnit());
        advertise.setMarketPrice(marketPrice);
        return success(advertise);
    }

    /**
     * 广告列表
     *
     * @param pageNo            页码1开始
     * @param pageSize          每页数量
     * @param advertise
     * @param advertiseRankType
     * @param isPositive
     * @return
     */
    @PostMapping(value = "/no-auth/getAllAdvertiseList")
    @ApiOperation(value = "广告列表", notes = "广告列表")
    @ApiImplicitParams({@ApiImplicitParam(name = "pageNo", required = true, dataType = "int", paramType = "query", value = "页码1开始"),
            @ApiImplicitParam(name = "pageSize", required = true, dataType = "int", paramType = "query", value = "每页数量")})
    public MessageRespResult<PageResult<OtcAdvertise>> getAllAdvertiseList(@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
                                                                           @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                                           Advertise advertise, AdvertiseRankType advertiseRankType, BooleanEnum isPositive) {
        AssertUtil.isTrue(advertise.getCurrencyId() != null, OtcExceptionMsg.MISSING_CURRENCY_ID);
        AssertUtil.notNull(advertise.getCoinId(), OtcExceptionMsg.MISSING_OTC_COIN_ID);
        OtcCoin otcCoin;
        if(advertise.getCoinId() == null){
            otcCoin = otcCoinService.getOne(new LambdaQueryWrapper<OtcCoin>().eq(OtcCoin::getUnit,advertise.getCoinUnit()));
        }else{
            otcCoin = otcCoinService.getById(advertise.getCoinId());
        }
        AssertUtil.notNull(otcCoin, OtcExceptionMsg.OTC_COIN_NOT_EXIST);
        AssertUtil.notNull(advertise.getAdvertiseType(), OtcExceptionMsg.MISSING_ADVERTISE_TYPE);
        CurrencyManage currency = currencyManageService.getById(advertise.getCurrencyId());
        AssertUtil.notNull(currency, OtcExceptionMsg.CURRENCY_NOT_EXIST);
        BigDecimal marketPrice = rateService.getCurrencyRate(currency.getUnit(), otcCoin.getUnit());
        PageResult<OtcAdvertise> result;
        if (advertiseRankType != null) {
            //add by tansitao 时间： 2018/10/26 原因：添加精度
            result = advertiseService.pageAdvertiseRank(pageNo, pageSize, advertise, marketPrice, advertiseRankType, isPositive, otcCoin.getCoinScale());
        } else {
            //add by tansitao 时间： 2018/10/26 原因：添加精度
            result = advertiseService.pageAdvertise(pageNo, pageSize, advertise, marketPrice, otcCoin.getCoinScale());
        }
        result.getContent().forEach(otcAdvertise -> {
            //获取币种信息
            otcAdvertise.setCoinName(otcCoin.getName());
            otcAdvertise.setUnit(otcCoin.getUnit());
            otcAdvertise.setCoinNameCn(otcCoin.getNameCn());
            //add by tansitao 时间： 2018/11/20 原因：优化，计算溢价时广告的价格
            if (PriceType.MUTATIVE.getCode().equals(otcAdvertise.getPriceType())) {
                BigDecimal premiseRate = otcAdvertise.getPremiseRate().divide(BigDecimal.valueOf(100), otcCoin.getCoinScale(), BigDecimal.ROUND_HALF_UP);
                if (AdvertiseType.SELL.getCode().equals(otcAdvertise.getAdvertiseType())) {
                    premiseRate = premiseRate.add(BigDecimal.ONE);
                } else if (AdvertiseType.BUY.getCode().equals(otcAdvertise.getAdvertiseType())) {
                    premiseRate = BigDecimal.ONE.subtract(premiseRate);
                }
                otcAdvertise.setPrice(premiseRate.multiply(marketPrice));
            }
            //add by tansitao 时间： 2018/11/20 原因：从redis中获取交易中的订单数
            Integer onlineNum = (Integer) redisUtil.getHash(SysConstant.C2C_MONITOR_ORDER + otcAdvertise.getMemberId() + "-" + otcAdvertise.getAdvertiseId(), SysConstant.C2C_ONLINE_NUM);
            otcAdvertise.setTradingOrderNume(onlineNum == null ? 0 : onlineNum);
        });

        List<OtcAdvertise> otcAdvertises = doSort(result.getContent(), AdvertiseType.find(advertise.getAdvertiseType()), advertiseRankType);
        result.setContent(otcAdvertises);
        return success(result);
    }

    /**
     * 定时任务自动余额不足的下架广告
     * @return
     */
    @PostMapping("/autoPutOffShelvesAdvertise")
    public MessageRespResult<Map<String,List<Long>>> autoPutOffShelvesAdvertise(){
        return success(advertiseService.autoPutOffShelvesAdvertise());
    }

    /**
     * 新增排序规则
     * 1、正在进行中的订单少的
     * 2、48小时內接单次数少的
     * 3、48小时內接单金额小的
     */
    private List<OtcAdvertise> doSort(List<OtcAdvertise> advertises, AdvertiseType type, AdvertiseRankType advertiseRankType) {
        Set<Long> sids = advertises.stream().map(a -> a.getMemberId()).collect(Collectors.toSet());
        List<MemberOrderCount> memberOrderCounts = new ArrayList<>();
        advertises.forEach(aa -> memberOrderCounts.add(new MemberOrderCount().setMemberId(aa.getMemberId())
                .setPrice(aa.getPrice()).setHasTrade(aa.getTransactions()).setAdverId(aa.getAdvertiseId()).setSort(aa.getSort())));
        //正在进行中订单数
        List<Map<String, Long>> tradings = otcOrderService.selectCountByMembers(sids.toArray(new Long[0]), type);
        //商家48小时的接单数 和金额
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -2);
        List<Map<String, Long>> count48 = otcOrderService.selectCountByMembersAnd48(sids.toArray(new Long[0]), type, calendar.getTime());
        for (MemberOrderCount count : memberOrderCounts) {
            Long memberId = count.getMemberId();
            for (Map<String, Long> map : tradings) {
                Long m = map.get("member_id");
                if (memberId.equals(m)) {
                    count.setTradingCounts(map.get("count"));
                }
            }

            for (Map<String, Long> mp : count48) {
                Long m = mp.get("member_id");
                if (memberId.equals(m)) {
                    Long count1 = mp.get("count");
                    count.setCount48(count1);
                    Object money = mp.get("_money");
                    count.setMoney48(new BigDecimal(String.valueOf(money)));
                }
            }

            Long tradingCounts = count.getTradingCounts();
            count.setTradingCounts(tradingCounts == null ? 0L : tradingCounts);
            Long count481 = count.getCount48();
            count.setCount48(count481 == null ? 0L : count481);
            BigDecimal money48 = count.getMoney48();
            count.setMoney48(money48 == null ? BigDecimal.ZERO : money48);

        }

        Comparator comparator = new OrderComparator(advertiseRankType);
        memberOrderCounts.sort(comparator);
        List<OtcAdvertise> ads = new ArrayList<>();
        for (MemberOrderCount c : memberOrderCounts) {
            Long adId = c.getAdverId();
            Iterator<OtcAdvertise> iterator = advertises.iterator();
            while (iterator.hasNext()) {
                OtcAdvertise next = iterator.next();
                Long m2 = next.getAdvertiseId();
                if (adId.equals(m2)) {
                    ads.add(next);
                    iterator.remove();
                }
            }
        }

        return ads;
    }

}
