package com.spark.bitrade.biz;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.common.LuckyGameRedisUtil;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.controller.param.ListBullParam;
import com.spark.bitrade.controller.vo.LuckyRunBullListVo;
import com.spark.bitrade.entity.LuckyJoinInfo;
import com.spark.bitrade.entity.LuckyManageCoin;
import com.spark.bitrade.entity.LuckyNumberManager;
import com.spark.bitrade.entity.SilkDataDist;
import com.spark.bitrade.enums.BullStatusEnum;
import com.spark.bitrade.enums.LuckyErrorCode;
import com.spark.bitrade.service.*;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.ExceptionUitl;
import com.spark.bitrade.util.MessageRespResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 小牛快跑业务层
 */
@Service
@Slf4j
public class LuckyRunBullBizService {

    @Autowired
    private LuckyNumberManagerService luckyNumberManagerService;
    @Autowired
    private LuckyJoinInfoService luckyJoinInfoService;
    @Autowired
    private LuckyManageCoinService luckyManageCoinService;
    @Autowired
    private LuckyGameRedisUtil luckyGameRedisUtil;
    @Autowired
    private ISilkDataDistApiService silkDataDistApiService;
    @Autowired
    private IMemberWalletApiService memberWalletApiService;


    /**
     * 查询小牛快跑列表
     *
     * @param param
     * @return
     */
    public IPage<LuckyRunBullListVo> listBulls(ListBullParam param) {
        Long memberId = param.getMemberId();
        IPage<LuckyRunBullListVo> page = new Page(param.getPage(), param.getPageSize());
        List<Long> memberActIds = new ArrayList<>();
        if (param.getMemberId() != null && StringUtils.isNotBlank(param.getOnlyMine())) {
            memberActIds = luckyNumberManagerService.findMemberActIds(param.getMemberId());
            if (memberActIds.isEmpty()) {
                return new Page<>();
            }
        }
        List<LuckyRunBullListVo> luckyNumberManagers = luckyNumberManagerService.listBulls(param, page, memberActIds);
        if (!CollectionUtils.isEmpty(luckyNumberManagers)) {
            luckyNumberManagers.forEach(l -> {
                //redis 取参赛人数 参赛总票数
                if (l.getLuckyTime().after(new Date())) {
                    Map<String, Integer> joinCount = luckyGameRedisUtil.getJoinCount(l.getActId().toString());
                    l.setJoinMemberCount(joinCount.get("member"));
                    l.setJoinTicketCount(joinCount.get("ticket"));
                }
                //  选牛中 ，赛牛中
                l.coinsChange();
                Long actId = l.getActId();
                if ((param.getStatus() == BullStatusEnum.CHOOSING_BULL
                        || param.getStatus() == BullStatusEnum.IN_THE_GAME) && memberId != null) {
                    //查询自己参加的活动
                    List<LuckyRunBullListVo.MyJoinBulls> myJoinBulls = luckyJoinInfoService.findMyJoinBulls(memberId, actId);
                    l.setMyJoinBulls(myJoinBulls);
                }
                List<LuckyManageCoin> manageCoins = luckyManageCoinService.bullsRank(actId);
                l.setManageCoins(manageCoins);
                //已结束
                if (param.getStatus() == BullStatusEnum.END) {
                    if (memberId != null) {
                        //中奖信息
                        LuckyRunBullListVo myBullLucky = luckyJoinInfoService.findMyBullLucky(memberId, actId);
                        if (myBullLucky != null) {
                            l.setMemberAddLuckyAmount(myBullLucky.getMemberAddLuckyAmount());
                            l.setMemberLuckyAmount(myBullLucky.getMemberLuckyAmount());
                            l.setMemberLuckyCount(myBullLucky.getMemberLuckyCount());
                            BooleanEnum isShare = myBullLucky.getIsShare();
                            l.setIsShare(isShare);
                        }
                        //每票中奖金额
                        BigDecimal joinMemberAmount = l.getJoinMemberAmount();
                        Integer winNum = l.getWinNum();
                        if (winNum != null && winNum != 0) {
                            l.setOnceWinAmount(joinMemberAmount.divide(new BigDecimal(winNum), 8, RoundingMode.DOWN));
                        }
                    }

                }

            });
        }
        page.setRecords(luckyNumberManagers);
        return page;
    }

