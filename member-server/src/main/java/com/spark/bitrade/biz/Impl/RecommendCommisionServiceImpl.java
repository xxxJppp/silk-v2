package com.spark.bitrade.biz.Impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.api.MemberFeignApi;
import com.spark.bitrade.biz.IBenefitsOrderService;
import com.spark.bitrade.biz.IRecommendCommisionService;
import com.spark.bitrade.constant.BizRemarksEnum;
import com.spark.bitrade.constant.BizTypeEnum;
import com.spark.bitrade.constant.PayTypeEnum;
import com.spark.bitrade.entity.MemberRecommendCommision;
import com.spark.bitrade.mapper.MemberRecommendCommisionMapper;
import com.spark.bitrade.param.PageParam;
import com.spark.bitrade.service.MemberRecommendCommisionService;
import com.spark.bitrade.util.HttpRequestUtil;
import com.spark.bitrade.vo.MemberRecommendCommisionVo;
import com.spark.bitrade.vo.RecommendCommisionVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author: Zhong Jiang
 * @date: 2019-11-21 9:24
 */
@Service
@Slf4j
public class RecommendCommisionServiceImpl implements IRecommendCommisionService {

    @Autowired
    private MemberRecommendCommisionService commisionService;

    @Autowired
    private IBenefitsOrderService benefitsOrderService;


    @Override
    public IPage<RecommendCommisionVo> findMemberRecommendCommisionsByBuy(Long memberId, PageParam param) {
        Page<RecommendCommisionVo> commisionPage = new Page<>(param.getPage(), param.getPageSize());
        List<RecommendCommisionVo> records = commisionService.findRecommendCommisionListMapper(commisionPage, memberId, param.getStartTime(), param.getEndTime());
        records = records.stream().filter(filter -> filter.getBizType().intValue() != 40).collect(Collectors.toList());
        // 确认列表备注
//        List<MemberRecommendCommision> records = recommendCommisionList.getRecords();
        String language = HttpRequestUtil.getHttpServletRequest().getHeader("language");
        for (RecommendCommisionVo record : records) {
            if (record.getBizType() == BizTypeEnum.TOKEN_EXCHANGE.getCode()) {
                if ("zh_CN".equals(language)) {
                    record.setRemarks(BizRemarksEnum.RECOMMEND_RETURN.getName());
                } else if ("en_US".equals(language)) {
                    record.setRemarks(BizRemarksEnum.RETURN_COMMISSION_EN.getName());
                } else if ("ko_KR".equals(language)) {
                    record.setRemarks(BizRemarksEnum.RETURN_COMMISSION_KO.getName());
                } else if ("zh_HK".equals(language)) {
                    record.setRemarks(BizRemarksEnum.RETURN_COMMISSION_HK.getName());
                }
            } else {
            	if(record.getBizType() == BizTypeEnum.CURRENCY_GET.getCode()) {
            		record.setRemarks(BizTypeEnum.CURRENCY_GET.getName());
            	}
            	else if(record.getBizType() == BizTypeEnum.INV_MEMBER_GET.getCode()) {
            		record.setRemarks(BizTypeEnum.INV_MEMBER_GET.getName());
            	}
            	else if(record.getBizType() == BizTypeEnum.INV_CT_GET.getCode()) {
            		record.setRemarks(BizTypeEnum.INV_CT_GET.getName());
            	}
            	else {
            		String remarks = benefitsOrderService.confirmRemarks(record.getRefId());
            		record.setRemarks(remarks);
            	}
            }
        }
        commisionPage.setRecords(records);
        return commisionPage;
    }

