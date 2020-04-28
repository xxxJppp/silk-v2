package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.entity.SupportSectionManage;
import com.spark.bitrade.param.SectionSearchParam;
import com.spark.bitrade.vo.SupportSectionManageVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 转版管理 Mapper 接口
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
public interface SupportSectionManageMapper extends BaseMapper<SupportSectionManage> {


    List<SupportSectionManageVo> findSectionRecords(@Param("memberId") Long memberId,
                                                    @Param("page") IPage page,
                                                    @Param("param") SectionSearchParam param);
}
