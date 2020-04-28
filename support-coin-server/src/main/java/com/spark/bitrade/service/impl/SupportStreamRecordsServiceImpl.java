package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.entity.SupportStreamRecords;
import com.spark.bitrade.mapper.SupportStreamRecordsMapper;
import com.spark.bitrade.param.StreamSearchParam;
import com.spark.bitrade.service.SupportStreamRecordsService;
import com.spark.bitrade.vo.CoinMatchVo;
import com.spark.bitrade.vo.SupportStreamSwitchVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 引流开关记录 服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
@Service
public class SupportStreamRecordsServiceImpl extends ServiceImpl<SupportStreamRecordsMapper, SupportStreamRecords> implements SupportStreamRecordsService {

    @Override
    public List<SupportStreamSwitchVo> findStreamSwitchRecord(Long memberId, StreamSearchParam param, IPage page) {
        param.transTime();
        return baseMapper.findStreamSwitchRecord(memberId,param ,page );
    }

    @Override
    public List<CoinMatchVo> findCoinMatch(String coin) {
        return baseMapper.findCoinMatch(coin);
    }
}
