package com.spark.bitrade.biz;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.constant.AuditStatusEnum;
import com.spark.bitrade.constant.*;

import java.math.RoundingMode;
import java.util.Date;

import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.*;
import com.spark.bitrade.form.RedPackForm;
import com.spark.bitrade.param.RedPackParam;
import com.spark.bitrade.param.RedRecieveParam;
import com.spark.bitrade.service.*;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.DateUtil;
import com.spark.bitrade.util.ExceptionUitl;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.vo.ApplyRedPackAuditRecordVo;
import com.spark.bitrade.vo.ApplyRedPackListVo;
import com.spark.bitrade.vo.RedPackRecieveDetailVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class SupportRedPackBizService {
    @Autowired
    private SupportUpCoinApplyService supportUpCoinApplyService;
    @Autowired
    private SupportOpenRedPackService supportOpenRedPackService;
    @Autowired
    private SupportRedAuditRecordService supportRedAuditRecordService;
    @Autowired
    private SupportPayRecordsService supportPayRecordsService;
    @Resource
    private IMemberWalletApiService memberWalletApiService;
    @Autowired
    private SupportApplyRedPackService supportApplyRedPackService;
    @Resource
    private ISilkDataDistApiService dataDistApiService;
    @Resource
    private ICoinExchange coinExchange;
    /**
     * 红包开通状态
     * @param memberId
     * @return
     */
    public SupportOpenRedPack openRedPackStatus(Long memberId){
        SupportUpCoinApply apply = supportUpCoinApplyService.findApprovedUpCoinByMember(memberId);
        String coin = apply.getCoin();
        SupportOpenRedPack open = supportOpenRedPackService.findByProjectCoin(coin);
        return open;
    }

    public SupportRedAuditRecord findOpenAuditRecord(Long openRedId){

        QueryWrapper<SupportRedAuditRecord> q=new QueryWrapper<>();
        q.lambda().eq(SupportRedAuditRecord::getApplyType,0)
                .eq(SupportRedAuditRecord::getOpenRedId,openRedId)
                .orderByDesc(SupportRedAuditRecord::getCreateTime);
        List<SupportRedAuditRecord> list = supportRedAuditRecordService.list(q);
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        return list.get(0);
    }

    public SupportOpenRedPack canApplyOpenRed(Long memberId){
        SupportOpenRedPack supportOpenRedPack = this.openRedPackStatus(memberId);
        if (supportOpenRedPack!=null){
            if (supportOpenRedPack.getAuditStatus()==AuditStatusEnum.PENDING){
                ExceptionUitl.throwsMessageCodeException(SupportCoinMsgCode.HAS_EXIST_OPEN_APPLY);
            }
            if (supportOpenRedPack.getAuditStatus()==AuditStatusEnum.APPROVED){
                ExceptionUitl.throwsMessageCodeException(SupportCoinMsgCode.HAS_ALREADY_OPEN_REDPACK);
            }
        }
        return supportOpenRedPack;
    }

    /**
     * 申请创建
     * @param memberId
     * @param payCoin
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public SupportRedAuditRecord doOpenApplyRed(Long memberId,
                                                String payCoin,SupportOpenRedPack openRedPack) {
        MessageRespResult<SilkDataDist> cost = dataDistApiService.findOne("SUPPORT_RED_PACK", "OPEN_COST");
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(cost);
        BigDecimal payAmount=new BigDecimal(cost.getData().getDictVal());

        MessageRespResult<SilkDataDist> one = dataDistApiService.findOne("SUPPORT_RED_PACK", "TOTAL_RECEIVE_ACCOUNT_ID");
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(one);
        Long totalId=Long.valueOf(one.getData().getDictVal());

        MessageRespResult<BigDecimal> usdExchangeRate = coinExchange.getUsdExchangeRate(payCoin);
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(usdExchangeRate);
        AssertUtil.isTrue(usdExchangeRate.getData().compareTo(BigDecimal.ZERO)>0,SupportCoinMsgCode.USDT_RATE_GET_FAILED);
        payAmount=payAmount.divide(usdExchangeRate.getData(),8,RoundingMode.HALF_UP);
        SupportUpCoinApply apply = supportUpCoinApplyService.findApprovedUpCoinByMember(memberId);
        if(openRedPack==null){
            openRedPack=new SupportOpenRedPack();
            openRedPack.setProjectCoin(apply.getCoin());
            openRedPack.setUpCoinId(apply.getId());
            openRedPack.setMemberId(memberId);
            openRedPack.setDeleteFlag(0);
        }
        openRedPack.setAuditStatus(AuditStatusEnum.PENDING);
        openRedPack.setAuditOpinion("");
        boolean b = supportOpenRedPackService.saveOrUpdate(openRedPack);
        AssertUtil.isTrue(b, CommonMsgCode.ERROR);

        //审批历史生成
        SupportRedAuditRecord record=new SupportRedAuditRecord();
        record.setOpenRedId(openRedPack.getId());
        record.setApplyType(0);
        record.setPayCoin(payCoin);
        record.setPayAmount(payAmount);
        record.setProjectCoin(apply.getCoin());
        record.setAuditStatus(AuditStatusEnum.PENDING);
        record.setUpCoinId(apply.getId());
        record.setMemberId(memberId);
        record.setDeleteFlag(0);
        boolean save = supportRedAuditRecordService.save(record);
        AssertUtil.isTrue(save, CommonMsgCode.ERROR);

        //支付记录
        SupportPayRecords payRecords=new SupportPayRecords();
        payRecords.setMemberId(memberId);
        payRecords.setUpCoinId(apply.getId());
        payRecords.setModuleType(ModuleType.OPEN_RED_PACK);
        payRecords.setPayType(0);
        payRecords.setPayCoin(payCoin);
        payRecords.setPayAmount(payAmount);
        payRecords.setRemark("红包开通申请-->支付");
        payRecords.setApplyId(record.getId());
        boolean save1 = supportPayRecordsService.save(payRecords);
        AssertUtil.isTrue(save1, CommonMsgCode.ERROR);


        //支付
        WalletTradeEntity entity=new WalletTradeEntity();
        entity.setType(TransactionType.SUPPORT_PROJECT_PAY);
        entity.setRefId(String.valueOf(payRecords.getId()));
        entity.setMemberId(payRecords.getMemberId());
        entity.setCoinUnit(payRecords.getPayCoin());
        entity.setTradeBalance(payRecords.getPayAmount().negate());
        entity.setComment("开通红包功能");
        MessageRespResult<Boolean> result = memberWalletApiService.trade(entity);
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(result);
        //项目方总账户收款
        try {
            WalletTradeEntity re=new WalletTradeEntity();
            re.setType(TransactionType.SUPPORT_PROJECT_PAY);
            re.setRefId(String.valueOf(payRecords.getId()));
            re.setMemberId(totalId);
            re.setCoinUnit(payRecords.getPayCoin());
            re.setTradeBalance(payRecords.getPayAmount());
            re.setComment("开通红包功能-->收款");
            MessageRespResult<Boolean> reResult = memberWalletApiService.trade(re);
            ExceptionUitl.throwsMessageCodeExceptionIfFailed(reResult);
        }catch (Exception e){
            log.info("项目方总账户收款异常:{}",ExceptionUtils.getFullStackTrace(e));
        }

        return record;
    }

    /**
     * 红包申请
     * @param memberId
     * @param form
     */
    @Transactional(rollbackFor = Exception.class)
    public void applyRedPack(Long memberId, RedPackForm form) {
        MessageRespResult<SilkDataDist> two = dataDistApiService.findOne("SUPPORT_RED_PACK", "SERVICE_CHARGE");
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(two);
        BigDecimal serviceCharge=new BigDecimal(two.getData().getDictVal());
        MessageRespResult<SilkDataDist> one = dataDistApiService.findOne("RED_PACK_CONFIG", "TOTAL_ACCOUNT_ID");
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(one);
        Long redPackAccountId=Long.valueOf(one.getData().getDictVal());

        SupportUpCoinApply apply = supportUpCoinApplyService.findApprovedUpCoinByMember(memberId);

        SupportOpenRedPack open = supportOpenRedPackService.findByProjectCoin(apply.getCoin());
        AssertUtil.isTrue(open!=null&&open.getAuditStatus()==AuditStatusEnum.APPROVED,SupportCoinMsgCode.RED_PACK_NOT_OPEN);
        Long oldApplyRedPackId = form.getOldApplyRedPackId();
        SupportApplyRedPack pack;
        if(oldApplyRedPackId==null){
            pack=new SupportApplyRedPack();
            pack.setProjectCoin(apply.getCoin());
            pack.setUpCoinId(apply.getId());
            pack.setMemberId(memberId);
            pack.setDeleteFlag(0);
        }else {
            pack=supportApplyRedPackService.getById(oldApplyRedPackId);
            if (pack.getAuditStatus()==AuditStatusEnum.PENDING){
                ExceptionUitl.throwsMessageCodeException(SupportCoinMsgCode.HAS_EXIST_OPEN_APPLY);
            }
            if (pack.getAuditStatus()==AuditStatusEnum.APPROVED){
                ExceptionUitl.throwsMessageCodeException(SupportCoinMsgCode.HAS_ALREADY_OPEN_REDPACK);
            }
        }
        pack.setAuditStatus(AuditStatusEnum.PENDING);
        pack.setAuditOpinion("");
        pack.setApplyTime(new Date());
        pack.setRedPackName(form.getRedPackName());
        pack.setStartTime(DateUtil.stringToDate(form.getStartTime(),"yyyy-MM-dd HH:mm:ss"));
        pack.setEndTime(DateUtil.stringToDate(form.getEndTime(),"yyyy-MM-dd HH:mm:ss"));
        AssertUtil.isTrue(pack.getStartTime().before(pack.getEndTime()),SupportCoinMsgCode.TIME_IS_NOT_DONE);
        AssertUtil.isTrue(new Date().before(pack.getEndTime()),SupportCoinMsgCode.TIME_MUST_BE_VALID);
        pack.setRedCoin(form.getRedCoin());
        pack.setRedTotalAmount(form.getRedTotalAmount());
        pack.setMaxAmount(form.getMaxAmount());
        pack.setMinAmount(form.getMinAmount());
        if(form.getReceiveType()==2){
            BigDecimal divide = form.getRedTotalAmount().divide(new BigDecimal(form.getRedTotalCount()),4, RoundingMode.DOWN);
            pack.setMaxAmount(divide);
            pack.setMinAmount(divide);
        }
        pack.setRedTotalCount(form.getRedTotalCount());
        pack.setReceiveType(form.getReceiveType());
        pack.setIsOldUser(form.getIsOldUser());
        pack.setServiceCharge(serviceCharge);
        pack.setRemark(form.getRemark());
        pack.setWithin(12);
        boolean b = supportApplyRedPackService.saveOrUpdate(pack);
        AssertUtil.isTrue(b,CommonMsgCode.ERROR);

        //审批历史生成
        BigDecimal payAmount = form.getRedTotalAmount().multiply(new BigDecimal(1).add(serviceCharge));
        SupportRedAuditRecord record=new SupportRedAuditRecord();
        record.setOpenRedId(pack.getId());
        record.setApplyType(1);
        record.setPayCoin(form.getRedCoin());
        record.setPayAmount(payAmount);
        record.setProjectCoin(apply.getCoin());
        record.setAuditStatus(AuditStatusEnum.PENDING);
        record.setUpCoinId(apply.getId());
        record.setMemberId(memberId);
        record.setDeleteFlag(0);
        record.setRemark(form.getRemark());
        boolean save = supportRedAuditRecordService.save(record);
        AssertUtil.isTrue(save, CommonMsgCode.ERROR);


        //支付记录
        SupportPayRecords payRecords=new SupportPayRecords();
        payRecords.setMemberId(memberId);
        payRecords.setUpCoinId(apply.getId());
        payRecords.setModuleType(ModuleType.APPLY_RED_PACK);
        payRecords.setPayType(0);
        payRecords.setPayCoin(form.getRedCoin());
        payRecords.setPayAmount(payAmount);
        payRecords.setRemark("红包申请-->支付");
        payRecords.setApplyId(record.getId());
        boolean save1 = supportPayRecordsService.save(payRecords);
        AssertUtil.isTrue(save1, CommonMsgCode.ERROR);


        //支付
        WalletTradeEntity entity=new WalletTradeEntity();
        entity.setType(TransactionType.RED_PACK_COST);
        entity.setRefId(String.valueOf(payRecords.getId()));
        entity.setMemberId(payRecords.getMemberId());
        entity.setCoinUnit(payRecords.getPayCoin());
        entity.setTradeBalance(payRecords.getPayAmount().negate());
        entity.setComment("项目方新增红包");
        MessageRespResult<Boolean> result = memberWalletApiService.trade(entity);
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(result);

        //红包账户收款
        try {
            WalletTradeEntity re=new WalletTradeEntity();
            re.setType(TransactionType.RED_PACK_COST);
            re.setRefId(String.valueOf(payRecords.getId()));
            re.setMemberId(redPackAccountId);
            re.setCoinUnit(payRecords.getPayCoin());
            re.setTradeBalance(payRecords.getPayAmount());
            re.setComment("项目方新增红包-->收款");
            MessageRespResult<Boolean> reResult = memberWalletApiService.trade(re);
            ExceptionUitl.throwsMessageCodeExceptionIfFailed(reResult);
        }catch (Exception e){
            log.info("红包账户收款异常:{}",ExceptionUtils.getFullStackTrace(e));
        }
    }
    /**
     * 红包申请历史表
     */
    public IPage<ApplyRedPackListVo> applyRedPackList(Long memberId, RedPackParam param) {
        IPage<ApplyRedPackListVo> page=new Page<>(param.getPage(),param.getPageSize());
        SupportUpCoinApply ap = supportUpCoinApplyService.findApprovedUpCoinByMember(memberId);
        List<ApplyRedPackListVo> res=supportApplyRedPackService.applyRedPackList(page,param,ap.getCoin());
        Date current=new Date();
        for (ApplyRedPackListVo vo:res){
            Date startTime = vo.getStartTime();
            Date endTime = vo.getEndTime();
            if (startTime.after(current)){
                vo.setRedStatus(0);
            }
            if(startTime.before(current)&&current.before(endTime)){
                vo.setRedStatus(1);
            }
            if (current.after(endTime)){
                vo.setRedStatus(2);
            }
            if(vo.getReceiveType()==1){
                vo.setRedTotalCount(0);
            }
        }
        page.setRecords(res);
        return page;
    }

    /**
     * 审批历史查询
     * @param applyRedPackId
     * @param type
     * @return
     */
    public List<ApplyRedPackAuditRecordVo> applyAuditHistory(Long applyRedPackId, Integer type) {
        return supportRedAuditRecordService.applyRedPackAuditHistory(applyRedPackId,type);
    }

    /**
     * 红包领取明细
     * @param applyRedPackId
     * @param param
     * @return
     */
    public IPage<RedPackRecieveDetailVo> applyRedPackStatics(Long applyRedPackId, RedRecieveParam param) {
        IPage<RedPackRecieveDetailVo> page=new Page<>(param.getPage(),param.getPageSize());
        SupportApplyRedPack pa = supportApplyRedPackService.getById(applyRedPackId);
        if(pa!=null&&pa.getRedPackManageId()!=null){
            List<RedPackRecieveDetailVo> list=supportApplyRedPackService.applyRedPackStatics(page,param,pa.getRedPackManageId());
            Integer newMemberCount = supportApplyRedPackService.findNewMemberCount(pa.getRedPackManageId());
            for (RedPackRecieveDetailVo vo:list){
                vo.setNewMemberCount(newMemberCount);
                vo.setRedpackId(applyRedPackId);
            }
            page.setRecords(list);
        }
        return page;
    }

    /**
     * 红包优先级申请
     * @param addAmount
     * @param payCoin
     * @param memberId
     */
    @Transactional(rollbackFor = Exception.class)
    public void applyPriority(BigDecimal addAmount, String payCoin, Long memberId,Long applyRedPackId) {
        MessageRespResult<SilkDataDist> one2 = dataDistApiService.findOne("RED_PACK_CONFIG", "TOTAL_ACCOUNT_ID");
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(one2);
        Long redPackAccountId=Long.valueOf(one2.getData().getDictVal());
        SupportApplyRedPack pack = supportApplyRedPackService.getById(applyRedPackId);
        AssertUtil.isTrue(pack!=null&&pack.getAuditStatus()==AuditStatusEnum.APPROVED,
                SupportCoinMsgCode.RED_PACK_NOT_FIND);

        //验证是否能申请
        List<SupportRedAuditRecord> res = supportRedAuditRecordService.pendingList(applyRedPackId, 2);
        AssertUtil.isTrue(CollectionUtils.isEmpty(res),SupportCoinMsgCode.HAS_EXIST_OPEN_APPLY);
        //支付基础金额
        MessageRespResult<SilkDataDist> one = dataDistApiService.findOne("SUPPORT_RED_PACK", "PRIORITY_COST");
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(one);
        BigDecimal baseCost=new BigDecimal(one.getData().getDictVal());
        BigDecimal total=baseCost.add(addAmount);

        SupportUpCoinApply app = supportUpCoinApplyService.findApprovedUpCoinByMember(memberId);
        //审批历史生成
        SupportRedAuditRecord record=new SupportRedAuditRecord();
        record.setOpenRedId(applyRedPackId);
        record.setApplyType(2);
        record.setPayCoin(payCoin);
        record.setPayAmount(total);
        record.setProjectCoin(app.getCoin());
        record.setAuditStatus(AuditStatusEnum.PENDING);
        record.setUpCoinId(app.getId());
        record.setMemberId(memberId);
        record.setDeleteFlag(0);
        boolean save = supportRedAuditRecordService.save(record);
        AssertUtil.isTrue(save, CommonMsgCode.ERROR);

        //支付记录
        SupportPayRecords payRecords=new SupportPayRecords();
        payRecords.setMemberId(memberId);
        payRecords.setUpCoinId(app.getId());
        payRecords.setModuleType(ModuleType.PRIORITY_APPLY);
        payRecords.setPayType(0);
        payRecords.setPayCoin(payCoin);
        payRecords.setPayAmount(total);
        payRecords.setRemark("红包优先级申请-->支付");
        payRecords.setApplyId(record.getId());
        boolean save1 = supportPayRecordsService.save(payRecords);
        AssertUtil.isTrue(save1, CommonMsgCode.ERROR);


        //支付
        WalletTradeEntity entity=new WalletTradeEntity();
        entity.setType(TransactionType.SUPPORT_PROJECT_PAY);
        entity.setRefId(String.valueOf(payRecords.getId()));
        entity.setMemberId(payRecords.getMemberId());
        entity.setCoinUnit(payRecords.getPayCoin());
        entity.setTradeBalance(payRecords.getPayAmount().negate());
        entity.setComment("红包推荐首页");
        MessageRespResult<Boolean> result = memberWalletApiService.trade(entity);
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(result);

        //红包账户收款
        try {
            WalletTradeEntity re=new WalletTradeEntity();
            re.setType(TransactionType.SUPPORT_PROJECT_PAY);
            re.setRefId(String.valueOf(payRecords.getId()));
            re.setMemberId(redPackAccountId);
            re.setCoinUnit(payRecords.getPayCoin());
            re.setTradeBalance(payRecords.getPayAmount());
            re.setComment("红包推荐首页-->收款");
            MessageRespResult<Boolean> reResult = memberWalletApiService.trade(re);
            ExceptionUitl.throwsMessageCodeExceptionIfFailed(reResult);
        }catch (Exception e){
            log.info("红包账户收款异常:{}",ExceptionUtils.getFullStackTrace(e));
        }
    }
}
