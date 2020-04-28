package com.spark.bitrade.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.constant.LockStatus;
import com.spark.bitrade.constant.LockType;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.entity.LockCoinActivitieSetting;
import com.spark.bitrade.entity.LockCoinDetail;
import com.spark.bitrade.entity.Member;

/**
 * (LockCoinDetail)表服务接口
 *
 * @author zhangYanjun
 * @since 2019-06-19 15:57:04
 */
public interface LockCoinDetailService extends IService<LockCoinDetail> {

    /**
     * 锁仓操作
     *
     * @param member                   用户信息
     * @param lockCoinActivitieSetting 活动配置信息
     * @param activityCoinLockPrice    锁仓价格
     * @param usdt2CnyPrice            usdt对cny的价格
     * @param amount                   活动参与份数
     * @param boughtAmount             活动参与数量（一般和amount是一样的，如活动币种和参与活动币种不一样时，可能就不一样）
     * @param lockType                 锁仓类型
     * @return
     */
    LockCoinDetail lockCoin(Member member,
                            LockCoinActivitieSetting lockCoinActivitieSetting,
                            BigDecimal amount,
                            BigDecimal boughtAmount,
                            BigDecimal activityCoinLockPrice,
                            BigDecimal usdt2CnyPrice,
                            LockType lockType);

    /**
     * 根据id修改返佣状态(未返佣-》已返佣)
     *
     * @param id
     * @return boolean
     * @author zhangYanjun
     * @time 2019.06.21 17:47
     */
    boolean updateRewardStatusToCompleteById(Long id);

    /**
     * 根据id修改状态
     *
     * @param id
     * @param oldStatus
     * @param newStatus
     * @return boolean
     * @author zhangYanjun
     * @time 2019.07.18 17:14
     */
    boolean updateStatusTById(Long id, LockStatus oldStatus, LockStatus newStatus);

    /**
     * 根据id修改状态
     *
     * @param id
     * @param oldStatus
     * @param newStatus
     * @return boolean
     * @author yangch
     * @time 2019.07.18 17:14
     */
    boolean updateStatusTById(Long id, LockStatus oldStatus, LockStatus newStatus, String remark);

    /**
     * 当前有效的活动数量
     *
     * @param memberId 用户ID
     * @param lockType 活动类型
     * @return
     */
    Integer countValidLockCoinDetail(Long memberId, LockType lockType);

    /**
     * 当日参与活动的次数
     *
     * @param memberId 用户ID
     * @param lockType 活动类型
     * @return
     */
    Integer countLockCoinDetailInDay(Long memberId, LockType lockType);

    /**
     * 用户参加锁仓活动数据校验
     *
     * @param lockType        活动类型
     * @param id              活动配置id
     * @param amount          购买数量
     * @param payAmount       支付币数，如支付币种和活动币种一样可用为 购买数量
     * @param jyPassword      可选，资金密码
     * @param limitCountValid 可选，当前有效的活动数量限制
     * @param limitCountInDay 可选，当日参与活动的次数限制（不考虑是否有效）
     * @return 校验成功返回活动信息
     * @author yangch
     * @time 2019-06-20 17:04:49
     */
    LockCoinActivitieSetting lockVerify(Member member, LockType lockType, Long id,
                                        BigDecimal amount, BigDecimal payAmount, String jyPassword,
                                        Integer limitCountValid, Integer limitCountInDay);

    /**
     * 查询锁仓记录
     *
     * @param memberId   会员ID
     * @param lockType   锁仓类型
     * @param lockStatus 锁仓状态
     * @return
     */
    List<LockCoinDetail> list(Long memberId, LockType lockType, LockStatus lockStatus);

    /**
     * 获取单条锁仓记录
     *
     * @param memberId       会员ID
     * @param lockType       锁仓类型
     * @param lockStatus     锁仓状态
     * @param refActivitieId 关联锁仓活动ID
     * @return
     */
    LockCoinDetail getLockCoinDetailByMemberIdAndTypeAndStatus(Long memberId, LockType lockType, LockStatus lockStatus, Long refActivitieId);
    
    
    /**
     * 简单锁仓实现
     * @param member 会员
     * @param lockType 锁仓类型
     * @param txsType 交易类型
     * @param amount 锁仓数量
     * @param unit 锁仓币种
     * @param lockDay 锁仓天数
     * @return
     */
    LockCoinDetail simplelock(Member member,LockType lockType, TransactionType txsType,BigDecimal amount,String unit,int lockDay, Long lockId, Long operType);
}