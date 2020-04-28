package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constant.WalletChangeType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.constants.LSMsgCode;
import com.spark.bitrade.entity.*;
import com.spark.bitrade.mapper.*;
import com.spark.bitrade.service.*;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.ExceptionUitl;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.util.SpringContextUtil;
import com.spark.bitrade.vo.IocoPurchaseTransactionVo;
import com.spark.bitrade.vo.IocoPurchaseVo;
import com.spark.bitrade.vo.MemberBalance;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * ioco钱包交易记录(IocoMemberTransaction)表服务实现类
 *
 * @author daring5920
 * @since 2019-07-03 14:38:58
 */
@Service("iocoMemberTransactionService")
public class IocoMemberTransactionServiceImpl extends ServiceImpl<IocoMemberTransactionMapper, IocoMemberTransaction> implements IocoMemberTransactionService {

    @Autowired
    private IocoActivityConfigService activityConfigService;

    @Autowired
    private IocoMemberWalletService iocoMemberWalletService;

    @Autowired
    private IocoActivityWalletService iocoActivityWalletService;

    @Autowired
    private IocoActivityRuleService iocoActivityRuleService;


    @Autowired
    private IMemberApiService iMemberApiService;

    @Autowired
    private IMemberWalletApiService iMemberWalletApiService;

