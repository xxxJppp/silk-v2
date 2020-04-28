package com.spark.bitrade.biz;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.param.HoldCountParam;
import com.spark.bitrade.vo.HoldPositionVo;
import com.spark.bitrade.vo.MemberWalletCountVo;
import com.spark.bitrade.vo.MembertVo;

import java.math.BigDecimal;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.07 11:43
 */
public interface IHoldPositionService {

    /**
     * 项目方持仓统计
     *
     * @param memberId
     * @return
     */
    HoldPositionVo countMemberWallet(Long memberId, HoldCountParam param);
    /**
     * 有效用户持仓币数
     */
    BigDecimal validHoldCoinCount(String coinUnit);
}