    @Override
    public IPage<MemberRecommendCommision> findMemberRecommendCommisionsByExchange(Long memberId, PageParam param) {
        IPage<MemberRecommendCommision> recommendCommisionList = commisionService.getRecommendCommisionList(memberId, param, PayTypeEnum.COMMUNITY_SIZE.getCode());
        List<MemberRecommendCommision> list = recommendCommisionList.getRecords();
        list = list.stream().filter(filter -> filter.getBizType().intValue() != 40).collect(Collectors.toList());
        String language = HttpRequestUtil.getHttpServletRequest().getHeader("language");
        for (MemberRecommendCommision e : list) {
            if ("zh_CN".equals(language)) {
                e.setRemarks(BizRemarksEnum.RETURN_COMMISSION.getName());
            } else if ("en_US".equals(language)) {
                e.setRemarks(BizRemarksEnum.RETURN_COMMISSION_EN.getName());
            } else if ("ko_KR".equals(language)) {
                e.setRemarks(BizRemarksEnum.RETURN_COMMISSION_KO.getName());
            } else if ("zh_HK".equals(language)) {
                e.setRemarks(BizRemarksEnum.RETURN_COMMISSION_HK.getName());
            }

        }
        return recommendCommisionList;
    }

    @Override
    public IPage<MemberRecommendCommision> findMemberRecommendCommisionsBySend(Long memberId, PageParam param) {
        IPage<MemberRecommendCommision> commisionBySend = commisionService.getRecommendCommisionBySend(memberId, param);
        List<MemberRecommendCommision> list = commisionBySend.getRecords();
        list = list.stream().filter(filter -> filter.getBizType().intValue() != 40).collect(Collectors.toList());
        String language = HttpRequestUtil.getHttpServletRequest().getHeader("language");
        for (MemberRecommendCommision e : list) {
            if ("zh_CN".equals(language)) {
                e.setRemarks(BizRemarksEnum.COMMISSION_REBATE.getName());
            } else if ("en_US".equals(language)) {
                e.setRemarks(BizRemarksEnum.COMMISSION_REBATE_EN.getName());
            } else if ("ko_KR".equals(language)) {
                e.setRemarks(BizRemarksEnum.COMMISSION_REBATE_KO.getName());
            } else if ("zh_HK".equals(language)) {
                e.setRemarks(BizRemarksEnum.COMMISSION_REBATE_HK.getName());
            }
        }
        commisionBySend.setRecords(list);
        return commisionBySend;
    }

    @Override
    public List<MemberRecommendCommisionVo> countRecommendCommision(Long meberId) {
        List<MemberRecommendCommision> list = commisionService.countMemberRecommendCommision(meberId);
        list = list.stream().filter(filter -> filter.getBizType().intValue() != 40).collect(Collectors.toList());
        List<MemberRecommendCommisionVo> result = new ArrayList<>();
        MemberRecommendCommisionVo vo = new MemberRecommendCommisionVo();
        vo.setCommisionUnit("SLU");
        MemberRecommendCommisionVo vo1 = new MemberRecommendCommisionVo();
        vo1.setCommisionUnit("USDT");
        MemberRecommendCommisionVo vo2 = new MemberRecommendCommisionVo();
        vo2.setCommisionUnit("USDC");
        for (MemberRecommendCommision commision : list) {
            if ("SLU".equals(commision.getCommisionUnit())) {
                vo.setCommisionUnit(commision.getCommisionUnit());
                if (commision.getBizType() == BizTypeEnum.TOKEN_EXCHANGE.getCode()) {
                    vo.setCountExchange(vo.getCountExchange().add(commision.getTempCount()));
                } else {
                    vo.setCountCommision(vo.getCountCommision().add(commision.getTempCount()));
                }
            } else if ("USDT".equals(commision.getCommisionUnit())) {
                if (commision.getBizType() == BizTypeEnum.TOKEN_EXCHANGE.getCode()) {
                    vo1.setCountExchange(vo1.getCountExchange().add(commision.getTempCount()));
                } else {
                    vo1.setCountCommision(vo1.getCountCommision().add(commision.getTempCount()));
                }
            }else if ("USDC".equals(commision.getCommisionUnit())) {
            	vo2.setCountCommision(vo2.getCountCommision().add(commision.getTempCount()));
            }
        }
        vo.setSumCount(vo.getCountExchange().add(vo.getCountCommision()));
        vo1.setSumCount(vo1.getCountExchange().add(vo1.getCountCommision()));
        vo2.setSumCount(vo2.getCountCommision());
        result.add(0, vo);
        result.add(1, vo1);
        result.add(2, vo2);
        return result;
    }
}
