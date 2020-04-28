package com.spark.bitrade.biz;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.entity.SupportPayRecords;
import com.spark.bitrade.entity.SupportStreamRecords;
import com.spark.bitrade.param.StreamSearchParam;
import com.spark.bitrade.vo.SupportStreamSwitchVo;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.11.06 14:08  
 */
public interface IStreamSwitchService {
    /**
     * 构造 打开引流开关记录
     * @param memberId
     * @param remark
     * @param upCoinId
     * @return
     */
    SupportStreamRecords generateStreamRecord(Long memberId, String remark, Long upCoinId);


    void doSaveStreamSwitchApply(SupportStreamRecords streamRecords, SupportPayRecords payRecords);


    /**
     * 查询 打开引流开关记录
     * @param memberId
     * @param param
     * @return
     */
    IPage<SupportStreamSwitchVo> findStreamSwitchRecord(Long memberId, StreamSearchParam param);
}
