package com.spark.bitrade.biz.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.spark.bitrade.biz.ICoinMatchService;
import com.spark.bitrade.biz.IUpCoinApplyService;
import com.spark.bitrade.constant.AuditStatusEnum;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constant.SupportCoinMsgCode;
import com.spark.bitrade.constant.SupportInternationalKey;
import com.spark.bitrade.entity.SupportCoinRecords;
import com.spark.bitrade.entity.SupportInternational;
import com.spark.bitrade.entity.SupportUpCoinApply;
import com.spark.bitrade.form.SupportUpCoinApplyForm;
import com.spark.bitrade.param.PageParam;
import com.spark.bitrade.service.SupportCoinRecordsService;
import com.spark.bitrade.service.SupportInternationalService;
import com.spark.bitrade.service.SupportUpCoinApplyService;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.GeneratorUtil;
import com.spark.bitrade.util.SupportUtil;
import com.spark.bitrade.vo.CoinApplyVo;
import com.spark.bitrade.vo.CoinMatchVo;
import com.spark.bitrade.vo.NumberOfPeopleVo;
import com.spark.bitrade.vo.UpCoinApplyRecordVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.11.05 09:24  
 */
@Service
public class UpCoinApplyServiceImpl implements IUpCoinApplyService {

    @Autowired
    private SupportUpCoinApplyService supportUpCoinApplyService;

    @Autowired
    private SupportInternationalService supportInternationalService;

    @Autowired
    private ICoinMatchService coinMatchService;

    @Autowired
    private SupportCoinRecordsService coinRecordsService;

    /**
     * 验证是否能申请上币
     *
     * @param memberId
     */
    @Override
    public void validateCanUpCoin(Long memberId,String coin) {

        //验证是否存在上币申请
        QueryWrapper<SupportUpCoinApply> qw = new QueryWrapper<>();
        qw.lambda().ne(SupportUpCoinApply::getAuditStatus, AuditStatusEnum.REJECT)
                .eq(SupportUpCoinApply::getMemberId, memberId)
                .eq(SupportUpCoinApply::getDeleteFlag, BooleanEnum.IS_FALSE);

        List<SupportUpCoinApply> applies = supportUpCoinApplyService.list(qw);
        AssertUtil.isTrue(CollectionUtils.isEmpty(applies), SupportCoinMsgCode.APPLY_HAS_ALREADY_EXIST);
        QueryWrapper<SupportUpCoinApply> qw22= new QueryWrapper<>();
        qw22.lambda().ne(SupportUpCoinApply::getAuditStatus, AuditStatusEnum.REJECT)
                .eq(SupportUpCoinApply::getDeleteFlag, BooleanEnum.IS_FALSE)
                .eq(SupportUpCoinApply::getCoin,coin);
        List<SupportUpCoinApply> a  = supportUpCoinApplyService.list(qw);
        AssertUtil.isTrue(CollectionUtils.isEmpty(a), SupportCoinMsgCode.COIN_HAS_ALREADY_EXIST);
    }

    /**
     * 执行上币逻辑保存
     *
     * @param memberId
     * @param form
     */
    @Override
    public void doUpCoinApply(Long memberId, SupportUpCoinApplyForm form) {
        SupportUpCoinApply apply = new SupportUpCoinApply();

        BeanUtils.copyProperties(form, apply);
        apply.setMemberId(memberId);
        //附件地址处理
        String[] attchArray = form.getAttchArray();
        if (attchArray != null&&attchArray.length>0) {
            String[] arrs = new String[attchArray.length];
            for (int i = 0; i < arrs.length; i++) {
                String[] nameUrls = attchArray[i].split("@");
                if (nameUrls != null && nameUrls.length > 1) {
                    arrs[i] = SupportUtil.decodeUrl(nameUrls[0]) + "@" + nameUrls[1];
                }
            }
            apply.setAttchUrls(JSON.toJSONString(arrs));
        }
        //简介处理
        //简介内容
        String coinIntro = form.getProjectIntro();
        //简介KEY
        String key = memberId + "" + System.currentTimeMillis() + SupportInternationalKey.PROJECT_INTRO_KEY.getKey();
        apply.setProjectIntroKey(key);
        apply.setTradeCode(memberId + String.valueOf(GeneratorUtil.getRandomNumber(1000, 9999)));
        apply.setStreamStatus(0);
        apply.setAuditStatus(AuditStatusEnum.PENDING);
        supportUpCoinApplyService.save(apply);

        //默认生成中文国际化
        SupportInternational zh = new SupportInternational();
        zh.setInternationalKey(key + SupportInternationalKey.ZH_CH.getKey());
        zh.setValue(coinIntro);
        supportInternationalService.save(zh);
    }

