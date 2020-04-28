package com.spark.bitrade.biz;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.entity.SupportPayRecords;
import com.spark.bitrade.entity.SupportSectionManage;
import com.spark.bitrade.entity.SupportUpCoinApply;
import com.spark.bitrade.form.ChangeSectionForm;
import com.spark.bitrade.param.SectionSearchParam;
import com.spark.bitrade.vo.SupportSectionManageVo;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.11.05 15:11  
 */
public interface ISectionManageService {


    SupportSectionManage generateApply(Long memberId, ChangeSectionForm form, SupportUpCoinApply apply);

    void doApplySectionManage(SupportSectionManage sectionManage, SupportPayRecords payRecords);

    IPage<SupportSectionManageVo> findSectionRecords(Long memberId, SectionSearchParam param);
}
