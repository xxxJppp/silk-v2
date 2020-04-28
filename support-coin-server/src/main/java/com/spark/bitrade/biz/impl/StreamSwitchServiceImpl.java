package com.spark.bitrade.biz.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.biz.IStreamSwitchService;
import com.spark.bitrade.constant.AuditStatusEnum;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constant.SupportCoinMsgCode;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.SupportPayRecords;
import com.spark.bitrade.entity.SupportStreamRecords;
import com.spark.bitrade.param.StreamSearchParam;
import com.spark.bitrade.service.IMemberWalletApiService;
import com.spark.bitrade.service.SupportPayRecordsService;
import com.spark.bitrade.service.SupportStreamRecordsService;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.vo.SupportStreamSwitchVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.11.06 14:10  
 */
@Service
public class StreamSwitchServiceImpl implements IStreamSwitchService {

    @Autowired
    private SupportPayRecordsService supportPayRecordsService;

    @Autowired
    private SupportStreamRecordsService supportStreamRecordsService;

    @Autowired
    private IMemberWalletApiService memberWalletApiService;
    @Override
    public SupportStreamRecords generateStreamRecord(Long memberId, String remark, Long upCoinId) {

        SupportStreamRecords records = new SupportStreamRecords();
        records.setMemberId(memberId);
        records.setUpCoinId(upCoinId);
        records.setSwitchStatus(1);
        records.setRemark(remark);
        records.setAuditStatus(AuditStatusEnum.PENDING);
        records.setOperateType(0);
        return records;
    }

    /**
     * 执行引流事务
     * @param streamRecords
     * @param payRecords
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void doSaveStreamSwitchApply(SupportStreamRecords streamRecords, SupportPayRecords payRecords) {
        //验证是否存在正在审批的记录
        QueryWrapper<SupportStreamRecords> qs=new QueryWrapper<>();
        qs.lambda().eq(SupportStreamRecords::getAuditStatus,AuditStatusEnum.PENDING)
                .eq(SupportStreamRecords::getMemberId,streamRecords.getMemberId())
                .eq(SupportStreamRecords::getDeleteFlag, BooleanEnum.IS_FALSE);
        List<SupportStreamRecords> list = supportStreamRecordsService.list(qs);
        AssertUtil.isTrue(CollectionUtils.isEmpty(list), SupportCoinMsgCode.STREAM_SWITCH_APPLY_HAS_ALREADY_EXIST);

        boolean f1 = supportStreamRecordsService.save(streamRecords);
        payRecords.setApplyId(streamRecords.getId());
        boolean f2 = supportPayRecordsService.save(payRecords);

        AssertUtil.isTrue(f1&&f2,SupportCoinMsgCode.SAVE_STREAM_FAILED);

        //支付
        WalletTradeEntity entity=new WalletTradeEntity();
        entity.setType(TransactionType.SUPPORT_PROJECT_PAY);
        entity.setRefId(String.valueOf(payRecords.getId()));
        entity.setMemberId(payRecords.getMemberId());
        entity.setCoinUnit(payRecords.getPayCoin());
        entity.setTradeBalance(BigDecimal.ZERO.subtract(payRecords.getPayAmount()));
        entity.setComment("打开引流开关");
        MessageRespResult<Boolean> result = memberWalletApiService.trade(entity);
        AssertUtil.isTrue(result.isSuccess()&&result.getData(), CommonMsgCode.ACCOUNT_BALANCE_TRADE_FAILED);
    }

    @Override
    public IPage<SupportStreamSwitchVo> findStreamSwitchRecord(Long memberId, StreamSearchParam param) {
        IPage page=new Page(param.getPage(),param.getPageSize());
        List<SupportStreamSwitchVo> switchRecord = supportStreamRecordsService.findStreamSwitchRecord(memberId,param , page);
        page.setRecords(switchRecord);
        if(!CollectionUtils.isEmpty(switchRecord)){
            switchRecord.forEach(s->{
                Integer switchStatus = s.getSwitchStatus();
                if(switchStatus==1){
                    s.setSwitchStatusName("打开引流开关");
                }else {
                    s.setSwitchStatusName("关闭引流开关");
                }
                s.setAuditStatusName(s.getAuditStatus().getCnName());

            });
        }

        return page;
    }
}