    @Override
    public UpCoinApplyRecordVo findUpCoinRecordByMember(Long memberId) {
        QueryWrapper<SupportUpCoinApply> qw = new QueryWrapper<>();
        qw.lambda().eq(SupportUpCoinApply::getMemberId, memberId)
                .eq(SupportUpCoinApply::getDeleteFlag, BooleanEnum.IS_FALSE).orderByDesc(SupportUpCoinApply::getCreateTime);
        List<SupportUpCoinApply> supports = supportUpCoinApplyService.list(qw);
        if (CollectionUtils.isEmpty(supports)) {
            return null;
        }
        SupportUpCoinApply apply = supports.get(0);
        UpCoinApplyRecordVo vo = new UpCoinApplyRecordVo();
        BeanUtils.copyProperties(apply, vo);

        //微信url解析
        vo.setWechatUrl(SupportUtil.generateImageUrl(apply.getWechatUrl()));
        //附件地址解析
        List<String> files = JSONArray.parseArray(apply.getAttchUrls(), String.class);
        if (!CollectionUtils.isEmpty(files)) {
            files.removeAll(Collections.singleton(null));
            List<JSONObject> urls = new ArrayList<>();
            files.forEach(s -> {
                        String[] nameUrls = s.split("@");
                        JSONObject object = new JSONObject();
                        if (nameUrls != null && nameUrls.length > 1) {
                            object.put("name", nameUrls[1]);
                            object.put("url", SupportUtil.generateImageUrl(nameUrls[0]));
                            urls.add(object);
                        }
                    }
            );
            vo.setNameUrls(urls);
        }
        //简介解析
        SupportInternational international = supportInternationalService.findOneByinternationalKey(apply.getProjectIntroKey() + SupportInternationalKey.ZH_CH.getKey());
        if (international != null) {
            vo.setProjectIntro(international.getValue());
        }
        return vo;
    }

