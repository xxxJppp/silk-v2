package com.spark.bitrade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.entity.MemberBenefitsOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.param.PageParam;

import java.util.List;

/**
 * <p>
 * 会员申请订单 服务类
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-18
 */
public interface MemberBenefitsOrderService extends IService<MemberBenefitsOrder> {

    /**
     * 获取用户订单列表
     *
     * @return
     */
    IPage<MemberBenefitsOrder> findMemberBenefitsOrdersList(Long memberId, PageParam param);


    MemberBenefitsOrder findMemberBenefitsOrderByOrderNumber(String orderNum);


    List<MemberBenefitsOrder> findBenefitsOrderByMemberIdAndOperate(Long extendId);
}
