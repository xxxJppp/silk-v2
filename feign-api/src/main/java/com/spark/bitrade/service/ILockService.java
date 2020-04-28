package com.spark.bitrade.service;

import com.spark.bitrade.constant.FeignServiceConstant;
import com.spark.bitrade.constant.LockStatus;
import com.spark.bitrade.constant.LockType;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.entity.LockCoinActivitieSetting;
import com.spark.bitrade.entity.LockCoinDetail;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

/**
 * 提供Lock API服务
 *
 * @author zhangYanjun
 * @time 2019.06.20 11:35
 */
@FeignClient(FeignServiceConstant.LOCK_SERVER2)
public interface ILockService {

    /**
     * 根据id获取锁仓详情
     *
     * @param id
     * @return LockCoinDetail
     * @author zhangYanjun
     * @time 2019.06.20 11:45
     **/
    @PostMapping(value = "/lock2/v2/lockCoinDetail/get")
    MessageRespResult<LockCoinDetail> findLockCoinDetailById(@RequestParam(value = "id") Long id);


    /**
     * 用户参加锁仓活动数据校验
     *
     * @param lockType        活动类型
     * @param id              活动配置id
     * @param amount          购买数量
     * @param jyPassword      可选，资金密码
     * @param limitCountValid 可选，当前有效的活动数量限制
     * @param limitCountInDay 可选，当日参与活动的次数限制（不考虑是否有效）
     * @return 校验成功返回活动信息
     * @author yangch
     * @time 2019-06-20 17:04:49
     */
    @PostMapping("/lock2/v2/lock/lockVerify")
    MessageRespResult<LockCoinActivitieSetting> lockVerify(
            @RequestHeader("apiKey") String apiKey,
            @RequestParam(value = "lockType") LockType lockType,
            @RequestParam(value = "id") Long id,
            @RequestParam(value = "amount") BigDecimal amount,
            @RequestParam(value = "jyPassword") String jyPassword,
            @RequestParam(value = "limitCountValid") Integer limitCountValid,
            @RequestParam(value = "limitCountInDay") Integer limitCountInDay);

    /**
     * 用户参加锁仓活动
     * 备注：通用的锁仓接口
     *
     * @param lockType        活动类型
     * @param id              活动配置id
     * @param amount          购买份数
     * @param boughtAmount    可选，购买数量（一般和amount是一样的，如活动币种和参与活动币种不一样时，可能就不一样）
     * @param payCoinPrice    可选，支付币种USDT汇率
     * @param jyPassword      可选，资金密码
     * @param limitCountValid 可选，当前有效的活动数量限制
     * @param limitCountInDay 可选，当日参与活动的次数限制（不考虑是否有效）
     * @return 锁仓信息
     * @author yangch
     * @time 2019-06-20 17:04:49
     */
    @PostMapping("/lock2/v2/lock/lockWithPassword")
    MessageRespResult<LockCoinDetail> lockWithPassword(
            @RequestHeader("apiKey") String apiKey,
            @RequestParam(value = "lockType") LockType lockType,
            @RequestParam(value = "id") Long id,
            @RequestParam(value = "amount") BigDecimal amount,
            @RequestParam(value = "boughtAmount") BigDecimal boughtAmount,
            @RequestParam(value = "payCoinPrice") BigDecimal payCoinPrice,
            @RequestParam(value = "jyPassword") String jyPassword,
            @RequestParam(value = "limitCountValid") Integer limitCountValid,
            @RequestParam(value = "limitCountInDay") Integer limitCountInDay);


    /**
     * 参加SLP布朗计划活动
     *
     * @param id                   活动配置id
     * @param payCoinUnit          支付币种
     * @param payCoinUnitUsdtPrice 支付币种USDT汇率
     * @param jyPassword           可选，资金密码
     * @param limitCountValid      可选，当前有效的活动数量限制
     * @param limitCountInDay      可选，当日参与活动的次数限制（不考虑是否有效）
     * @return 锁仓信息
     * @author yangch
     * @time 2019-06-20 17:04:49
     */
    @PostMapping("/lock2/v2/lockSlp/lock")
    MessageRespResult<LockCoinDetail> lockSlp(@RequestHeader("apiKey") String apiKey,
                                              @RequestParam(value = "id") Long id,
                                              @RequestParam(value = "payCoinUnit") String payCoinUnit,
                                              @RequestParam(value = "payCoinUnitUsdtPrice") BigDecimal payCoinUnitUsdtPrice,
                                              @RequestParam(value = "jyPassword") String jyPassword,
                                              @RequestParam(value = "limitCountValid") Integer limitCountValid,
                                              @RequestParam(value = "limitCountInDay") Integer limitCountInDay);


