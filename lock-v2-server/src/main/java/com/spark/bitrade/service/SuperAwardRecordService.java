package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.dto.SuperAwardDto;
import com.spark.bitrade.entity.SuperAwardRecord;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author qiliao
 * @since 2019-07-16
 */
public interface SuperAwardRecordService extends IService<SuperAwardRecord> {

    /**
     * 根据日期查询要返的手续费奖励
     * @param dateStr
     * @return
     */
    List<SuperAwardDto> feeAwardByDay(String dateStr);
    /**
     * 查询需要奖励的手续费
     * @return
     */
    List<SuperAwardDto> feeAward();

    /**
     * 执行奖励操作
     * @param superAwardDtos
     */
    void excuteFeeAward(List<SuperAwardDto> superAwardDtos);

    /**
     * 统计用户每月币币交易
     *
     */
    List<SuperAwardDto> memberActiveAward();

    /**
     * 计算60万活跃用户
     * @param superAwardDtos
     */
    void excuteBBExchange(List<SuperAwardDto> superAwardDtos);


    void runFeeCaluate();

    void runBBExchange();

    void runFeeCaluateByDay(String dateStr);

}
