package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.entity.SupportNewsInfo;
import com.spark.bitrade.param.NewInfoParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 扶持上币咨询信息 Mapper 接口
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
public interface SupportNewsInfoMapper extends BaseMapper<SupportNewsInfo> {

    List<SupportNewsInfo> findListBymemberIdAndupCoinId(@Param("memberId") Long memberId,
                                                        @Param("upCoinId") Long upCoinId,
                                                        @Param("seacthParam") NewInfoParam seacthParam,
                                                        @Param("page") IPage page);
}