    @Override
    public CoinApplyVo getSupportUpCoinApply(Long memberId) {
        SupportUpCoinApply record = supportUpCoinApplyService.findApprovedUpCoinByMember(memberId);
        SupportCoinRecords coinRecords = coinRecordsService.getOneCoinRecords(memberId, record.getId());
        CoinApplyVo supportCoinApplyVo = new CoinApplyVo();
        supportCoinApplyVo.setProjectName(record.getName());
        supportCoinApplyVo.setCoinName(record.getCoin());
        List<SupportInternational> internationals = null;
        // 判断是否存在基本信息修改申请
        if (coinRecords != null) {
            // 审核中基本信息修改申请存在
            // 获取多语种简介
            supportCoinApplyVo.setAreaCode(coinRecords.getAreaCode());
            supportCoinApplyVo.setLinkPhone(coinRecords.getLinkPhone());
            supportCoinApplyVo.setAuditStatus(coinRecords.getAuditStatus());
            if(coinRecords.getAuditStatus()!=AuditStatusEnum.PENDING){
                supportCoinApplyVo.setAuditOpinion(coinRecords.getAuditOpinion());
            }

            internationals = supportInternationalService.findByinternationalKey(coinRecords.getIntroKey());
        } else {
            // 基本信息修改申请不存在
            BeanUtils.copyProperties(record, supportCoinApplyVo);
            List<SupportCoinRecords> list = coinRecordsService.getCoinRecordsList(memberId, record.getId());
            if (list != null && list.size() > 0) {
                // 查询最新一次通过或者拒绝的申请审核建议
                supportCoinApplyVo.setAuditOpinion(list.get(0).getAuditOpinion());
                supportCoinApplyVo.setAuditStatus(list.get(0).getAuditStatus());
            }
            internationals = supportInternationalService.findByinternationalKey(record.getIntroKey());
            supportCoinApplyVo.setAuditStatus(AuditStatusEnum.APPROVED);
        }
        // 获取交易对
        List<CoinMatchVo> matchVoList = coinMatchService.findCoinMatchVoList(record.getId());
        supportCoinApplyVo.setCoinMatchList(matchVoList);
        for (SupportInternational international : internationals) {
            if (international.getInternationalKey().endsWith(SupportInternationalKey.US_EN.getKey())) {
                supportCoinApplyVo.setIntroEn(international.getValue());
            } else if (international.getInternationalKey().endsWith(SupportInternationalKey.ZH_CH.getKey())) {
                supportCoinApplyVo.setIntroCn(international.getValue());
            } else if (international.getInternationalKey().endsWith(SupportInternationalKey.ZH_HK.getKey())) {
                supportCoinApplyVo.setIntroHk(international.getValue());
            } else if (international.getInternationalKey().endsWith(SupportInternationalKey.KO_KR.getKey())) {
                supportCoinApplyVo.setIntroKo(international.getValue());
            }
        }
        Integer personCount = supportUpCoinApplyService.validPersonCount(record.getCoin());
        supportCoinApplyVo.setEffectiveUserNum(personCount);
        return supportCoinApplyVo;
    }

    @Override
    public SupportUpCoinApply getById(Long upCoinId) {
        return supportUpCoinApplyService.getById(upCoinId);
    }


    public SupportUpCoinApply getByMemberId(Long memberId) {
        QueryWrapper<SupportUpCoinApply> qw = new QueryWrapper<>();
        qw.lambda().eq(SupportUpCoinApply::getAuditStatus, AuditStatusEnum.APPROVED)
                .eq(SupportUpCoinApply::getMemberId, memberId)
                .eq(SupportUpCoinApply::getDeleteFlag, BooleanEnum.IS_FALSE);
        return supportUpCoinApplyService.getOne(qw);
    }


    @Override
    public Map<String,String> findUpCoinText() {
        return supportUpCoinApplyService.findUpCoinText();
    }

    @Override
    public NumberOfPeopleVo numberOfPeople(String coin, String coinId, PageParam pageParam) {
        pageParam.transTime();
        NumberOfPeopleVo vo = new NumberOfPeopleVo();
        BigDecimal b1 = supportUpCoinApplyService.withRechargeTotal(0, coin, pageParam);
        if(b1!=null){
            vo.setRechargeTotal(b1);
        }
        BigDecimal b2 = supportUpCoinApplyService.withRechargeTotal(1, coin, pageParam);
        if(b2!=null){
            vo.setWithTotal(b2);
        }
        BigDecimal b3 = supportUpCoinApplyService.widthDrawToAuditTotal(coinId, pageParam);
        if(b3!=null){
            vo.setWithToAuditTotal(b3);
        }
        Integer i1 = supportUpCoinApplyService.widthDrawToAuditPersonCount(coinId, pageParam);
        if(i1!=null){
            vo.setWithToAuditPersons(i1);
        }
        Integer i2 = supportUpCoinApplyService.withPersonCount(1, coin, pageParam);
        if(i2!=null){
            vo.setWithedPerson(i2);
        }
        return vo;
    }

}
