package com.spark.bitrade.biz;

import com.spark.bitrade.vo.RequireConditionVo;

import java.util.List;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.20 09:53
 */
public interface IRequireConditionService {

    /**
     * 获取会员申请条件列表
     *
     * @return
     */
    List<RequireConditionVo> getRequireConditionVoList();


    /**
     * 更新缓存
     * @return
     */
    Boolean updateRequireConditionCache();


//    RequireConditionVo findRequireConditionByLevelId(Integer levelId);
}
