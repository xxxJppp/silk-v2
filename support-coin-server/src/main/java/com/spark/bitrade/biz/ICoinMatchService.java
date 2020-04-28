package com.spark.bitrade.biz;

import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.SupportPayRecords;
import com.spark.bitrade.entity.SupportUpCoinApply;
import com.spark.bitrade.form.CoinMathForm;
import com.spark.bitrade.param.CoinMatchParam;
import com.spark.bitrade.vo.CoinMatchVo;

import java.util.List;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.05 17:23
 */
public interface ICoinMatchService {

    /**
     * 查询指定币的交易对信息
     *
     * @param upCoinId 币id
     * @return
     */
    List<CoinMatchVo> findCoinMatchVoList(Long upCoinId);

    /**
     * 获取项目方交易对
     *
     * @param memberId
     * @return
     */
    CoinMatchVo findCoinMacthesList(Long memberId, CoinMatchParam pageParam);

    /**
     * 新增交易对
     *
     * @return 主币币种名称
     */
    SupportPayRecords addCoinMatch(Member member, CoinMathForm coinMathForm, SupportUpCoinApply apply);

}
