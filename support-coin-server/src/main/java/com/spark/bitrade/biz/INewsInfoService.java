package com.spark.bitrade.biz;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.entity.SupportNewsInfo;
import com.spark.bitrade.form.SupportNewsInfoForm;
import com.spark.bitrade.param.NewInfoParam;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.05 16:16
 */
public interface INewsInfoService {

    /**
     * 查询资讯列表
     * @param memberId
     * @param pageParam
     * @return
     */
    IPage<SupportNewsInfo> findNewsInfosList(Long memberId, NewInfoParam pageParam);

    /**
     * 添加资讯
     * @param newsInfoForm
     */
    String saveNewsInfos(SupportNewsInfoForm newsInfoForm);
}
