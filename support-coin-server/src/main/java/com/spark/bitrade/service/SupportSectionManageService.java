package com.spark.bitrade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.SupportSectionManage;
import com.spark.bitrade.param.SectionSearchParam;
import com.spark.bitrade.vo.SupportSectionManageVo;

import java.util.List;

/**
 * <p>
 * 转版管理 服务类
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
public interface SupportSectionManageService extends IService<SupportSectionManage> {

    List<SupportSectionManageVo> findSectionRecords(Long memberId, IPage page, SectionSearchParam param);
}
