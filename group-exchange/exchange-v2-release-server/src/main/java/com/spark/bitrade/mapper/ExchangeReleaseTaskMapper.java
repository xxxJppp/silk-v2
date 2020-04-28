package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.constant.ReleaseTaskStatus;
import com.spark.bitrade.entity.ExchangeReleaseTask;
import org.apache.ibatis.annotations.Param;

/**
 * 币币交易释放-释放任务表(ExchangeReleaseTask)表数据库访问层
 *
 * @author yangch
 * @since 2019-12-16 14:52:16
 */
public interface ExchangeReleaseTaskMapper extends BaseMapper<ExchangeReleaseTask> {
    /**
     * 更新任务状态
     *
     * @param id     ID
     * @param oldVal 更新前的值
     * @param newVal 更新后的值
     * @return
     */
    int updateReleaseTaskStatus(@Param("id") long id,
                                @Param("oldVal") ReleaseTaskStatus oldVal,
                                @Param("newVal") ReleaseTaskStatus newVal);
}