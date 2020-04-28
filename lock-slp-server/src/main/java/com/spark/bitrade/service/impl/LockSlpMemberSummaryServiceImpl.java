package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constant.SlpReleaseType;
import com.spark.bitrade.constant.SlpStatus;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.dto.SlpMemberSummaryCountDto;
import com.spark.bitrade.dto.SlpMemberSummaryUpdateDto;
import com.spark.bitrade.entity.*;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.job.util.FuncWrapUtil;
import com.spark.bitrade.mapper.LockSlpMemberSummaryMapper;
import com.spark.bitrade.service.LockSlpMemberSummaryService;
import com.spark.bitrade.service.LockSlpReleaseLevelConfigService;
import com.spark.bitrade.service.LockSlpReleaseTaskRecordService;
import com.spark.bitrade.service.LockSlpUpdateTaskService;
import com.spark.bitrade.vo.LockSlpCurrentNodeVo;
import com.spark.bitrade.vo.LockSlpMemberSummaryVo;
import com.spark.bitrade.vo.LockSummationVo;
import com.spark.bitrade.vo.PromotionMemberExtensionVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 会员社区奖励实时统计表(LockSlpMemberSummary)表服务实现类
 *
 * @author yangch
 * @since 2019-06-18 21:27:03
 */
@Slf4j
@Service("lockSlpMemberSummaryService")
public class LockSlpMemberSummaryServiceImpl extends ServiceImpl<LockSlpMemberSummaryMapper, LockSlpMemberSummary>
        implements LockSlpMemberSummaryService {

    private LockSlpReleaseLevelConfigService levelConfigService;
    private LockSlpReleaseTaskRecordService lockSlpReleaseTaskRecordService;
    private LockSlpUpdateTaskService lockSlpUpdateTaskService;

    @Autowired
    public void setLevelConfigService(LockSlpReleaseLevelConfigService levelConfigService) {
        this.levelConfigService = levelConfigService;
    }

    @Autowired
    public void setLockSlpReleaseTaskRecordService(LockSlpReleaseTaskRecordService lockSlpReleaseTaskRecordService) {
        this.lockSlpReleaseTaskRecordService = lockSlpReleaseTaskRecordService;
    }

    @Autowired
    public void setLockSlpUpdateTaskService(LockSlpUpdateTaskService lockSlpUpdateTaskService) {
        this.lockSlpUpdateTaskService = lockSlpUpdateTaskService;
    }

    @Override
    @Transactional
    public LockSlpMemberSummary initAndGet(LockSlpUpdateTask task, LockSlpReleasePlan plan) {
        String pk = task.getMemberId() + task.getCoinUnit();

        LockSlpMemberSummary summary = getById(pk);
        log.info("获取 [ pk = {}, task_id = {}, plan_id = {} ]", pk, task.getId(), plan.getId());
        if (summary != null) {
            // 二次锁仓
            if (plan.getMemberId() == task.getMemberId().intValue()
                    && task.getDeep() == 1 && task.getStatus() == SlpStatus.NOT_PROCESSED) {
                // 最大最小一致
                BigDecimal lockAmount = plan.getLockAmount();
                BigDecimal realAmount = plan.getRealAmount();
                BigDecimal releaseRate = plan.getReleaseRate();
                BigDecimal remainAmount = realAmount.multiply(plan.getZoomScale());

                // UpdateWrapper<LockSlpMemberSummary>  uw = new UpdateWrapper<>();
                // uw.eq("id", pk);
                // uw.set("max_lock_amount", lockAmount).set("max_release_rate", releaseRate);
                // uw.set("min_lock_amount", lockAmount).set("min_release_rate", releaseRate);
                Date now = Calendar.getInstance().getTime();

                SlpMemberSummaryUpdateDto dto = new SlpMemberSummaryUpdateDto(pk, lockAmount, realAmount, releaseRate, remainAmount, now);

                this.baseMapper.updateBySummaryDto(dto);
                // reload
                summary = getById(pk);
            }

            // 比较推荐人是否一致
            if (!FuncWrapUtil.isEqual(summary.getInviterId(), task.getInviterId())) {
                this.baseMapper.updateInviterId(pk, FuncWrapUtil.orElse(task.getInviterId(), new Long("0")));
            }

            // 更新下级数据
            return countAndFillSubSummary(summary);
        }

        summary = new LockSlpMemberSummary();
        summary.setId(pk);
        summary.setMemberId(task.getMemberId());
        summary.setCoinUnit(task.getCoinUnit());

        // 等级配置
        LockSlpReleaseLevelConfig defaultLevelConfig = levelConfigService.getDefaultLevelConfig(task.getCoinUnit());
        if (defaultLevelConfig != null) {
            summary.setCurrentLevelId(defaultLevelConfig.getLevelId().longValue());
            summary.setCurrentLevelName(defaultLevelConfig.getLevelName());
            summary.setReleaseRate(defaultLevelConfig.getRelaseRate());
        }

        if (plan.getMemberId() == task.getMemberId().intValue()) {
            // 最大最小一致
            summary.setMaxLockAmount(plan.getLockAmount());
            summary.setMaxReleaseRate(plan.getReleaseRate());
            summary.setMinLockAmount(plan.getLockAmount());
            summary.setMinReleaseRate(plan.getReleaseRate());

            // 锁仓数据
            summary.setTotalValidAmount(plan.getRealAmount());
            summary.setTotalRemainAmount(plan.getRemainAmount());
        } else {
            // 最大最小一致
            fillFieldDefaultValue(summary);
        }


        summary.setTotalSubValidAmount(BigDecimal.ZERO);

        // 推荐人
        summary.setInviterId(task.getInviterId());
        return saveAndFillSubSummary(summary);
    }

    @Override
    @Transactional
    public LockSlpMemberSummary getAndInit(Long memberId, Long inviterId, String coinUnit) {
        String pk = memberId + coinUnit;

        LockSlpMemberSummary summary = getById(pk);

        if (summary != null) {
            return summary;
        }

        summary = new LockSlpMemberSummary();
        summary.setId(pk);
        summary.setMemberId(memberId);
        summary.setCoinUnit(coinUnit);

        // 等级配置
        LockSlpReleaseLevelConfig defaultLevelConfig = levelConfigService.getDefaultLevelConfig(coinUnit);
        if (defaultLevelConfig != null) {
            summary.setCurrentLevelId(defaultLevelConfig.getLevelId().longValue());
            summary.setCurrentLevelName(defaultLevelConfig.getLevelName());
            summary.setReleaseRate(defaultLevelConfig.getRelaseRate());
        }

        // 填充默认值
        fillFieldDefaultValue(summary);


        summary.setTotalSubValidAmount(BigDecimal.ZERO);

        // 推荐人
        summary.setInviterId(inviterId);

        summary.setPromotion(0);
        // createTime, updateTime 框架填充
        summary.setCreateTime(Calendar.getInstance().getTime());

        if (save(summary)) {
            return summary;
        }

        throw new MessageCodeException(CommonMsgCode.FAILURE);
    }


    @Override
    @Transactional
    public void updateLockSlpMemberSummary(LockSlpUpdateTask task, LockSlpReleasePlan plan) {
        LockSlpMemberSummary current = initAndGet(task, plan);

        upgrade(current);
    }

    @Override
    @Transactional
    public void upgrade(LockSlpMemberSummary summary) {
        // 找出所有直接下级
        QueryWrapper<LockSlpMemberSummary> query = new QueryWrapper<>();
        query.eq("inviter_id", summary.getMemberId()).eq("coin_unit", summary.getCoinUnit());

        List<LockSlpMemberSummary> list = list(query);
        SummaryValue summaryValue = new SummaryValue(list);

        // 直推人数查询推荐关系表
        // 恢复有效直推 summaryValue.promotion = baseMapper.countSlpMemberPromotion(summary.getMemberId());

        // 修改条件
        UpdateWrapper<LockSlpMemberSummary> update = new UpdateWrapper<>();
        update.eq("id", summary.getId());
        update.set("promotion", summaryValue.promotion).set("total_sub_valid_amount", summaryValue.amount);

        // 获取所有等级配置信息
        List<LockSlpReleaseLevelConfig> configs = levelConfigService.findByCoinUnit(summary.getCoinUnit());

        // 调整目标等级
        LockSlpReleaseLevelConfig target = getNewTargetLevelConfig(summary.getCurrentLevelId().intValue(), configs, summaryValue);

        update.set("current_level_id", target.getLevelId())
                .set("current_level_name", target.getLevelName())
                .set("release_rate", target.getRelaseRate());

        update.set("update_time", Calendar.getInstance().getTime());

        // 个人数据由其他任务更新

        // 更新数据
        update(update);
    }

    @Override
    public long countCurrentSubLevel(LockSlpMemberSummary summary) {
        // 获取所有等级配置信息
        List<LockSlpReleaseLevelConfig> configs = levelConfigService.findByCoinUnit(summary.getCoinUnit());

        // LockSlpReleaseLevelConfig[] levelConfigs = resolveLevelConfig(configs, summary.getCurrentLevelId().intValue());
        LockSlpReleaseLevelConfig prev = resolveLevelConfig(configs, summary.getCurrentLevelId().intValue()).get(0);

        // 平级
        if (prev.getLevelId() == summary.getCurrentLevelId().intValue()) {
            return 0;
        }

        // 找出所有直接下级
        QueryWrapper<LockSlpMemberSummary> query = new QueryWrapper<>();
        query.eq("inviter_id", summary.getMemberId())
                .eq("coin_unit", summary.getCoinUnit())
                .eq("current_level_id", prev.getLevelId());

        return count(query);
    }

    /**
     * 填充默认值
     *
     * @param summary 实时统计
     */
    private void fillFieldDefaultValue(LockSlpMemberSummary summary) {
        // 最大最小一致
        summary.setMaxLockAmount(BigDecimal.ZERO);
        summary.setMaxReleaseRate(BigDecimal.ZERO);
        summary.setMinLockAmount(BigDecimal.ZERO);
        summary.setMinReleaseRate(BigDecimal.ZERO);

        // 锁仓数据
        summary.setTotalValidAmount(BigDecimal.ZERO);
        summary.setTotalRemainAmount(BigDecimal.ZERO);
    }

    /**
     * 统计并填充下级信息
     *
     * @param summary 统计信息
     * @return summary
     */
    private LockSlpMemberSummary countAndFillSubSummary(LockSlpMemberSummary summary) {
        SlpMemberSummaryCountDto dto = this.baseMapper.countSubSummary(summary.getMemberId(), summary.getCoinUnit());

        // 优化，若无数据无变更则不更新db
        if (dto.getPromotion() == summary.getPromotion().intValue()
                && dto.getTotalSubValidAmount().compareTo(summary.getTotalSubValidAmount()) == 0) {
            return summary;
        }

        summary.setTotalSubValidAmount(dto.getTotalSubValidAmount());
        summary.setPromotion(dto.getPromotion());

        // 更新数据 ?? 是否有必要
        UpdateWrapper<LockSlpMemberSummary> update = new UpdateWrapper<>();
        update.eq("id", summary.getId()).set("promotion", dto.getPromotion()).set("total_sub_valid_amount", dto.getTotalSubValidAmount());
        update.set("update_time", Calendar.getInstance().getTime());

        update(update);

        return summary;
    }

    private LockSlpMemberSummary saveAndFillSubSummary(LockSlpMemberSummary summary) {
        summary.setPromotion(0);
        // createTime, updateTime 框架填充
        summary.setCreateTime(Calendar.getInstance().getTime());

        if (save(summary)) {
            return countAndFillSubSummary(summary);
        }

        throw new MessageCodeException(CommonMsgCode.FAILURE);
    }

    /**
     * 获取目标等级信息
     * <p>
     * 只升不降
     *
     * @param levelId      当前等级ID
     * @param configs      等级配置信息
     * @param summaryValue 汇总统计信息
     * @return level
     */
    private LockSlpReleaseLevelConfig getNewTargetLevelConfig(int levelId, List<LockSlpReleaseLevelConfig> configs, SummaryValue summaryValue) {
        List<LockSlpReleaseLevelConfig> resolvers = resolveLevelConfig(configs, levelId);

        LockSlpReleaseLevelConfig target = resolvers.get(0);
        int size = resolvers.size();
        // 最高等级
        if (size == 1) {
            return target;
        }

        // 顺序检查
        int index = -1;
        while (index++ < size - 2) {
            // 是否可升级
            boolean up_able = achieve(resolvers.get(index), resolvers.get(index + 1), summaryValue);
            if (up_able) {
                target = resolvers.get(index + 1);
            } else { // 当不可升级则中断寻找
                break;
            }

        }
        return target;
    }

    /**
     * 是否达到目标等级
     *
     * @param prev         目标前一个等级
     * @param next         目标等级
     * @param summaryValue 汇总信息
     * @return bool
     */
    private boolean achieve(LockSlpReleaseLevelConfig prev, LockSlpReleaseLevelConfig next,
                            SummaryValue summaryValue) {
        Integer promotionCount = next.getPromotionCount();  // 直推数
        BigDecimal performanceAmount = next.getPerformanceAmount(); // 理财金额
        Integer subLevel = next.getSubLevleCount(); // 伞下节点

        if (promotionCount != null && promotionCount > 0) {
            if (summaryValue.promotion < promotionCount) {
                return false;
            }
        }

        if (performanceAmount != null) {
            if (summaryValue.amount.compareTo(performanceAmount) < 0) { // 业绩不算自己
                return false;
            }
        }

        if (subLevel != null && subLevel > 0) {
            int sub = summaryValue.getSubLevelCount(prev.getLevelId());
            return sub >= subLevel;
        }

        return true;
    }

    /**
     * 找出当前等级及其后面的所有等级信息
     *
     * @param configs 配置
     * @param levelId 当前等级id
     * @return list [ current, next1, next2 ...]
     */
    private List<LockSlpReleaseLevelConfig> resolveLevelConfig(List<LockSlpReleaseLevelConfig> configs, int levelId) {

        List<LockSlpReleaseLevelConfig> next = new ArrayList<>();
        boolean skip = true;
        // 找出后面的等级
        for (LockSlpReleaseLevelConfig config : configs) {
            if (skip) {
                if (config.getLevelId() == levelId) {
                    next.add(config);
                    skip = false;
                }
                continue;
            }
            next.add(config);
        }
        return next;
    }

    class SummaryValue {
        private int promotion = 0;
        private BigDecimal amount = BigDecimal.ZERO;
        private Map<Integer, AtomicInteger> subLevel = new HashMap<>();

        SummaryValue(List<LockSlpMemberSummary> summaries) {
            for (LockSlpMemberSummary summary : summaries) {
                this.promotion = this.promotion + 1; // 直推人人数 summary.getPromotion();
                this.amount = this.amount.add(summary.getTotalSubValidAmount()).add(summary.getTotalValidAmount());
                this.subLevel.computeIfAbsent(summary.getCurrentLevelId().intValue(),
                        k -> new AtomicInteger(0)).incrementAndGet();
            }
        }

        public int getSubLevelCount(Integer subLevelId) {
            AtomicInteger integer = subLevel.get(subLevelId);
            if (integer != null) {
                return integer.get();
            }
            return 0;
        }
    }

    @Override
    public LockSlpMemberSummary findByMemberIdAndUnit(Long memberId, String unit) {
        // QueryWrapper<LockSlpMemberSummary> wrapper = new QueryWrapper<>();
        // wrapper.eq("member_id",memberId).eq("coin_unit",unit);
        // return this.baseMapper.selectOne(wrapper);

        final String pk = memberId + unit;
        return getById(pk);
    }

    /**
     * SLP加速释放页面，查询锁仓汇总明细
     *
     * @param size      显示条数
     * @param current   当前页数
     * @param inviterId 推荐人ID
     * @param startTime 查询起始时间
     * @param endTime   查询结束时间
     * @return SLP加速记录
     */
    @Override
    public IPage<LockSummationVo> listLockSummation(Integer size, Integer current, Long inviterId, Long startTime, Long endTime) {
        Page<LockSlpMemberSummary> lockSlpMemberSummaryPage = new Page<>(current, size);
        QueryWrapper<LockSlpMemberSummary> lockSlpMemberSummaryQueryWrapper = new QueryWrapper<LockSlpMemberSummary>()
                .and(wrapper -> wrapper.eq("inviter_id", inviterId))
                .orderByDesc("update_time");
        if (startTime != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(startTime * 1000);
            lockSlpMemberSummaryQueryWrapper.ge("update_time", calendar.getTime());
        }
        Long queryEndTime = null;
        if (endTime != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(endTime * 1000);
            calendar.add(Calendar.MONTH, 1);
            queryEndTime = calendar.getTimeInMillis() / 1000;
            lockSlpMemberSummaryQueryWrapper.lt("update_time", calendar.getTime());
        }
        IPage<LockSlpMemberSummary> lockSlpMemberSummaryIPage = page(lockSlpMemberSummaryPage, lockSlpMemberSummaryQueryWrapper);
        Page<LockSummationVo> lockSummationVoPage = new Page<>(current, size);
        List<LockSummationVo> lockSummationVoList = new ArrayList<>();
        List<LockSlpMemberSummaryVo> lockSlpMemberSummaryVoList = new ArrayList<>();
        LockSummationVo lockSummationVo = new LockSummationVo();
        log.info("获取锁仓汇总,接收参数:size={},current={},startTime={},endTime={},memberId={},锁仓汇总明细返回{}条记录", size, current, startTime, endTime, inviterId, lockSlpMemberSummaryIPage.getRecords());
        if (lockSlpMemberSummaryIPage.getRecords() != null && lockSlpMemberSummaryIPage.getRecords().size() > 0) {
            lockSlpMemberSummaryIPage.getRecords().forEach(record -> {
                LockSlpMemberSummaryVo lockSlpMemberSummaryVo = new LockSlpMemberSummaryVo();
                lockSlpMemberSummaryVo.setMemberId(record.getMemberId());
                BigDecimal totalValidAmount = record.getTotalValidAmount();
                BigDecimal totalSubValidAmount = record.getTotalSubValidAmount();
                lockSlpMemberSummaryVo.setTotalValidAmount(totalValidAmount);
                lockSlpMemberSummaryVo.setTotalSubValidAmount(totalSubValidAmount);
                lockSlpMemberSummaryVoList.add(lockSlpMemberSummaryVo);
            });
            LockSummationVo sumTotalLock = getBaseMapper().sumTotalLock(inviterId, startTime, queryEndTime);
            log.info("获取锁仓汇总,接收参数:size={},current={},startTime={},endTime={},memberId={},锁仓汇总返回数据:{}", size, current, startTime, endTime, inviterId, sumTotalLock);
            if (!StringUtils.isEmpty(sumTotalLock)) {
                lockSummationVo.setTotalLock(sumTotalLock.getTotalLock());
            }
        } else {
            log.info("获取锁仓汇总,接收参数:size={},current={},startTime={},endTime={},memberId={},查询锁仓汇总为空", size, current, startTime, endTime, inviterId);
            lockSummationVo.setTotalLock(new BigDecimal(0));
        }
        lockSummationVo.setLockSlpMemberSummaryVoList(lockSlpMemberSummaryVoList);
        lockSummationVoList.add(lockSummationVo);
        lockSummationVoPage.setTotal(lockSlpMemberSummaryIPage.getTotal());
        lockSummationVoPage.setPages(lockSlpMemberSummaryIPage.getPages());
        lockSummationVoPage.setRecords(lockSummationVoList);
        return lockSummationVoPage;
    }

    /**
     * 获取用户当前节点以及分类释放汇总
     *
     * @param memberId  会员ID
     * @param startTime 查询起始时间
     * @param endTime   查询结束时间
     * @return 用户当前节点以及分类释放汇总
     */
    @Override
    public LockSlpCurrentNodeVo findCurrentNode(Long memberId, Long startTime, Long endTime) {
        log.info("获取用户当前节点以及分类释放汇总,接收参数memberId={},startTime={},endTime={}", memberId, startTime, endTime);
        LockSlpCurrentNodeVo lockSlpCurrentNodeVo = new LockSlpCurrentNodeVo();
        // 当前社区节点
        QueryWrapper<LockSlpMemberSummary> lockSlpMemberSummaryQueryWrapper = new QueryWrapper<>();
        lockSlpMemberSummaryQueryWrapper.eq("member_id", memberId);
        LockSlpMemberSummary lockSlpMemberSummary = baseMapper.selectOne(lockSlpMemberSummaryQueryWrapper);
        if (!StringUtils.isEmpty(lockSlpMemberSummary)) {
            lockSlpCurrentNodeVo.setCurrentLevelName(lockSlpMemberSummary.getCurrentLevelName());
        } else {
            lockSlpCurrentNodeVo.setCurrentLevelName("");
        }
        // 分类释放汇总
        QueryWrapper<LockSlpReleaseTaskRecord> lockSlpReleaseTaskRecordQueryWrapper = new QueryWrapper<>();
        lockSlpReleaseTaskRecordQueryWrapper.select("sum(release_in_amount) as releaseInAmount,type");
        lockSlpReleaseTaskRecordQueryWrapper.eq("member_id", memberId);
        if (startTime != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(startTime * 1000);
            lockSlpReleaseTaskRecordQueryWrapper.ge("create_time", calendar.getTime());
        }
        if (endTime != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(endTime * 1000);
            calendar.add(Calendar.MONTH, 1);
            lockSlpReleaseTaskRecordQueryWrapper.lt("create_time", calendar.getTime());
        }
        lockSlpReleaseTaskRecordQueryWrapper.groupBy("type");
        List<LockSlpReleaseTaskRecord> records = lockSlpReleaseTaskRecordService.list(lockSlpReleaseTaskRecordQueryWrapper);
        if (!CollectionUtils.isEmpty(records)) {
            for (LockSlpReleaseTaskRecord r : records) {
                SlpReleaseType type = r.getType();
                switch (type) {
                    case RELEASE_SUN:
                        lockSlpCurrentNodeVo.setSunReleasedReward(r.getReleaseInAmount());
                        continue;
                    case RELEASE_PEERS:
                        lockSlpCurrentNodeVo.setCommunityReleasedReward(lockSlpCurrentNodeVo.getCommunityReleasedReward().add(r.getReleaseInAmount()));
                        continue;
                    case RELEASE_CROSS:
                        lockSlpCurrentNodeVo.setCommunityReleasedReward(lockSlpCurrentNodeVo.getCommunityReleasedReward().add(r.getReleaseInAmount()));
                        continue;
                    case RELEASE_INVITE:
                        lockSlpCurrentNodeVo.setDirectReleasedReward(lockSlpCurrentNodeVo.getDirectReleasedReward().add(r.getReleaseInAmount()));
                    default:
                        continue;
                }
            }
        }
        return lockSlpCurrentNodeVo;
    }

    /**
     * 获取会员社区节点信息
     *
     * @param memberId 会员ID
     * @return 会员社区节点信息
     */
    @Override
    public PromotionMemberExtensionVo findCurrentLevelName(Long memberId) {
        PromotionMemberExtensionVo promotionMemberExtensionVo = new PromotionMemberExtensionVo();
        QueryWrapper<LockSlpMemberSummary> lockSlpMemberSummaryQueryWrapper = new QueryWrapper<>();
        lockSlpMemberSummaryQueryWrapper.eq("member_id", memberId);
        LockSlpMemberSummary lockSlpMemberSummary = getBaseMapper().selectOne(lockSlpMemberSummaryQueryWrapper);
        log.info("获取会员社区节点信息,接收参数:memberId={},返回数据:{}", memberId, lockSlpMemberSummary);
        String currentLevelName = "";
        int status = 0;
        if (!StringUtils.isEmpty(lockSlpMemberSummary)) {
            currentLevelName = lockSlpMemberSummary.getCurrentLevelName();
            status = 1;
        }
        promotionMemberExtensionVo.setStatus(status);
        promotionMemberExtensionVo.setLevelName(currentLevelName);
        return promotionMemberExtensionVo;
    }

    /**
     * 获取当前会员或者伞下会员，是否存在锁仓记录
     *
     * @param memberId 会员ID集合
     * @return 有效总人数
     */
    @Override
    public int countEffectiveTotal(Long memberId) {
        return getBaseMapper().countEffectiveTotal(memberId);
    }

}