package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constant.SlpStatus;
import com.spark.bitrade.entity.LockSlpReleasePlan;
import com.spark.bitrade.mapper.LockSlpReleasePlanMapper;
import com.spark.bitrade.service.LockSlpReleasePlanService;
import com.spark.bitrade.vo.LockSlpMemberRecordDetailVo;
import com.spark.bitrade.vo.LockSlpMemberRecordVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

/**
 * 本金返还计划表(LockSlpReleasePlan)表服务实现类
 *
 * @author yangch
 * @since 2019-06-18 21:27:03
 */
@Service("lockSlpReleasePlanService")
public class LockSlpReleasePlanServiceImpl extends ServiceImpl<LockSlpReleasePlanMapper, LockSlpReleasePlan> implements LockSlpReleasePlanService {

    @Autowired
    private LockSlpReleasePlanMapper lockSlpReleasePlanMapper;


    /**
     * 获取用户参与记录汇总
     *
     * @param memberId 会员ID
     * @return 用户参与记录
     */
    @Override
    public LockSlpMemberRecordVo findMemberRecordAnlys(Long memberId) {
        // 待释放数量
        BigDecimal boReleasedAmount = lockSlpReleasePlanMapper.findTotalToBoReleasedByMemberId(memberId);
        if (boReleasedAmount == null) {
            boReleasedAmount = BigDecimal.ZERO;
        }
        // 已释放数量 分加速释放和正常释放
        // 正常释放slp
        BigDecimal normalReleasedAmount = lockSlpReleasePlanMapper.findNormalReleasedAmount(memberId);
        if (normalReleasedAmount == null) {
            normalReleasedAmount = BigDecimal.ZERO;
        }
        // 加速释放slp
        BigDecimal fastReleasedAmount = lockSlpReleasePlanMapper.findFastResleasedAmount(memberId);
        if (fastReleasedAmount == null) {
            fastReleasedAmount = BigDecimal.ZERO;
        }
        LockSlpMemberRecordVo lockSlpMemberRecordVo = LockSlpMemberRecordVo.builder()
                .toBeReleasedAmount(boReleasedAmount)
                .hasBeenReleasedAmount(normalReleasedAmount.add(fastReleasedAmount)).build();
        return lockSlpMemberRecordVo;
    }


    /**
     * 获取用户参与记录行明细
     *
     * @param memberId 会员ID
     * @return 用户参与记录
     */
    @Override
    public IPage<LockSlpMemberRecordDetailVo> findMemberRecordsLine(Long memberId, int current, int size) {
        IPage<LockSlpMemberRecordDetailVo> page = new Page<>(current, size);
        List<LockSlpMemberRecordDetailVo> lockSlpMemberRecordDetailVoList =
                lockSlpReleasePlanMapper.findMemberRecordDetails(memberId, page);
        page.setRecords(lockSlpMemberRecordDetailVoList);
        return page;
    }


    @Override
    public List<LockSlpReleasePlan> getLatestReleaseAt(Long releaseAt) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(releaseAt);

        // 进行中的，释放时间小于目标时间的
        QueryWrapper<LockSlpReleasePlan> query = new QueryWrapper<>();
        query.eq("status", SlpStatus.NOT_PROCESSED).lt("release_current_date", instance.getTime());

        return list(query);
    }

    /**
     * 获取锁仓记录所需的字段，包括：理财套餐、锁仓数量、锁仓时间
     *
     * @param id 本金返回计划ID
     * @return 锁仓记录所需的字段，包括：理财套餐、锁仓数量、锁仓时间
     */
    @Override
    public LockSlpReleasePlan getLockSlpReleasePlanById(Long id) {
        return baseMapper.selectById(id);
    }
}