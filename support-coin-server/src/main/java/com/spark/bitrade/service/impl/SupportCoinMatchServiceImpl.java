package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constant.AuditStatusEnum;
import com.spark.bitrade.entity.SupportCoinMatch;
import com.spark.bitrade.mapper.SupportCoinMatchMapper;
import com.spark.bitrade.param.CoinMatchParam;
import com.spark.bitrade.service.SupportCoinMatchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 扶持上币交易对  服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
@Service
public class SupportCoinMatchServiceImpl extends ServiceImpl<SupportCoinMatchMapper, SupportCoinMatch> implements SupportCoinMatchService {

    @Resource
    private SupportCoinMatchMapper coinMatchMapper;

    @Override
    public List<SupportCoinMatch> findByUpCoinId(Long upCoinId) {
        QueryWrapper<SupportCoinMatch> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SupportCoinMatch.UP_COIN_ID, upCoinId);
        queryWrapper.eq(SupportCoinMatch.AUDIT_STATUS, AuditStatusEnum.APPROVED);
        return coinMatchMapper.selectList(queryWrapper);
    }

    @Override
    public IPage<SupportCoinMatch> findByUpCoinIdAndMemberId(Long memberId, Long upCoinId, CoinMatchParam param) {
        QueryWrapper<SupportCoinMatch> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SupportCoinMatch.MEMBER_ID, memberId);
        queryWrapper.eq(SupportCoinMatch.UP_COIN_ID, upCoinId);
        if (StringUtils.isNotBlank(param.getTargetCoin())) {
            queryWrapper.like(SupportCoinMatch.TARGET_COIN, param.getTargetCoin());
        }

        queryWrapper.eq(param.getAuditStatus()!=null,SupportCoinMatch.AUDIT_STATUS, param.getAuditStatus());

        if (StringUtils.isNotBlank(param.getStartTime())) {
            queryWrapper.ge(SupportCoinMatch.CREATE_TIME, param.getStartTime());
        }
        if (StringUtils.isNotBlank(param.getEndTime())) {
            queryWrapper.le(SupportCoinMatch.CREATE_TIME, param.getEndTime());
        }
        queryWrapper.orderByDesc(SupportCoinMatch.CREATE_TIME);
        return coinMatchMapper.selectPage(new Page<SupportCoinMatch>(param.getPage(), param.getPageSize()), queryWrapper);
    }

    @Override
    public SupportCoinMatch findByAuditStauts(Long memberId, Long upCoinId) {
        QueryWrapper<SupportCoinMatch> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SupportCoinMatch.UP_COIN_ID, upCoinId);
        queryWrapper.eq(SupportCoinMatch.AUDIT_STATUS, AuditStatusEnum.PENDING);
        queryWrapper.eq(SupportCoinMatch.MEMBER_ID, memberId);
        return coinMatchMapper.selectOne(queryWrapper);
    }

    @Override
    public List<SupportCoinMatch> findByMatch(Long memberId, String match) {
        QueryWrapper<SupportCoinMatch> sc=new QueryWrapper<>();
        sc.lambda().eq(SupportCoinMatch::getMemberId,memberId)
                .in(SupportCoinMatch::getAuditStatus,AuditStatusEnum.APPROVED,AuditStatusEnum.PENDING)
                .eq(SupportCoinMatch::getTargetCoin,match);

        return this.list(sc);
    }

    @Override
    public List<String> findByCoinUnit(String coin) {
        return baseMapper.findByCoinUnit(coin);
    }
}
