package com.spark.bitrade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.controller.param.ListBullParam;
import com.spark.bitrade.controller.param.ListLuckyNumberParam;
import com.spark.bitrade.controller.vo.LuckyNumberListVo;
import com.spark.bitrade.controller.vo.LuckyRunBullListVo;
import com.spark.bitrade.entity.LuckyManageCoin;
import com.spark.bitrade.entity.LuckyNumberManager;
import com.spark.bitrade.util.MessageRespResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 欢乐幸运号活动信息表 服务类
 * </p>
 *
 * @author qiliao
 * @since 2019-12-16
 */
public interface LuckyNumberManagerService extends IService<LuckyNumberManager> {

    List<LuckyRunBullListVo> listBulls(ListBullParam param, IPage page,List<Long> memberActIds);

    MessageRespResult<IPage<LuckyNumberListVo>> numberGameList(ListLuckyNumberParam param);
    
    MessageRespResult<LuckyNumberListVo> numberGameInfo(ListLuckyNumberParam param);
    
    MessageRespResult<String> luckyGameSchedule();

    LuckyRunBullListVo detailBull(Long actId);

    List<Long> findMemberActIds(@Param("memberId") Long memberId);

    LuckyNumberManager findLastSettleLucky(Long memberId);

    List<LuckyManageCoin> findRealCoinBulls(Long actId);

    void sortBulls(List<LuckyManageCoin> bulls);
}
