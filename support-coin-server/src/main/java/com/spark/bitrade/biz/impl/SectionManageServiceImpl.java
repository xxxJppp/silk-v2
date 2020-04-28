package com.spark.bitrade.biz.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.biz.ISectionManageService;
import com.spark.bitrade.constant.*;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.SupportPayRecords;
import com.spark.bitrade.entity.SupportSectionManage;
import com.spark.bitrade.entity.SupportUpCoinApply;
import com.spark.bitrade.form.ChangeSectionForm;
import com.spark.bitrade.param.SectionSearchParam;
import com.spark.bitrade.service.IMemberWalletApiService;
import com.spark.bitrade.service.SupportPayRecordsService;
import com.spark.bitrade.service.SupportSectionManageService;
import com.spark.bitrade.service.SupportUpCoinApplyService;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.vo.SupportSectionManageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 *  
 *   转版管理业务
 *  @author liaoqinghui  
 *  @time 2019.11.05 15:11  
 */
@Service
public class SectionManageServiceImpl implements ISectionManageService {
    @Autowired
    private SupportUpCoinApplyService supportUpCoinApplyService;
    @Autowired
    private SupportSectionManageService supportSectionManageService;
    @Autowired
    private SupportPayRecordsService supportPayRecordsService;
    @Autowired
    private IMemberWalletApiService memberWalletApiService;
    /**
     * 生成申请记录
     *
     * @param memberId
     * @param form
     * @param apply
     * @return
     */
    @Override
    public SupportSectionManage generateApply(Long memberId, ChangeSectionForm form, SupportUpCoinApply apply) {


        AssertUtil.isTrue(apply.getRealSectionType()!= SectionTypeEnum.MAIN_ZONE,SupportCoinMsgCode.CANT_CHANGE_SECTION);
        AssertUtil.isTrue(form.getTargetSection()== SectionTypeEnum.MAIN_ZONE,SupportCoinMsgCode.CANT_CHANGE_SECTION);
        SupportSectionManage manage = new SupportSectionManage();
        manage.setCurrentSection(apply.getRealSectionType());
        manage.setTargetSection(form.getTargetSection());
        manage.setMemberId(memberId);
        manage.setUpCoinId(apply.getId());
        manage.setRemark(form.getRemark());
        manage.setAuditStatus(AuditStatusEnum.PENDING);

        return manage;
    }

    /**
     * 执行转版管理申请
     *
     * @param sectionManage
     * @param payRecords
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void doApplySectionManage(SupportSectionManage sectionManage, SupportPayRecords payRecords) {

        QueryWrapper<SupportSectionManage> sqm = new QueryWrapper<>();
        sqm.lambda().eq(SupportSectionManage::getMemberId, sectionManage.getMemberId())
                .eq(SupportSectionManage::getAuditStatus, AuditStatusEnum.PENDING)
                .eq(SupportSectionManage::getDeleteFlag, BooleanEnum.IS_FALSE);
        List<SupportSectionManage> list = supportSectionManageService.list(sqm);
        AssertUtil.isTrue(CollectionUtils.isEmpty(list), SupportCoinMsgCode.CHANGE_SECTION_HAS_ALREADY_EXIST);
        boolean manageFlag = supportSectionManageService.save(sectionManage);
        payRecords.setApplyId(sectionManage.getId());
        boolean payRecordFlag = supportPayRecordsService.save(payRecords);


        AssertUtil.isTrue(manageFlag && payRecordFlag, SupportCoinMsgCode.SAVE_CHANGE_SECTION_FAILED);

        //支付
        WalletTradeEntity entity=new WalletTradeEntity();
        entity.setType(TransactionType.SUPPORT_PROJECT_PAY);
        entity.setRefId(String.valueOf(payRecords.getId()));
        entity.setMemberId(payRecords.getMemberId());
        entity.setCoinUnit(payRecords.getPayCoin());
        entity.setTradeBalance(BigDecimal.ZERO.subtract(payRecords.getPayAmount()));
        entity.setComment("转版管理支付");
        MessageRespResult<Boolean> result = memberWalletApiService.trade(entity);
        AssertUtil.isTrue(result.isSuccess()&&result.getData(), CommonMsgCode.ACCOUNT_BALANCE_TRADE_FAILED);

    }

    @Override
    public IPage<SupportSectionManageVo> findSectionRecords(Long memberId, SectionSearchParam param) {
        IPage page = new Page(param.getPage(), param.getPageSize());
        List<SupportSectionManageVo> sectionRecords = supportSectionManageService.findSectionRecords(memberId, page, param);
        page.setRecords(sectionRecords);
        sectionRecords.forEach(s -> {
            s.setCurrentSectionName(s.getCurrentSection().getCnName());
            s.setTargetSectionName(s.getTargetSection().getCnName());
            if (s.getAuditStatus() == AuditStatusEnum.PENDING) {
                s.setAuditStatusName("对接中");
            } else {
                s.setAuditStatusName(s.getAuditStatus().getCnName());
            }
        });
        return page;
    }


}


