    /**
     * 活动结束详情
     *
     * @param memberId
     * @param actId
     * @return
     */
    public LuckyRunBullListVo bullDetail(Long memberId, Long actId) {
        LuckyRunBullListVo vo = luckyNumberManagerService.detailBull(actId);
        if (vo == null) {
            return vo;
        }
        //活动统计
        //redis 取参赛人数 参赛总票数 活动未结束的时候
        if (vo.getLuckyTime().after(new Date())) {
            Map<String, Integer> joinCount = luckyGameRedisUtil.getJoinCount(actId.toString());
            vo.setJoinMemberCount(joinCount.get("member"));
            vo.setJoinTicketCount(joinCount.get("ticket"));
        }
        //小牛排行榜
        List<LuckyManageCoin> manageCoins = luckyManageCoinService.bullsRank(actId);
        vo.setManageCoins(manageCoins);
        //我的中奖详情
        if (memberId != null) {
            LuckyRunBullListVo myBullLucky = luckyJoinInfoService.findMyBullLucky(memberId, actId);
            if (myBullLucky != null) {
                vo.setMemberLuckyCount(myBullLucky.getMemberLuckyCount());
                vo.setMemberLuckyAmount(myBullLucky.getMemberLuckyAmount());
                vo.setMemberAddLuckyAmount(myBullLucky.getMemberAddLuckyAmount());
                vo.setIsShare(myBullLucky.getIsShare());
            }


            //查询自己参加的活动
            List<LuckyRunBullListVo.MyJoinBulls> myJoinBulls = luckyJoinInfoService.findMyJoinBulls(memberId, actId);
            vo.setMyJoinBulls(myJoinBulls);
        }
        if (vo.getWinNum() > 0) {
            vo.setOnceWinAmount(vo.getJoinMemberAmount().divide(new BigDecimal(vo.getWinNum()), 8, BigDecimal.ROUND_DOWN));
        }

        luckyJoinInfoService.readAck(memberId, actId.toString(), 1);
        return vo;
    }

    /**
     * 验证购买参数
     */
    public LuckyNumberManager validateBuy(Long actId, Integer buyCount, String coinUnit, Long memberId) {
        //购买数量不能等于0
        AssertUtil.isTrue(buyCount > 0, LuckyErrorCode.BUY_COUNT_MUST_GT_ZERO);
        LuckyNumberManager manager = luckyNumberManagerService.getById(actId);
        //活动判断
        AssertUtil.isTrue(manager != null && manager.getDeleteState() == BooleanEnum.IS_FALSE, LuckyErrorCode.ACT_NOT_FIND);


        //选牛结束时间
        Date endTime = manager.getEndTime();
        //取当前时间加十分钟
        Calendar ca = Calendar.getInstance();
        AssertUtil.isTrue(endTime.after(ca.getTime()), LuckyErrorCode.HAS_ALREADY_END);
        //验证是否存在该牛
        Optional<LuckyManageCoin> optional = luckyManageCoinService.findByActIdAndCoin(actId, coinUnit);
        AssertUtil.isTrue(optional.isPresent(), LuckyErrorCode.THIS_BULL_NOT_FIND);
        return manager;
    }

