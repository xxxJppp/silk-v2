package com.spark.bitrade.biz.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.spark.bitrade.api.dto.MinerAssetDTO;
import com.spark.bitrade.api.dto.MinerBalanceTransactionType;
import com.spark.bitrade.api.dto.MinerOrderTransactionType;
import com.spark.bitrade.api.vo.MinerBalanceTransactionsVO;
import com.spark.bitrade.api.vo.MinerBalanceVO;
import com.spark.bitrade.api.vo.MinerOrderTransactionsVO;
import com.spark.bitrade.api.vo.MinerOrdersVO;
import com.spark.bitrade.biz.MinerService;
import com.spark.bitrade.biz.MinerWebSocketService;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.constant.BtBankSystemConfig;
import com.spark.bitrade.enums.MessageCode;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.BtBankMinerBalance;
import com.spark.bitrade.repository.entity.BtBankMinerBalanceTransaction;
import com.spark.bitrade.repository.entity.BtBankMinerOrder;
import com.spark.bitrade.repository.entity.BtBankMinerOrderTransaction;
import com.spark.bitrade.repository.service.BtBankMinerBalanceService;
import com.spark.bitrade.repository.service.BtBankMinerBalanceTransactionService;
import com.spark.bitrade.repository.service.BtBankMinerOrderService;
import com.spark.bitrade.repository.service.BtBankMinerOrderTransactionService;
import com.spark.bitrade.service.BtBankConfigService;
import com.spark.bitrade.service.MemberWalletService;
import com.spark.bitrade.util.BigDecimalUtil;
import com.spark.bitrade.util.IdWorkByTwitter;
import com.spark.bitrade.util.MessageRespResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author davi
 */
@Slf4j
@Service
public class MinerServiceImpl implements MinerService {

    @Autowired
    private BtBankMinerBalanceService minerBalanceService;
    @Autowired
    private MemberWalletService memberWalletService;
    @Autowired
    private BtBankMinerBalanceTransactionService minerBalanceTransactionService;
    @Autowired
    private BtBankMinerOrderService minerOrderService;
    @Autowired
    private BtBankMinerOrderTransactionService minerOrderTransactionService;
    @Autowired
    private IdWorkByTwitter idWorkByTwitter;
    @Autowired
    private BtBankConfigService btBankConfigService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MinerWebSocketService minerWebSocketService;


    @Override
    public MinerAssetDTO queryMinerAsset(Long memberId) {
        BtBankMinerBalance minerBalance = this.checkAndCreateBalanceRecord(memberId);
        MinerAssetDTO dto = new MinerAssetDTO();
        dto.setUsedAmount(minerBalance.getBalanceAmount());
        dto.setLockedAmount(minerBalance.getLockAmount());
        dto.setRewardAmount(minerBalance.getProcessingRewardSum());
        dto.setGotRewardAmount(minerBalance.getGotRewardSum());
        List<BigDecimal> total =
                Arrays.asList(
                        minerBalance.getBalanceAmount(),
                        minerBalance.getLockAmount(),
                        minerBalance.getProcessingRewardSum());
        Optional<BigDecimal> totalAmount = total.stream().reduce(BigDecimal::add);
        dto.setTotalAmount(totalAmount.orElse(BigDecimal.ZERO));
        return dto;
    }

    @Override
    public void transferAsset(BigDecimal amount, Long memberId) {
        // 全局配置允许转出
        String transferSwitch =
                (String) btBankConfigService.getConfig(BtBankSystemConfig.TRANSFER_SWITCH);
        if (!transferSwitch.equalsIgnoreCase("1")) {
            throw new BtBankException(BtBankMsgCode.TURN_IN_SWITCH_OFF);
        }

        this.checkAndCreateBalanceRecord(memberId);

        // amount 超过全局配置的最低划转金额
        String minimumAmountString =
                (String) btBankConfigService.getConfig(BtBankSystemConfig.MINIMUM_TRANSFER_AMOUNT);
        BigDecimal minimumAmount = new BigDecimal(minimumAmountString);
        if (amount.compareTo(minimumAmount) < 0) {
            throw new BtBankException(BtBankMsgCode.BELOW_THE_MINIMUM);
        }

        // 远程扣减资产
        MessageRespResult respResult =
                memberWalletService.optionMemberWalletBalance(memberId, "BT", "BT", amount.negate(), 0L, "btbank划转到矿池");
        Boolean succeeded = (Boolean) respResult.getData();
        if (!succeeded) {
            log.warn("transfer amount into miner pool failed. memberId({}) amount({})", memberId, amount);
            throw new BtBankException(MessageCode.ACCOUNT_BALANCE_INSUFFICIENT);
        }

        // 远程划转成功，增加资产转入矿池记录
        BtBankMinerBalanceTransaction tx = new BtBankMinerBalanceTransaction();
        tx.setMemberId(memberId);
        tx.setId(idWorkByTwitter.nextId());
        tx.setCreateTime(new Date());
        tx.setRefId(null);
        tx.setBalance(amount);
        tx.setMoney(amount);
        tx.setType(1);
        if (!minerBalanceTransactionService.save(tx)) {
            log.warn("save BtBankMinerBalanceTransaction failed. {}", tx);
            throw new BtBankException(MessageCode.INCORRECT_STATE);
        }

        // 账户资产增加
        if (minerBalanceService.updateIncBalanceAmount(memberId, amount) <= 0) {
            log.warn("update BtBankMinerBalance failed. memberId({}) amount({})", memberId, amount);
            throw new BtBankException(MessageCode.INCORRECT_STATE);
        }
    }