    /**
     * 升级SLP布朗计划套餐活动
     *
     * @param id                   活动配置id
     * @param payCoinUnit          支付币种
     * @param payCoinUnitUsdtPrice 支付币种USDT汇率
     * @param jyPassword           可选，资金密码
     * @return 锁仓信息
     * @author yangch
     * @time 2019-07-25 16:11:18
     */
    @PostMapping("/lock2/v2/lockSlp/upgradeSlpPackage")
    MessageRespResult<List<LockCoinDetail>> upgradeSlpPackage(
            @RequestHeader("apiKey") String apiKey,
            @RequestParam(value = "id") Long id,
            @RequestParam(value = "payCoinUnit") String payCoinUnit,
            @RequestParam(value = "payCoinUnitUsdtPrice") BigDecimal payCoinUnitUsdtPrice,
            @RequestParam(value = "jyPassword") String jyPassword);

    /**
     * 根据id获取锁仓活动方案配置
     *
     * @param id
     * @return LockCoinActivitieSetting
     * @author zhangYanjun
     * @time 2019.06.20 15:33
     */
    @PostMapping(value = "/lock2/v2/lockCoinActivitieSetting/findOne")
    MessageRespResult<LockCoinActivitieSetting> findLockSettingById(@RequestParam(value = "id") Long id);

    /**
     * 根据id修改返佣状态
     *
     * @param id 锁仓详情id
     * @return com.spark.bitrade.util.MessageRespResult<java.lang.Boolean>
     * @author zhangYanjun
     * @time 2019.06.21 16:21
     */
    @PostMapping(value = "/lock2/v2/lockCoinDetail/updateRewardStatusToCompleteById")
    MessageRespResult<Boolean> updateRewardStatusToCompleteById(@RequestParam(value = "id") Long id);

    /**
     * 根据id修改返佣状态
     *
     * @param id 锁仓详情id
     * @return com.spark.bitrade.util.MessageRespResult<java.lang.Boolean>
     * @author zhangYanjun
     * @time 2019.06.21 16:21
     */
    @PostMapping(value = "/lock2/v2/lockCoinDetail/updateStatusById")
    MessageRespResult<Boolean> updateStatusById(@RequestParam(value = "id") Long id,
                                                @RequestParam(value = "oldStatus") LockStatus oldStatus,
                                                @RequestParam(value = "newStatus") LockStatus newStatus);


    /**
     * 根据活动方案ID查询生效中子活动列表
     *
     * @param projectId 活动方案ID
     * @return 集合数据
     */
    @PostMapping(value = "/lock2/v2/lockCoinActivitieSetting/findListByTime")
    MessageRespResult<List<LockCoinActivitieSetting>> findListByTime(@RequestParam("projectId") Long projectId);

    /**
     * 获取参与记录
     *
     * @param lockType   活动类型
     * @param lockStatus 锁仓状态
     * @return
     */
    @PostMapping("/lock2/v2/lock/listAll")
    MessageRespResult<List<LockCoinDetail>> listAll(@RequestHeader("apiKey") String apiKey,
                                                           @RequestParam("lockType") LockType lockType,
                                                           @RequestParam("lockStatus")LockStatus lockStatus);

    @PostMapping("/lock2/v2/lock/simplelock")
    MessageRespResult<LockCoinDetail> simplelock(@RequestHeader("apiKey") String apiKey,
                                                 @RequestParam("lockType") LockType lockType,
                                                 @RequestParam("txsType") TransactionType txsType,
                                                 @RequestParam("amount") BigDecimal amount,
                                                 @RequestParam("unit") String unit,
                                                 @RequestParam("lockDay") int lockDay,
                                                 @RequestParam("lockId") Long lockId,
                                                 @RequestParam("operType") Long operType);

}
