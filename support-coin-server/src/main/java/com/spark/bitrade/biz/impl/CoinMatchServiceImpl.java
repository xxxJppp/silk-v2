package com.spark.bitrade.biz.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.biz.ICoinMatchService;
import com.spark.bitrade.biz.IPayRecordService;
import com.spark.bitrade.config.PayConfigProperties;
import com.spark.bitrade.constant.*;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.SupportCoinMatch;
import com.spark.bitrade.entity.SupportPayRecords;
import com.spark.bitrade.entity.SupportUpCoinApply;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.form.CoinMathForm;
import com.spark.bitrade.param.CoinMatchParam;
import com.spark.bitrade.service.IMemberWalletApiService;
import com.spark.bitrade.service.SupportCoinMatchService;
import com.spark.bitrade.service.SupportPayRecordsService;
import com.spark.bitrade.service.SupportUpCoinApplyService;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.vo.CoinMatchVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.05 17:23
 */
@Service
public class CoinMatchServiceImpl implements ICoinMatchService {

    @Autowired
    private SupportUpCoinApplyService upCoinApplyService;

    @Autowired
    private SupportCoinMatchService coinMatchService;

    @Autowired
    private IPayRecordService payRecordService;

    @Autowired
    private SupportPayRecordsService supportPayRecordsService;

    @Autowired
    private IMemberWalletApiService memberWalletApiService;

    @Autowired
    private PayConfigProperties payConfigProperties;


    @Override
    public List<CoinMatchVo> findCoinMatchVoList(Long upCoinId) {
        // 获取扶持币种
        SupportUpCoinApply upCoinApply = upCoinApplyService.getById(upCoinId);
        //查询币币交易交易对
        List<String> coins=coinMatchService.findByCoinUnit(upCoinApply.getCoin());
        // 获取交易对
        List<SupportCoinMatch> coinMatchLists = coinMatchService.findByUpCoinId(upCoinId);
        Set<String> matchCoins = coinMatchLists.stream().map(s -> upCoinApply.getCoin() + "/" + s.getTargetCoin()).collect(Collectors.toSet());
        matchCoins.addAll(coins);
        List<CoinMatchVo> coinMatchVoList = new ArrayList<>();
        for (String coinMatch : matchCoins) {
            CoinMatchVo matchVo = new CoinMatchVo();
            // 构建页面显示名字
            matchVo.setCoinMatchName(coinMatch);
            coinMatchVoList.add(matchVo);
        }
        return coinMatchVoList;
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public CoinMatchVo findCoinMacthesList(Long memberId, CoinMatchParam param) {
        // 查询该币种是否可以使用增加交易对功能
        SupportUpCoinApply upCoinApply = upCoinApplyService.findApprovedUpCoinByMember(memberId);
        CoinMatchVo vo = new CoinMatchVo();
        if (upCoinApply.getSectionType().getCnName().equals(SectionTypeEnum.SUPPORT_UP_ZONE.getCnName())) {
            vo.setIsAddCoinMacth("1");
        } else {
            vo.setIsAddCoinMacth("0");
        }
        vo.setCoinName(upCoinApply.getCoin());
        IPage<SupportCoinMatch> page = coinMatchService.findByUpCoinIdAndMemberId(memberId, upCoinApply.getId(), param);
        vo.setCoinMatches(page);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SupportPayRecords addCoinMatch(Member member, CoinMathForm coinMathForm,SupportUpCoinApply apply) {
        Integer payNum = 0;
        if (apply.getRealSectionType().getOrdinal() == SectionTypeEnum.MAIN_ZONE.getOrdinal()) {
            payNum = payConfigProperties.getMainCoin();
        } else if (apply.getRealSectionType().getOrdinal() == SectionTypeEnum.INNOVATION_ZONE.getOrdinal()) {
            payNum = payConfigProperties.getInnovativeCoin();
        } else {
            payNum = payConfigProperties.getInnovativeCoin();
        }
//        SupportCoinMatch coinMatchs = coinMatchService.findByAuditStauts(member.getId(), apply.getId());
//////        if (coinMatchs != null) {
//////            throw new MessageCodeException(SupportCoinMsgCode.COINMATCH_IS_EXIST_PENDING);
//////        }
        List<SupportCoinMatch> matches = coinMatchService.findByMatch(member.getId(), coinMathForm.getTargetCoin());
        AssertUtil.isTrue(CollectionUtils.isEmpty(matches),SupportCoinMsgCode.CAN_NOT_ADD_COINMATCH_FAILED);
        List<String> matchUnits = coinMatchService.findByCoinUnit(apply.getCoin());
        AssertUtil.isTrue(!matchUnits.contains(apply.getCoin()+"/"+coinMathForm.getTargetCoin()),
                SupportCoinMsgCode.CAN_NOT_ADD_COINMATCH_FAILED);
        // 生成支付信息
        SupportPayRecords payRecord = payRecordService.generatePayRecord(member.getId(), apply.getId(),
                ModuleType.EXCHANGE_MANAGE,
                coinMathForm.getPayCoin(),
                coinMathForm.getRemark(),new BigDecimal(payNum)
        );

        SupportCoinMatch coinMatch = new SupportCoinMatch();
        coinMatch.setMemberId(member.getId());
        coinMatch.setUpCoinId(apply.getId());
        coinMatch.setTargetCoin(coinMathForm.getTargetCoin());
        coinMatch.setRemark(coinMathForm.getRemark());
        coinMatch.setAuditStatus(AuditStatusEnum.PENDING);
        coinMatch.setCreateTime(new Date());
        boolean saveCoinMatchResult = coinMatchService.save(coinMatch);
        payRecord.setApplyId(coinMatch.getId());
        boolean savePayResult = supportPayRecordsService.save(payRecord);
        AssertUtil.isTrue(savePayResult&&saveCoinMatchResult,SupportCoinMsgCode.SAVE_COINMATCH_FAILED);
        WalletTradeEntity entity=new WalletTradeEntity();
        entity.setType(TransactionType.SUPPORT_PROJECT_PAY);
        entity.setRefId(String.valueOf(payRecord.getId()));
        entity.setMemberId(payRecord.getMemberId());
        entity.setCoinUnit(payRecord.getPayCoin());
        entity.setTradeBalance(BigDecimal.ZERO.subtract(payRecord.getPayAmount()));
        entity.setComment("新增交易对支付");
        MessageRespResult<Boolean> result = memberWalletApiService.trade(entity);
        AssertUtil.isTrue(result.isSuccess()&&result.getData(), CommonMsgCode.ACCOUNT_BALANCE_TRADE_FAILED);

        return payRecord;
    }
}