    @Override
    public MinerBalanceVO getMinerBalance(Long memberId) {

        BtBankMinerBalance bankMinerBalance = checkAndCreateBalanceRecord(memberId);
        MinerBalanceVO minerBalanceVO = new MinerBalanceVO();
        BeanUtils.copyProperties(bankMinerBalance, minerBalanceVO);

        List types = Arrays.asList(MinerBalanceTransactionType.DISPATCH_COMMISSION_TRANSFER_IN.getValue(),
                MinerBalanceTransactionType.GRAB_COMMISSION_TRANSFER_IN.getValue(),
                MinerBalanceTransactionType.FIEXD_COMMISSION_TRANSFER_IN.getValue());

        BigDecimal yesterdayCommission = minerBalanceTransactionService.getYestodayMinerBalanceTransactionsSumByMemberId(memberId, types);

        if (yesterdayCommission == null) {
            yesterdayCommission = BigDecimal.ZERO;
        }

        minerBalanceVO.setYestodayRewardSum(yesterdayCommission);

        return minerBalanceVO;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public BtBankMinerOrderTransaction grabMineOrder(Long memberId, Long orderId) {


        String seckillSwitch = btBankConfigService.getConfig(BtBankSystemConfig.SECKILL_SWITCH).toString();

        if (!"1".equals(seckillSwitch)) {
            log.error("暂时无法抢单，请稍后再试 id: {}", orderId);
            throw new BtBankException(BtBankMsgCode.UNABLE_TO_SNATCH_THE_ORDER);
        }

        Object o = redisTemplate.opsForValue().get(BtBankSystemConfig.REDIS_MINER_ORDER_PREFIX + orderId);
        BtBankMinerOrder minerOrder = null;
        if (o == null) {
            minerOrder = minerOrderService.getById(orderId);
            redisTemplate.opsForValue().set(BtBankSystemConfig.REDIS_MINER_ORDER_PREFIX + orderId, minerOrder);
        } else {
            minerOrder = (BtBankMinerOrder) o;
        }

        if (minerOrder == null) {
            log.error("订单不存在 id: {}", orderId);
            throw new BtBankException(BtBankMsgCode.ORDER_NOT_EXIST);

        } else if (minerOrder.getStatus() > 0) {
            log.error("订单已经被抢或被派出 id: {}", minerOrder);
            throw new BtBankException(BtBankMsgCode.ORDERS_HAVE_LOOTED_OR_DISPATCHED);
        } else {


            log.info("抢订单  {}", minerOrder);

            BtBankMinerBalance minerBalance = checkAndCreateBalanceRecord(memberId);

            if (minerBalance.getBalanceAmount().compareTo(minerOrder.getMoney()) < 0) {
                log.error("可用余额不足 memberId: {}", memberId);
                throw new BtBankException(MessageCode.ACCOUNT_BALANCE_INSUFFICIENT);
            } else {

                log.info("抢订单用户  {}", minerBalance);

                // 修改订单状态
                minerOrder.setStatus(MinerOrderTransactionType.SECKILLED_ORDER.getValue());
                minerOrder.setMemberId(minerBalance.getMemberId());
                minerOrder.setProcessTime(new Date());

                if (minerOrderService.grabMinerOrderByIdWithStatus(minerOrder, 0) > 0) {

                    // 计算收益

                    String seckillRate = btBankConfigService.getConfig(BtBankSystemConfig.SECKILL_COMMISSION_RATE).toString();

                    BigDecimal commissinRate = new BigDecimal(seckillRate);
                    minerBalance.setBalanceAmount(
                            BigDecimalUtil.sub(minerBalance.getBalanceAmount(), minerOrder.getMoney()));

                    BigDecimal reward = BigDecimalUtil.mul2down(minerOrder.getMoney(), commissinRate, 8);
                    // 添加用户收益统计
                    minerBalance.setProcessingRewardSum(minerBalance.getProcessingRewardSum().add(reward));
                    minerBalance.setLockAmount(minerOrder.getMoney());


                    List<BtBankMinerBalanceTransaction> minerBalanceTransactions =
                            minerBalanceTransactionService.list(
                                    new QueryWrapper<BtBankMinerBalanceTransaction>()
                                            .eq("member_id", memberId)
                                            .eq("type", MinerBalanceTransactionType.TRANSFER_IN.getValue())
                                            .gt("balance", 0).orderByAsc("create_time"));

                    BigDecimal needPay = minerOrder.getMoney();

                    for (BtBankMinerBalanceTransaction transaction : minerBalanceTransactions) {
                        if (BigDecimalUtil.gt0(needPay)) {

                            BigDecimal tmpPayDecimal = needPay.compareTo(transaction.getBalance()) > 0 ? transaction.getBalance() : needPay;

                            if (minerBalanceTransactionService.spendBalanceWithIdAndBalance(transaction.getId(), tmpPayDecimal) > 0) {
                                needPay = BigDecimalUtil.sub(needPay, tmpPayDecimal);
                            }//;.updateById(transaction);

                        } else {
                            break;
                        }
                    }

                    if (BigDecimalUtil.gt0(needPay)) {
                        log.error("可用余额不足 memberId: {}", memberId);
                        throw new BtBankException(MessageCode.ACCOUNT_BALANCE_INSUFFICIENT);
                    }


                    // 添加订单记录
                    BtBankMinerOrderTransaction minerOrderTransaction = new BtBankMinerOrderTransaction();
                    minerOrderTransaction.setId(idWorkByTwitter.nextId());
                    minerOrderTransaction.setMemberId(minerBalance.getMemberId());
                    minerOrderTransaction.setCreateTime(new Date());
                    minerOrderTransaction.setMinerOrderId(minerOrder.getId());
                    minerOrderTransaction.setMoney(minerOrder.getMoney());
                    minerOrderTransaction.setRewardAmount(reward);
                    minerOrderTransaction.setType(MinerOrderTransactionType.SECKILLED_ORDER.getValue());

                    Integer unlockTimeSpan = Integer.valueOf(btBankConfigService.getConfig(BtBankSystemConfig.UNLOCK_TIME).toString());

                    //-----
//
//                    Calendar calendar = Calendar.getInstance();
//                    calendar.setTime(new Date());
//                    calendar.set(Calendar.HOUR_OF_DAY, 0);
//                    calendar.set(Calendar.MINUTE, 0);
//                    calendar.set(Calendar.SECOND, 0);
//                    minerOrderTransaction.setUnlockTime(DateUtil.addMinToDate(calendar.getTime(), unlockTimeSpan));

                    // 写资金流水 修改资金
                    //

                    if (lockMinerBalanceAndAddProcessingReward(minerBalance.getId(), minerBalance.getMemberId(), minerOrderTransaction.getId(), minerOrder.getMoney(), reward, minerOrder.getId()) > 0) {
                        //minerOrderTransactionService.save(minerOrderTransaction);
                        minerOrderTransactionService.insertGrabOrDepatchOrder(minerOrderTransaction, unlockTimeSpan);
                        //清除订单缓存
                        redisTemplate.delete(BtBankSystemConfig.REDIS_MINER_ORDER_PREFIX + orderId);
                        minerWebSocketService.sendNewOrderStatusToAllClient(minerOrder);
                        log.info("抢单完成 {}", minerBalance);
                        return minerOrderTransaction;
                    }
                }
            }
        }

        log.error("抢单失败 memberId: {}", memberId);
        throw new BtBankException(BtBankMsgCode.FAILED_TO_SNATCH_THE_ORDER);
    }


    /**
     * 抢单锁创 并添加记录
     *
     * @param minerBalanceId
     * @param memberId
     * @param minerOrderTransactionId
     * @param money
     * @param reward
     * @param orderId
     * @return true
     * @time 2019.10.26 1:23
     */

    @Override
    public int lockMinerBalanceAndAddProcessingReward(Long minerBalanceId, Long memberId, Long minerOrderTransactionId, BigDecimal money, BigDecimal reward, Long orderId) {

        int result = minerBalanceService.grabSuccAndUpdate(minerBalanceId, money, reward);
        if (result > 0) {
            BtBankMinerBalanceTransaction transaction = new BtBankMinerBalanceTransaction();
            transaction.setMemberId(memberId);
            transaction.setId(idWorkByTwitter.nextId());
            transaction.setCreateTime(new Date());
            transaction.setMoney(money);
            transaction.setOrderTransactionId(minerOrderTransactionId);
            transaction.setType(MinerBalanceTransactionType.GRABBED_LOCKS.getValue());
            transaction.setRefId(orderId);
            minerBalanceTransactionService.save(transaction);

            BtBankMinerBalanceTransaction grabcomissionTransferTransaction = new BtBankMinerBalanceTransaction();
            grabcomissionTransferTransaction.setMemberId(memberId);
            grabcomissionTransferTransaction.setId(idWorkByTwitter.nextId());
            grabcomissionTransferTransaction.setCreateTime(new Date());
            grabcomissionTransferTransaction.setMoney(reward);
            grabcomissionTransferTransaction.setOrderTransactionId(minerOrderTransactionId);
            grabcomissionTransferTransaction.setType(MinerBalanceTransactionType.GRAB_COMMISSION_TRANSFER_IN.getValue());
            grabcomissionTransferTransaction.setRefId(orderId);
            minerBalanceTransactionService.save(grabcomissionTransferTransaction);

        }
        return result;
    }


    @Override
    public MinerOrderTransactionsVO getMinerOrderTransactionsByMemberId(Long memberId, List<Integer> types, int page, int size) {
        return minerOrderTransactionService.getMinerOrderTransactionsByMemberId(memberId, types, page, size);
    }

    @Override
    public MinerOrderTransactionsVO getMinerOrderTransactionsByMemberId(Long memberId, int page, int size) {
        return getMinerOrderTransactionsByMemberId(memberId, new ArrayList<Integer>(), page, size);
    }

    @Override
    public MinerOrdersVO getMinerOrdersByMemberId(
            Long memberId, List<Integer> types, int page, int size) {
        return minerOrderService.getMinerOrdersByMemberId(memberId, types, page, size);
    }

    @Override
    public MinerOrdersVO getMyMinerOrdersByMemberId(
            Long memberId, List<Integer> types, int page, int size) {
        return minerOrderService.getMinerOrdersByMemberIdOrderByProcessCreateTime(memberId, types, page, size);
    }


    @Override
    public MinerOrdersVO getMinerOrdersByMemberId(Long memberId, int page, int size) {
        return getMinerOrdersByMemberId(memberId, new ArrayList<Integer>(), page, size);
    }

    @Override
    public MinerOrdersVO getMinerOrders(List<Integer> types, int page, int size) {
        return minerOrderService.getMinerOrders(types, page, size);
    }

    @Override
    public MinerOrdersVO getMinerOrders(int page, int size) {
        return getMinerOrders(new ArrayList<>(), page, size);
    }

    @Override
    public MinerBalanceTransactionsVO getMinerBalanceTransactionsByMemberId(
            Long memberId, List<Integer> types, int page, int size) {
        return minerBalanceTransactionService.getMinerBalanceTransactionsByMemberId(
                memberId, types, page, size);
    }

    @Override
    public MinerBalanceTransactionsVO getMinerBalanceTransactionsByMemberId(
            Long memberId, int page, int size) {
        return getMinerBalanceTransactionsByMemberId(memberId, new ArrayList<Integer>(), page, size);
    }

    private BtBankMinerBalance checkAndCreateBalanceRecord(Long memberId) {
        BtBankMinerBalance memberBalance = minerBalanceService.findFirstByMemberId(memberId);
        if (memberBalance == null) {
            memberBalance = new BtBankMinerBalance();
            memberBalance.setMemberId(memberId);
            memberBalance.setId(idWorkByTwitter.nextId());
            memberBalance.setBalanceAmount(BigDecimal.ZERO);
            memberBalance.setGotRewardSum(BigDecimal.ZERO);
            memberBalance.setLockAmount(BigDecimal.ZERO);
            memberBalance.setProcessingRewardSum(BigDecimal.ZERO);
            Date now = new Date();
            memberBalance.setCreateTime(now);
            memberBalance.setUpdateTime(now);
            minerBalanceService.save(memberBalance);
        }
        return memberBalance;
    }
}
