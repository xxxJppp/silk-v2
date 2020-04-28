package com.spark.bitrade.service;

import com.spark.bitrade.entity.MemberRequireCondition;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 会员申请条件 服务类
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-18
 */
public interface MemberRequireConditionService extends IService<MemberRequireCondition> {

    /**
     * 根据vip等级获取会员申请条件
     *
     * @param levelId
     * @return
     */
    List<MemberRequireCondition> getRequireConditionBylevelId(Integer levelId);

    /**
     * 更新缓存
     * @return
     */
    boolean updateCache();

}