    /**
     * 获取ioco首页数据
     *
     * @param memberId
     * @return true
     * @author shenzucai
     * @time 2019.07.03 17:04
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public IocoPurchaseVo getIocoIndexData(Long memberId) {

        // 1，查询出当前活动配置,生效的活动同一时间只能有一个
        QueryWrapper<IocoActivityConfig> configQueryWrapper = new QueryWrapper<IocoActivityConfig>().eq("status", 1);
        IocoActivityConfig iocoActivityConfig = activityConfigService.getOne(configQueryWrapper);
        // AssertUtil.isTrue(iocoActivityConfig != null, LSMsgCode.NULL_IOCO);

        // 2,获取推荐人数的信息
        MessageRespResult<SlpMemberPromotion> promotionMessageRespResult = iMemberApiService.getSlpMemberPromotion(memberId);
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(promotionMessageRespResult);
        SlpMemberPromotion slpMemberPromotion = promotionMessageRespResult.getData();

        // AssertUtil.isTrue(slpMemberPromotion != null, LSMsgCode.NOT_FIND_SLP_PROMOTION);

        // 3,获取用户slp余额
        QueryWrapper<IocoMemberWallet> memberWalletQueryWrapper = new QueryWrapper<IocoMemberWallet>().eq("member_id", memberId).eq("unit", "SLP");
        IocoMemberWallet iocoMemberWallet = iocoMemberWalletService.getOne(memberWalletQueryWrapper);
        // 3.1 如果没有slp余额账户，则进行创建
        if (Objects.isNull(iocoMemberWallet)) {
            iocoMemberWallet = new IocoMemberWallet();
            iocoMemberWallet.setAmount(BigDecimal.ZERO);
            iocoMemberWallet.setMemberId(memberId);
            iocoMemberWallet.setUnit("SLP");
            AssertUtil.isTrue(iocoMemberWalletService.save(iocoMemberWallet), LSMsgCode.IOCO_EXCEPTION);
        }
        IocoActivityWallet iocoActivityWallet = null;
        if(!Objects.isNull(iocoActivityConfig)) {
            // 4,获取当前活动可申购的详细信息 一个用户在当前活动中只能有一条记录
            QueryWrapper<IocoActivityWallet> activityWalletQueryWrapper = new QueryWrapper<IocoActivityWallet>().eq("member_id", memberId)
                    .eq("unit", "SLP")
                    .eq("activity_id", iocoActivityConfig.getId());

            iocoActivityWallet = iocoActivityWalletService.getOne(activityWalletQueryWrapper);
        }

        // 4.1 如果没有slp活动账户，则进行创建
        if (Objects.isNull(iocoActivityWallet) && !Objects.isNull(slpMemberPromotion) && !Objects.isNull(iocoActivityConfig)) {
            // 4.2 根据活动配置和邀请关系给用户定级
            IocoActivityRule iocoActivityRule = iocoActivityRuleService.getActivityRuleByTotalCount(slpMemberPromotion.getAllCount(), iocoActivityConfig.getId());
            // AssertUtil.isTrue( !Objects.isNull(iocoActivityRule),LSMsgCode.IOCO_RECOMMEND_NOT_ENOUGH);
            if (!Objects.isNull(iocoActivityRule)) {
                iocoActivityWallet = new IocoActivityWallet();
                iocoActivityWallet.setMinAmount(iocoActivityRule.getMinSlpAmount());
                iocoActivityWallet.setActivityId(iocoActivityConfig.getId());
                iocoActivityWallet.setBalance(iocoActivityRule.getMaxSlpAmount());
                iocoActivityWallet.setMemberId(memberId);
                iocoActivityWallet.setPlanAmount(iocoActivityRule.getMaxSlpAmount());
                iocoActivityWallet.setRuleId(iocoActivityRule.getId());
                iocoActivityWallet.setUnit("SLP");
                AssertUtil.isTrue(iocoActivityWalletService.save(iocoActivityWallet), LSMsgCode.IOCO_EXCEPTION);
            }

        }
        // 5,获取兑换币种和可用余额列表

        // 5.1 获取usdt
        MessageRespResult<MemberWallet> usdtWalletMessageRespResult = iMemberWalletApiService.getWallet(memberId, "USDT");
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(usdtWalletMessageRespResult);
        MemberWallet usdt = usdtWalletMessageRespResult.getData();

        // 5.2 获取bt
        MessageRespResult<MemberWallet> btWalletMessageRespResult = iMemberWalletApiService.getWallet(memberId, "BT");
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(btWalletMessageRespResult);
        MemberWallet bt = btWalletMessageRespResult.getData();

        List<MemberBalance> memberBalanceList = new ArrayList<>(2);
        memberBalanceList.add(new MemberBalance("USDT", Objects.isNull(usdt) ? BigDecimal.ZERO : usdt.getBalance()));
        memberBalanceList.add(new MemberBalance("BT", Objects.isNull(bt) ? BigDecimal.ZERO : bt.getBalance()));
        // 6,汇总信息返回给前端页面
        IocoPurchaseVo iocoPurchaseVo = IocoPurchaseVo.builder()
                .id(Objects.isNull(iocoActivityConfig) ? 0L : iocoActivityConfig.getId())
                .activityPeriod(Objects.isNull(iocoActivityConfig) ? 0 :iocoActivityConfig.getAcivityPeriod())
                .activityUrl(Objects.isNull(iocoActivityConfig) ? "":iocoActivityConfig.getActivityUrl())
                .activityName(Objects.isNull(iocoActivityConfig) ? "":iocoActivityConfig.getActivityName())
                .status(Objects.isNull(iocoActivityConfig) ? 0:iocoActivityConfig.getStatus())
                .startTime(Objects.isNull(iocoActivityConfig) ? new Date():iocoActivityConfig.getStartTime())
                .endTime(Objects.isNull(iocoActivityConfig) ? new Date():iocoActivityConfig.getEndTime())
                .usdtAmount(Objects.isNull(iocoActivityConfig) ? BigDecimal.ZERO:iocoActivityConfig.getUsdtAmount())
                .btAmount(Objects.isNull(iocoActivityConfig) ? BigDecimal.ZERO:iocoActivityConfig.getBtAmount())
                .slpAmount(Objects.isNull(iocoActivityConfig) ? BigDecimal.ZERO:iocoActivityConfig.getSlpAmount())
                .activitTotalSlpAmount(Objects.isNull(iocoActivityConfig) ? BigDecimal.ZERO:iocoActivityConfig.getActivitTotalSlpAmount())
                .activitTotalSlpBalance(Objects.isNull(iocoActivityConfig) ? BigDecimal.ZERO:iocoActivityConfig.getActivitTotalSlpBalance())
                .directMembers(Objects.isNull(slpMemberPromotion) ? 0 : slpMemberPromotion.getDirectCount())
                .allMembers(Objects.isNull(slpMemberPromotion) ? 0 : slpMemberPromotion.getAllCount())
                .balance(iocoMemberWallet.getAmount())
                .planAmount(Objects.isNull(iocoActivityWallet) ? BigDecimal.ZERO : iocoActivityWallet.getPlanAmount())
                .remainAmount(Objects.isNull(iocoActivityWallet) ? BigDecimal.ZERO : iocoActivityWallet.getBalance())
                .minSlpAmount(Objects.isNull(iocoActivityWallet) ? BigDecimal.ZERO : iocoActivityWallet.getMinAmount())
                .minShare(Objects.isNull(iocoActivityWallet) ? 1 : iocoActivityWallet.getMinAmount().divide(iocoActivityConfig.getSlpAmount(), 0, BigDecimal.ROUND_UP).intValue())
                .memberBalances(memberBalanceList)
                .build();

        return iocoPurchaseVo;
    }

    /**
     * 获取ioco转赠界面数据
     *
     * @param memberId
     * @return true
     * @author shenzucai
     * @time 2019.07.03 17:04
     */
    @Override
    public IocoMemberWallet giftIndex(Long memberId) {

        QueryWrapper<IocoMemberWallet> memberWalletQueryWrapper = new QueryWrapper<IocoMemberWallet>().eq("member_id", memberId).eq("unit", "SLP");
        IocoMemberWallet iocoMemberWallet = iocoMemberWalletService.getOne(memberWalletQueryWrapper);
        return iocoMemberWallet;
    }


    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Boolean updateActivityStatus(IocoActivityConfig iocoActivityConfig) {

        Long currentTime = System.currentTimeMillis();
        if (currentTime >= iocoActivityConfig.getEndTime().getTime() || iocoActivityConfig.getActivitTotalSlpBalance().compareTo(BigDecimal.ZERO) != 1) {
            // 当前活动设置为关闭
            UpdateWrapper<IocoActivityConfig> currenActivityConfigUpdateWrapper = new UpdateWrapper<IocoActivityConfig>()
                    .set("status", 2).eq("status", 1).eq("id", iocoActivityConfig.getId());
            activityConfigService.update(currenActivityConfigUpdateWrapper);
            // 下一个活动设置为开启
            UpdateWrapper<IocoActivityConfig> nextctivityConfigUpdateWrapper = new UpdateWrapper<IocoActivityConfig>()
                    .set("status", 1).eq("status", 0).eq("acivity_period", iocoActivityConfig.getAcivityPeriod() + 1);
            activityConfigService.update(nextctivityConfigUpdateWrapper);

            return false;
        } else {
            return true;
        }


    }

