package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.spark.bitrade.entity.NewYearMemberInfo;
import com.spark.bitrade.mapper.NewYearMemberInfoMapper;
import com.spark.bitrade.service.NewYearMemberInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户矿石表 服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2019-12-30
 */
@Service
public class NewYearMemberInfoServiceImpl extends ServiceImpl<NewYearMemberInfoMapper, NewYearMemberInfo> implements NewYearMemberInfoService {

    @Override
    public NewYearMemberInfo findRecordByMemberId(Long memberId) {
        QueryWrapper<NewYearMemberInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(NewYearMemberInfo::getMemberId, memberId);
        return this.baseMapper.selectOne(queryWrapper);
    }

    @Override
    public boolean decrMemberMiningNumber(Long memberId) {
        try {
            this.baseMapper.decrMemberInfo(memberId);
            return true;
        } catch (Exception e) {
            return  false;
        }
    }

    @Override
    public boolean incrMemberMiningNumber(Long memberId) {
        try {
            this.baseMapper.incrMemberInfo(memberId);
            return true;
        } catch (Exception e) {
            return  false;
        }
    }
}
