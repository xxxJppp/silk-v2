package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.entity.SupportSectionManage;
import com.spark.bitrade.mapper.SupportSectionManageMapper;
import com.spark.bitrade.param.SectionSearchParam;
import com.spark.bitrade.service.SupportSectionManageService;
import com.spark.bitrade.vo.SupportSectionManageVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 转版管理 服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
@Service
public class SupportSectionManageServiceImpl extends ServiceImpl<SupportSectionManageMapper, SupportSectionManage> implements SupportSectionManageService {

    @Override
    public List<SupportSectionManageVo> findSectionRecords(Long memberId, IPage page, SectionSearchParam param) {
        param.transTime();
        return baseMapper.findSectionRecords(memberId,page ,param );
    }
}
