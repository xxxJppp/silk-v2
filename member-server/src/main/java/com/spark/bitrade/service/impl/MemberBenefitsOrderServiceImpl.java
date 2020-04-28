package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.entity.MemberBenefitsOrder;
import com.spark.bitrade.mapper.MemberBenefitsOrderMapper;
import com.spark.bitrade.param.PageParam;
import com.spark.bitrade.service.MemberBenefitsOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 会员申请订单 服务实现类
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-18
 */
@Service
public class MemberBenefitsOrderServiceImpl extends ServiceImpl<MemberBenefitsOrderMapper, MemberBenefitsOrder> implements MemberBenefitsOrderService {

    @Override
    public IPage<MemberBenefitsOrder> findMemberBenefitsOrdersList(Long memberId, PageParam param) {
        QueryWrapper<MemberBenefitsOrder> queryWrapper = new QueryWrapper();
        queryWrapper.eq(MemberBenefitsOrder.MEMBER_EXTEND_ID, memberId).orderByDesc(MemberBenefitsOrder.CREATE_TIME);
        return this.baseMapper.selectPage(new Page<>(param.getPage(), param.getPageSize()), queryWrapper);
    }

    @Override
    public MemberBenefitsOrder findMemberBenefitsOrderByOrderNumber(String orderNum) {
        QueryWrapper<MemberBenefitsOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MemberBenefitsOrder::getOrderNumber, orderNum);
        return this.baseMapper.selectOne(queryWrapper);
    }

    @Override
    public List<MemberBenefitsOrder>  findBenefitsOrderByMemberIdAndOperate(Long extendId) {
        QueryWrapper<MemberBenefitsOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MemberBenefitsOrder::getMemberExtendId, extendId)
                .eq(MemberBenefitsOrder::getOperateType, 30)
                .eq(MemberBenefitsOrder::getPayType, 20)
                .orderByDesc(MemberBenefitsOrder::getCreateTime);
        return this.baseMapper.selectList(queryWrapper);
    }
}