    @Transactional(rollbackFor = Exception.class)
    public synchronized void buyBull(Long memberId, LuckyNumberManager luckyNumberManager, Integer buyCount, String coinUnit) {
        MessageRespResult<SilkDataDist> silk = silkDataDistApiService.findOne("LUCKY_CONFIG", "TOTAL_ACCOUNT_ID");
        AssertUtil.isTrue(silk.isSuccess() && silk.getData() != null, LuckyErrorCode.RECEIVE_ACCOUNT_NOT_FOUND);
        List<LuckyJoinInfo> joinInfos = new ArrayList<>();

        //luckyGameRedisUtil.
        Integer joinCount = luckyJoinInfoService.findMyJoinBullCount(memberId, luckyNumberManager.getId());
        //购买数量
        AssertUtil.isTrue(luckyNumberManager.getSingleMaxNum() >= buyCount + joinCount, LuckyErrorCode.OVER_MAX_TICKET);
        LuckyJoinInfo luckyJoinInfo;
        for (int i = 0; i < buyCount; i++) {
            luckyJoinInfo = new LuckyJoinInfo();
            luckyJoinInfo.setNumId(luckyNumberManager.getId());
            luckyJoinInfo.setMemberId(memberId);
            luckyJoinInfo.setJoinInfo(coinUnit);
            luckyJoinInfo.setWin(BooleanEnum.IS_FALSE);
            luckyJoinInfo.setCreateTime(new Date());
            luckyJoinInfo.setDeleteState(BooleanEnum.IS_FALSE);
            joinInfos.add(luckyJoinInfo);
        }
        boolean b = luckyJoinInfoService.saveBatch(joinInfos);
        AssertUtil.isTrue(b, LuckyErrorCode.JOIN_FAILED);

        BigDecimal payAmount = luckyNumberManager.getAmount().multiply(new BigDecimal(buyCount));

        //支付 先扣钱
        WalletTradeEntity entity = new WalletTradeEntity();
        entity.setType(TransactionType.BUY_LUCKY_BULL);
        entity.setRefId(String.valueOf(joinInfos.get(0).getId()));
        entity.setMemberId(memberId);
        entity.setCoinUnit(luckyNumberManager.getUnit());
        entity.setTradeBalance(payAmount.negate());
        entity.setComment("购买小牛快跑");
        MessageRespResult<Boolean> result = memberWalletApiService.trade(entity);
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(result);
        //总账户加钱  存在分布式事务 总账户加钱不能影响 购买
        try {

            Long totalId = Long.valueOf(silk.getData().getDictVal());
            //接受账号加钱
            WalletTradeEntity reveive = new WalletTradeEntity();
            reveive.setType(TransactionType.BUY_LUCKY_BULL);
            reveive.setRefId(String.valueOf(joinInfos.get(0).getId()));
            reveive.setMemberId(totalId);
            reveive.setCoinUnit(luckyNumberManager.getUnit());
            reveive.setTradeBalance(payAmount);
            reveive.setComment("用户购买小牛快跑");
            MessageRespResult<Boolean> reveiveResult = memberWalletApiService.trade(reveive);
            ExceptionUitl.throwsMessageCodeExceptionIfFailed(reveiveResult);
        } catch (Exception e) {
            log.info("总帐户加钱失败:{}", ExceptionUtils.getFullStackTrace(e));
        }
        //更新redis 购买量  存在分布式事务 更新redis不能影响 购买
        try {
            for (int k = 0; k < buyCount; k++) {
                luckyGameRedisUtil.joinGame(luckyNumberManager.getId().toString(), memberId.toString());
            }
        } catch (Exception e) {
            log.info("更新redis失败{}", ExceptionUtils.getFullStackTrace(e));
        }

    }

    @Transactional
    public void shareBull(Long memberId, Long actId) {

        LuckyNumberManager manager = luckyNumberManagerService.getById(actId);
        AssertUtil.isTrue(manager != null && manager.getDeleteState() == BooleanEnum.IS_FALSE, LuckyErrorCode.ACT_NOT_FIND);
        LuckyRunBullListVo lucky = luckyJoinInfoService.findMyBullLucky(memberId, actId);
        if (lucky.getIsShare() == BooleanEnum.IS_TRUE) {
            //如果已分享 则不增加余额
            return;
        }
        //更新状态为已分享
        int i = luckyJoinInfoService.updateBullShareStatus(actId, memberId);
        AssertUtil.isTrue(i > 0, LuckyErrorCode.SHARE_FAILED);
        //增加分享奖金
        WalletTradeEntity share = new WalletTradeEntity();
        share.setType(TransactionType.LUCKY_APPEND_WX_BULL);
        share.setMemberId(memberId);
        share.setCoinUnit(manager.getUnit());
        share.setTradeBalance(lucky.getMemberAddLuckyAmount());
        share.setComment("小牛快跑追加奖金发放");
        MessageRespResult<Boolean> reveiveResult = memberWalletApiService.trade(share);
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(reveiveResult);

        //总账户kou钱  存在分布式事务 总账户扣钱不能影响 分享
        try {
            //读消息
            luckyJoinInfoService.readAck(memberId, actId.toString(), 2);
            MessageRespResult<SilkDataDist> silk = silkDataDistApiService.findOne("LUCKY_CONFIG", "TOTAL_ACCOUNT_ID");
            AssertUtil.isTrue(silk.isSuccess() && silk.getData() != null, LuckyErrorCode.RECEIVE_ACCOUNT_NOT_FOUND);
            Long totalId = Long.valueOf(silk.getData().getDictVal());
            //接受账号加钱
            WalletTradeEntity sub = new WalletTradeEntity();
            sub.setType(TransactionType.LUCKY_APPEND_WX_BULL);
            sub.setMemberId(totalId);
            sub.setCoinUnit(manager.getUnit());
            sub.setTradeBalance(lucky.getMemberAddLuckyAmount().negate());
            sub.setComment("小牛快跑追加奖金发放");
            MessageRespResult<Boolean> result = memberWalletApiService.trade(sub);
            ExceptionUitl.throwsMessageCodeExceptionIfFailed(result);
        } catch (Exception e) {
            log.info("总帐户加钱失败:{}", ExceptionUtils.getFullStackTrace(e));
        }

    }


    public List<LuckyManageCoin> findRealCoinBulls(Long actId) {
        return luckyNumberManagerService.findRealCoinBulls(actId);
    }
}