    /**
     * 申购接口
     *
     * @param memberId       用户id
     * @param purchasetUnit  支付币种
     * @param purchaseAmount 支付数量
     * @param slpAmount      申购数量
     * @param share          申购份数
     * @return true
     * @author shenzucai
     * @time 2019.07.03 21:50
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean purchaseSLP(Long memberId, String purchasetUnit, BigDecimal purchaseAmount, BigDecimal slpAmount, Integer share, Long activityId) {

        AssertUtil.isTrue(share >= 1, LSMsgCode.IOCO_BUY_AMOUNT_TO_SMALL);
        // 0,前置操作验证数据合法性
        // 0.1 查询出当前活动配置,生效的活动同一时间只能有一个
        QueryWrapper<IocoActivityConfig> configQueryWrapper = new QueryWrapper<IocoActivityConfig>().eq("status", 1).eq("id", activityId);
        IocoActivityConfig iocoActivityConfig = activityConfigService.getOne(configQueryWrapper);
        AssertUtil.isTrue(iocoActivityConfig != null, LSMsgCode.NULL_IOCO);
        // 活动是否失效 /此处需要 updateActivityStatus 不回滚
        Boolean checkActivity = getService().updateActivityStatus(iocoActivityConfig);

        AssertUtil.isTrue(checkActivity, LSMsgCode.IOCO_CURRENT_ACTIVITY_ALREADY_INVALID);

        // 添加活动切换
        AssertUtil.isTrue(iocoActivityConfig.getActivitTotalSlpBalance().compareTo(slpAmount) != -1, LSMsgCode.IOCO_CURRENT_ACTIVITY_BALANCE_NOT_ENOUGH);
        // 0.1.1 判断金额是否正确
        if (StringUtils.equalsIgnoreCase(purchasetUnit, "USDT")) {
            AssertUtil.isTrue(purchaseAmount
                    .divide(iocoActivityConfig.getUsdtAmount(), 8, BigDecimal.ROUND_DOWN)
                    .multiply(iocoActivityConfig.getSlpAmount())
                    .setScale(8, BigDecimal.ROUND_DOWN).compareTo(slpAmount) != -1, LSMsgCode.IOCO_PARAMETER_VERIFIED);

            AssertUtil.isTrue(new BigDecimal(share)
                    .multiply(iocoActivityConfig.getUsdtAmount())
                    .setScale(8, BigDecimal.ROUND_DOWN)
                    .compareTo(purchaseAmount) != -1, LSMsgCode.IOCO_PARAMETER_VERIFIED);

        } else if (StringUtils.equalsIgnoreCase(purchasetUnit, "BT")) {
            AssertUtil.isTrue(purchaseAmount
                    .divide(iocoActivityConfig.getBtAmount(), 8, BigDecimal.ROUND_DOWN)
                    .multiply(iocoActivityConfig.getSlpAmount())
                    .setScale(8, BigDecimal.ROUND_DOWN).compareTo(slpAmount) != -1, LSMsgCode.IOCO_PARAMETER_VERIFIED);

            AssertUtil.isTrue(new BigDecimal(share)
                    .multiply(iocoActivityConfig.getBtAmount())
                    .setScale(8, BigDecimal.ROUND_DOWN)
                    .compareTo(purchaseAmount) != -1, LSMsgCode.IOCO_PARAMETER_VERIFIED);
        }

        // 新增，没有推荐人，不可参加该活动
        MessageRespResult<SlpMemberPromotion> promotionMessageRespResult = iMemberApiService.getSlpMemberPromotion(memberId);
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(promotionMessageRespResult);
        SlpMemberPromotion slpMemberPromotion = promotionMessageRespResult.getData();
        // 邀请关系必须有，且邀请人不能为空或者0
        AssertUtil.isTrue(slpMemberPromotion != null && !Objects.isNull(slpMemberPromotion.getInviterId()) && slpMemberPromotion.getInviterId() > 0, LSMsgCode.NOT_FIND_SLP_PROMOTION);

        // 0.2,获取当前活动可申购的详细信息 一个用户在当前活动中只能有一条记录
        QueryWrapper<IocoActivityWallet> activityWalletQueryWrapper = new QueryWrapper<IocoActivityWallet>().eq("member_id", memberId)
                .eq("unit", "SLP")
                .eq("activity_id", activityId);
        IocoActivityWallet iocoActivityWallet = iocoActivityWalletService.getOne(activityWalletQueryWrapper);
        AssertUtil.isTrue(iocoActivityWallet != null, LSMsgCode.IOCO_RECOMMEND_NOT_ENOUGH);

        AssertUtil.isTrue(iocoActivityWallet.getMinAmount().compareTo(slpAmount) != 1, LSMsgCode.IOCO_BUY_AMOUNT_TO_SMALL);

        if (iocoActivityWallet.getBalance().compareTo(slpAmount) == -1) {
            ExceptionUitl.throwsMessageCodeException(LSMsgCode.IOCO_USER_BALANCE_NOT_ENOUGH);
        }

        // 1,生成SLP申购记录
        IocoMemberTransaction iocoMemberTransaction = new IocoMemberTransaction();
        iocoMemberTransaction.setFromMemberId(memberId);
        iocoMemberTransaction.setFromAmount(purchaseAmount);
        iocoMemberTransaction.setFromUnit(purchasetUnit);
        iocoMemberTransaction.setToMemberId(memberId);
        iocoMemberTransaction.setToAmount(slpAmount);
        iocoMemberTransaction.setToUnit("SLP");
        iocoMemberTransaction.setRuleId(iocoActivityWallet.getRuleId());
        iocoMemberTransaction.setType(0);
        AssertUtil.isTrue(save(iocoMemberTransaction), LSMsgCode.IOCO_EXCEPTION);
        // 2,操作支付币种余额
        WalletTradeEntity tradeEntity = new WalletTradeEntity();
        tradeEntity.setCoinId(StringUtils.upperCase(purchasetUnit));
        tradeEntity.setCoinUnit(StringUtils.upperCase(purchasetUnit));
        tradeEntity.setTradeBalance(purchaseAmount.negate());
        tradeEntity.setComment("IOCO-PURCHASE");
        tradeEntity.setMemberId(memberId);
        tradeEntity.setType(TransactionType.ACTIVITY_AWARD);
        tradeEntity.setRefId(String.valueOf(iocoMemberTransaction.getId()));
        tradeEntity.setChangeType(WalletChangeType.TRADE);
        MessageRespResult<Boolean> booleanMessageRespResult = iMemberWalletApiService.trade(tradeEntity);
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(booleanMessageRespResult);

        if(! booleanMessageRespResult.getData()){
            ExceptionUitl.throwsMessageCodeException(LSMsgCode.IOCO_EXCEPTION);
        }
        // 3,增加SLP数量
        // 3.1 减少用户活动账户中的剩余申购数量

        if (!Objects.isNull(iocoActivityWallet) && !Objects.isNull(slpMemberPromotion)) {
            // 4.2 根据活动配置和邀请关系给用户定级
            IocoActivityRule iocoActivityRule = iocoActivityRuleService.getActivityRuleByTotalCount(slpMemberPromotion.getAllCount(), iocoActivityConfig.getId());
            // AssertUtil.isTrue( !Objects.isNull(iocoActivityRule),LSMsgCode.IOCO_RECOMMEND_NOT_ENOUGH);
            if (!Objects.isNull(iocoActivityRule)
                    // 额度有上升时
                    && iocoActivityRule.getMaxSlpAmount().compareTo(iocoActivityWallet.getPlanAmount()) == 1) {
                BigDecimal acAmount = slpAmount.subtract(iocoActivityRule.getMaxSlpAmount().subtract(iocoActivityWallet.getPlanAmount()));
                UpdateWrapper<IocoActivityWallet> activityWalletUpdateWrapper = new UpdateWrapper<IocoActivityWallet>()
                        .setSql("balance = balance - " + acAmount)
                        .set("plan_amount",iocoActivityRule.getMaxSlpAmount())
                        .set("min_amount",iocoActivityRule.getMinSlpAmount())
                        .ge("balance", acAmount).eq("id", iocoActivityWallet.getId());
                AssertUtil.isTrue(iocoActivityWalletService.update(activityWalletUpdateWrapper), LSMsgCode.IOCO_EXCEPTION);
            }else{

                UpdateWrapper<IocoActivityWallet> activityWalletUpdateWrapper = new UpdateWrapper<IocoActivityWallet>()
                        .setSql("balance = balance - " + slpAmount).ge("balance", slpAmount).eq("id", iocoActivityWallet.getId());
                AssertUtil.isTrue(iocoActivityWalletService.update(activityWalletUpdateWrapper), LSMsgCode.IOCO_EXCEPTION);
            }

        }
        /*UpdateWrapper<IocoActivityWallet> activityWalletUpdateWrapper = new UpdateWrapper<IocoActivityWallet>()
                .setSql("balance = balance - " + slpAmount).ge("balance", slpAmount).eq("id", iocoActivityWallet.getId());
        AssertUtil.isTrue(iocoActivityWalletService.update(activityWalletUpdateWrapper), LSMsgCode.IOCO_EXCEPTION);*/
        // 3.2 减少当前活动账户中的剩余申购数量
        UpdateWrapper<IocoActivityConfig> activityConfigUpdateWrapper = new UpdateWrapper<IocoActivityConfig>()
                .setSql("activit_total_slp_balance = activit_total_slp_balance - " + slpAmount).ge("activit_total_slp_balance", slpAmount).eq("id", iocoActivityConfig.getId());
        AssertUtil.isTrue(activityConfigService.update(activityConfigUpdateWrapper), LSMsgCode.IOCO_EXCEPTION);
        // 3.3 增加用户账户中的SLP数量
        UpdateWrapper<IocoMemberWallet> memberWalletUpdateWrapper = new UpdateWrapper<IocoMemberWallet>()
                .setSql("amount = amount + " + slpAmount).eq("member_id", memberId).eq("unit", "SLP");
        AssertUtil.isTrue(iocoMemberWalletService.update(memberWalletUpdateWrapper), LSMsgCode.IOCO_EXCEPTION);
        return true;
    }

    /**
     * 转赠接口
     *
     * @param memberId   用户id
     * @param giftUnit   赠送币种
     * @param giftAmount 赠送数量
     * @param giftTo     赠送对象
     * @return true
     * @author shenzucai
     * @time 2019.07.03 21:50
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean giftSLP(Long memberId, String giftUnit, BigDecimal giftAmount, String giftTo) {
        // 查看当前用户余额
        QueryWrapper<IocoMemberWallet> memberWalletQueryWrapper = new QueryWrapper<IocoMemberWallet>()
                .eq("member_id", memberId).eq("unit", giftUnit);

        IocoMemberWallet iocoMemberWallet = iocoMemberWalletService.getOne(memberWalletQueryWrapper);
        AssertUtil.isTrue(!Objects.isNull(iocoMemberWallet), LSMsgCode.IOCO_EXCEPTION);
        AssertUtil.isTrue(iocoMemberWallet.getAmount().compareTo(giftAmount) != -1, LSMsgCode.IOCO_USER_BALANCE_NOT_ENOUGH);
        // 判断赠送方钱包是否存在
        MessageRespResult<Member> memberMessageRespResult = iMemberApiService.getMemberByPhoneOrEmail(giftTo.contains("@") ? 1 : 0, giftTo);
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(memberMessageRespResult);
        Member member = memberMessageRespResult.getData();
        AssertUtil.isTrue(!Objects.isNull(member), LSMsgCode.IOCO_USER_NOT_EXIST);
        QueryWrapper<IocoMemberWallet> iocoMemberWalletQueryWrapper = new QueryWrapper<IocoMemberWallet>()
                .eq("member_id", member.getId()).eq("unit", giftUnit);
        IocoMemberWallet iocoMemberWalletTo = iocoMemberWalletService.getOne(iocoMemberWalletQueryWrapper);
        if (Objects.isNull(iocoMemberWalletTo)) {
            iocoMemberWalletTo = new IocoMemberWallet();
            iocoMemberWalletTo.setAmount(BigDecimal.ZERO);
            iocoMemberWalletTo.setMemberId(member.getId());
            iocoMemberWalletTo.setUnit("SLP");
            AssertUtil.isTrue(iocoMemberWalletService.save(iocoMemberWalletTo), LSMsgCode.IOCO_EXCEPTION);
        }

        // 开始转账

        // 1,生成SLP申购记录
        IocoMemberTransaction iocoMemberTransaction = new IocoMemberTransaction();
        iocoMemberTransaction.setFromMemberId(memberId);
        iocoMemberTransaction.setFromAmount(giftAmount);
        iocoMemberTransaction.setFromUnit(giftUnit);
        iocoMemberTransaction.setToMemberId(member.getId());
        iocoMemberTransaction.setToAmount(giftAmount);
        iocoMemberTransaction.setToUnit(giftUnit);
        iocoMemberTransaction.setType(1);
        AssertUtil.isTrue(save(iocoMemberTransaction), LSMsgCode.IOCO_EXCEPTION);

        // 2 修改from用户余额
        UpdateWrapper<IocoMemberWallet> fromMemberWalletUpdateWrapper = new UpdateWrapper<IocoMemberWallet>()
                .setSql("amount = amount - " + giftAmount).ge("amount", giftAmount).eq("member_id", memberId).eq("unit", "SLP");
        AssertUtil.isTrue(iocoMemberWalletService.update(fromMemberWalletUpdateWrapper), LSMsgCode.IOCO_EXCEPTION);

        // 3 修改to用户余额

        UpdateWrapper<IocoMemberWallet> toMemberWalletUpdateWrapper = new UpdateWrapper<IocoMemberWallet>()
                .setSql("amount = amount + " + giftAmount).eq("member_id", member.getId()).eq("unit", "SLP");
        AssertUtil.isTrue(iocoMemberWalletService.update(toMemberWalletUpdateWrapper), LSMsgCode.IOCO_EXCEPTION);

        return true;
    }

    /**
     * ioco转账记录
     *
     * @param memberId 用户id
     * @param size     分页大小
     * @param current  当前页码
     * @param type     转账类型
     * @return true
     * @author shenzucai
     * @time 2019.07.04 8:40
     */
    @Override
    public IPage<IocoPurchaseTransactionVo> listByType(Long memberId, Integer size, Integer current, Integer type) {
        Page<IocoMemberTransaction> transactionPage = new Page<>(current, size);
        QueryWrapper<IocoMemberTransaction> transactionQueryWrapper = new QueryWrapper<IocoMemberTransaction>()
                .and(wrapper -> wrapper.eq("from_member_id", memberId).or().eq("to_member_id", memberId))
                .eq("type", type).orderByDesc("create_time");
        IPage<IocoMemberTransaction> transactionIPage = page(transactionPage, transactionQueryWrapper);


        Page<IocoPurchaseTransactionVo> transactionVoPage = new Page<>(current, size);

        Set<Long> longSet = new HashSet<>();
        // 如果记录为空，则返回空
        if (transactionIPage == null || transactionIPage.getRecords() == null || transactionIPage.getRecords().size() < 1) {
            transactionVoPage.setTotal(transactionIPage.getTotal());
            transactionVoPage.setPages(transactionIPage.getPages());
            transactionVoPage.setRecords(null);
            return transactionVoPage;
        }
        transactionIPage.getRecords().forEach(record -> {
            // 处理交易对象，取非己方
            longSet.add(Objects.equals(record.getToMemberId(), memberId) ? record.getFromMemberId() : record.getToMemberId());
        });

        // 填充转账记录所需的数据
        List<IocoPurchaseTransactionVo> transactionVoList = new ArrayList<>();

        MessageRespResult<List<Member>> listMessageRespResult = iMemberApiService.listMembersByIds(new ArrayList<>(longSet));

        ExceptionUitl.throwsMessageCodeExceptionIfFailed(listMessageRespResult);

        List<Member> members = listMessageRespResult.getData();

        if (transactionIPage.getRecords() != null && transactionIPage.getRecords().size() > 0) {
            transactionIPage.getRecords().forEach(record -> {
                IocoPurchaseTransactionVo transactionVo = new IocoPurchaseTransactionVo();
                transactionVo.setType(record.getType());
                transactionVo.setFromUnit(record.getFromUnit());
                transactionVo.setFromAmount(record.getFromAmount());
                transactionVo.setToUnit(record.getToUnit());
                transactionVo.setToAmount(record.getToAmount());
                transactionVo.setTransferType(Objects.equals(memberId, record.getToMemberId()) ? 0 : 1);
                if (members != null && members.size() > 0) {
                    members.forEach(item -> {
                        if (item.getId().equals(record.getToMemberId())) {
                            transactionVo.setGitfMemberAccount(StringUtils.isEmpty(item.getEmail())
                                    ? item.getMobilePhone() : StringUtils.isEmpty(item.getMobilePhone())
                                    ? item.getEmail() : item.getMobilePhone() + "/" + item.getEmail());
                        } else if (item.getId().equals(record.getFromMemberId())) {
                            transactionVo.setGitfMemberAccount(StringUtils.isEmpty(item.getEmail())
                                    ? item.getMobilePhone() : StringUtils.isEmpty(item.getMobilePhone())
                                    ? item.getEmail() : item.getMobilePhone() + "/" + item.getEmail());
                        }
                    });
                }
                transactionVo.setCreateTime(record.getCreateTime());

                transactionVoList.add(transactionVo);

            });
        }
        transactionVoPage.setTotal(transactionIPage.getTotal());
        transactionVoPage.setPages(transactionIPage.getPages());
        transactionVoPage.setRecords(transactionVoList);
        return transactionVoPage;
    }


    private IocoMemberTransactionServiceImpl getService() {
        return SpringContextUtil.getBean(this.getClass());
    }

}