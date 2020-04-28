package com.spark.bitrade.biz.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.biz.INewsInfoService;
import com.spark.bitrade.constant.AuditStatusEnum;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constant.SupportCoinMsgCode;
import com.spark.bitrade.entity.SupportNewsInfo;
import com.spark.bitrade.entity.SupportUpCoinApply;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.form.SupportNewsInfoForm;
import com.spark.bitrade.param.NewInfoParam;
import com.spark.bitrade.service.SupportNewsInfoService;
import com.spark.bitrade.service.SupportUpCoinApplyService;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.05 16:16
 */
@Service
public class NewsInfoServiceImpl implements INewsInfoService {

    @Autowired
    private SupportNewsInfoService newsInfoService;

    @Autowired
    private SupportUpCoinApplyService upCoinApplyService;

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public IPage<SupportNewsInfo> findNewsInfosList(Long memberId, NewInfoParam param) {
        SupportUpCoinApply upCoinApply = upCoinApplyService.findApprovedUpCoinByMember(memberId);
        IPage<SupportNewsInfo> page = newsInfoService.findListBymemberIdAndupCoinId(memberId, upCoinApply.getId(), param);
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String saveNewsInfos(SupportNewsInfoForm newsInfoForm) {
        // 判断是否存在还在审核中的资讯
        Long mid = Long.valueOf(newsInfoForm.getMemberId());
        SupportUpCoinApply apply = upCoinApplyService.findApprovedUpCoinByMember(mid);
//        SupportNewsInfo byAuditStatus = newsInfoService.findByAuditStatus(Long.valueOf(newsInfoForm.getMemberId()), apply.getId());
//        AssertUtil.isTrue(byAuditStatus==null,SupportCoinMsgCode.NEWSINFO_IS_EXIST_PENDING);
        SupportNewsInfo newsInfo = new SupportNewsInfo();
        String groupId = newsInfoForm.getGroupId();
        //组ID不为空说明在以前的基础上修改
        if(StringUtils.hasText(groupId)){
            SupportNewsInfo newa= newsInfoService.findByGroupId(groupId);
            if(newa!=null){
                //存在 判断状态不能为待审核和审核通过
//                if ( newa.getAuditStatus().getOrdinal()!= AuditStatusEnum.REJECT.getOrdinal()){
////                    throw new MessageCodeException(SupportCoinMsgCode.NEWSINFO_IS_EXIST_PENDING);
////                }
                newsInfo.setGroupId(groupId);
            }
        }else {
            //新建
            newsInfo.setGroupId(System.currentTimeMillis()+"");
        }
        newsInfo.setTitle(newsInfoForm.getTitle());
        newsInfo.setText(newsInfoForm.getText());
        newsInfo.setMemberId(Long.valueOf(newsInfoForm.getMemberId()));
        newsInfo.setUpCoinId(apply.getId());
        newsInfo.setAuditStatus(AuditStatusEnum.PENDING);
        newsInfo.setDeleteFlag(BooleanEnum.IS_FALSE);
        newsInfo.setCreateTime(new Date());
        newsInfoService.saveOrUpdate(newsInfo);

        return apply.getCoin();
    }
}
