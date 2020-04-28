package com.spark.bitrade.biz.impl;

import com.spark.bitrade.biz.ICoinRecordService;
import com.spark.bitrade.biz.IUpCoinApplyService;
import com.spark.bitrade.constant.AuditStatusEnum;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constant.SupportCoinMsgCode;
import com.spark.bitrade.constant.SupportInternationalKey;
import com.spark.bitrade.entity.SupportCoinRecords;
import com.spark.bitrade.entity.SupportInternational;
import com.spark.bitrade.entity.SupportUpCoinApply;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.form.SupportCoinRecordsForm;
import com.spark.bitrade.service.SupportCoinRecordsService;
import com.spark.bitrade.service.SupportInternationalService;
import com.spark.bitrade.service.SupportUpCoinApplyService;
import com.spark.bitrade.vo.CoinApplyVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.05 11:39
 */
@Service
public class CoinRecordServiceImpl implements ICoinRecordService {

    @Autowired
    private IUpCoinApplyService upCoinApplyService;

    @Autowired
    private SupportUpCoinApplyService coinRecordsService;

    @Autowired
    private SupportCoinRecordsService recordsService;

    @Autowired
    private SupportInternationalService internationalService;


    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public CoinApplyVo getCoinApplyVo(Long memberId) {
        return upCoinApplyService.getSupportUpCoinApply(memberId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveCoinRecordApply(SupportCoinRecordsForm recordsForm) {
        SupportUpCoinApply record = coinRecordsService.findApprovedUpCoinByMember(Long.valueOf(recordsForm.getMemberId()));
        Long memberId = Long.valueOf(recordsForm.getMemberId());
        // 获取是否存在审核中的
        SupportCoinRecords oneCoinRecord = recordsService.getOneCoinRecords(memberId, record.getId());
        // 判断是否存在币种基本信息修改申请
        if (oneCoinRecord != null && oneCoinRecord.getAuditStatus() == AuditStatusEnum.PENDING) {
            // 存在并且审核状态为待审核
            throw new MessageCodeException(SupportCoinMsgCode.COINRECORD_IS_EXIST_PENDING);
        }
        // 不存在或者审核状态为已通过，未通过 最终只会有一条记录
        // 更新之前的数据或者新增
        SupportCoinRecords records = this.setCoinRecordsEntity(recordsForm);
        records.setUpCoinId(record.getId());
        records.setMemberId(memberId);
        records.setUpdateTime(new Date());
        recordsService.saveOrUpdate(records);

        // 保存多语言
        SupportInternational international = new SupportInternational();
        // 保存中文
        if (StringUtils.isNotBlank(recordsForm.getIntroCn())) {
            international.setInternationalKey(records.getIntroKey() + SupportInternationalKey.ZH_CH.getKey());
            saveInternational(international, recordsForm.getIntroCn());
        }

        // 保存英文
        if (StringUtils.isNotBlank(recordsForm.getIntroEn())) {
            international.setInternationalKey(records.getIntroKey() + SupportInternationalKey.US_EN.getKey());
            saveInternational(international, recordsForm.getIntroEn());
        }

        // 保存繁体
        if (StringUtils.isNotBlank(recordsForm.getIntroHk())) {
            international.setInternationalKey(records.getIntroKey() + SupportInternationalKey.ZH_HK.getKey());
            saveInternational(international, recordsForm.getIntroHk());
        }

        // 保存韩文
        if (StringUtils.isNotBlank(recordsForm.getIntroKo())) {
            international.setInternationalKey(records.getIntroKey() + SupportInternationalKey.KO_KR.getKey());
            saveInternational(international, recordsForm.getIntroKo());
        }
    }

    /**
     * 保存多语言
     *
     * @param international 对象
     * @param record        简介
     */
    private void saveInternational(SupportInternational international, String record) {
        international.setCreateTime(new Date());
        international.setDeleteFlag(BooleanEnum.IS_FALSE);
        international.setValue(record);
        internationalService.save(international);
    }


    private SupportCoinRecords setCoinRecordsEntity(SupportCoinRecordsForm recordsForm) {
        SupportCoinRecords coinRecords = new SupportCoinRecords();
        coinRecords.setCreateTime(new Date());
        String key = System.currentTimeMillis() + SupportInternationalKey.UP_COIN_UPDATE_INTRO.getKey();
        coinRecords.setIntroKey(key);
        coinRecords.setLinkPhone(recordsForm.getLinkPhone());
        coinRecords.setDeleteFlag(BooleanEnum.IS_FALSE);
        coinRecords.setUpdateTime(new Date());
        coinRecords.setCreateTime(new Date());
        coinRecords.setAreaCode(recordsForm.getAreaCode());
        coinRecords.setAuditStatus(AuditStatusEnum.PENDING);
        coinRecords.setAuditOpinion("");
        return coinRecords;
    }
}
