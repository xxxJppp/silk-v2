package com.spark.bitrade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.SupportStreamRecords;
import com.spark.bitrade.param.StreamSearchParam;
import com.spark.bitrade.vo.CoinMatchVo;
import com.spark.bitrade.vo.SupportStreamSwitchVo;

import java.util.List;

/**
 * <p>
 * 引流开关记录 服务类
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
public interface SupportStreamRecordsService extends IService<SupportStreamRecords> {

    List<SupportStreamSwitchVo> findStreamSwitchRecord(Long id, StreamSearchParam param, IPage page);

    List<CoinMatchVo> findCoinMatch(String coin);
}
