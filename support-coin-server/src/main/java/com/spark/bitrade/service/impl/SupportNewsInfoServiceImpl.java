package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constant.AuditStatusEnum;
import com.spark.bitrade.entity.SupportNewsInfo;
import com.spark.bitrade.mapper.SupportNewsInfoMapper;
import com.spark.bitrade.param.NewInfoParam;
import com.spark.bitrade.service.SupportNewsInfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 扶持上币咨询信息 服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
@Service
public class SupportNewsInfoServiceImpl extends ServiceImpl<SupportNewsInfoMapper, SupportNewsInfo> implements SupportNewsInfoService {

    @Autowired
    private SupportNewsInfoMapper newsInfoMapper;

    @Override
    public IPage<SupportNewsInfo> findListBymemberIdAndupCoinId(Long memberId, Long upCoinId, NewInfoParam seacthParam) {
        seacthParam.transTime();
        Page<SupportNewsInfo> supportNewsInfoPage = new Page<>(seacthParam.getPage(), seacthParam.getPageSize());
        List<SupportNewsInfo> list = newsInfoMapper.findListBymemberIdAndupCoinId(memberId, upCoinId, seacthParam, supportNewsInfoPage);
        supportNewsInfoPage.setRecords(list);
        return supportNewsInfoPage;
    }

    @Override
    public SupportNewsInfo findByAuditStatus(Long memberId, Long upCoinId) {
        QueryWrapper<SupportNewsInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SupportNewsInfo.MEMBER_ID, memberId);
        queryWrapper.eq(SupportNewsInfo.UP_COIN_ID, upCoinId);
        queryWrapper.eq(SupportNewsInfo.AUDIT_STATUS, AuditStatusEnum.PENDING);
        List<SupportNewsInfo> supportNewsInfos = newsInfoMapper.selectList(queryWrapper);
        if(CollectionUtils.isEmpty(supportNewsInfos)){
            return null;
        }
        return supportNewsInfos.get(0);
    }

    @Override
    public SupportNewsInfo findByGroupId(String groupId) {
        QueryWrapper<SupportNewsInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SupportNewsInfo::getGroupId, groupId);
        List<SupportNewsInfo> list = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }
}
