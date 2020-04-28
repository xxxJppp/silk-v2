package com.spark.bitrade.biz.impl;

import com.spark.bitrade.api.dto.MinerBalanceTransactionType;
import com.spark.bitrade.api.dto.MinerOrderTransactionType;
import com.spark.bitrade.api.dto.UnlockDTO;
import com.spark.bitrade.biz.MinerWebSocketService;
import com.spark.bitrade.biz.ScheduleService;
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
import com.spark.bitrade.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author shenzucai
 * @time 2019.10.24 16:35
 */
@Service
// @AllArgsConstructor
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private BtBankConfigService btBankConfigService;

    @Autowired
    private BtBankMinerBalanceService btBankMinerBalanceService;

    @Autowired
    private BtBankMinerBalanceTransactionService btBankMinerBalanceTransactionService;

    @Autowired
    private BtBankMinerOrderService btBankMinerOrderService;

    @Autowired
    private BtBankMinerOrderTransactionService btBankMinerOrderTransactionService;

    @Autowired
    private IdWorkByTwitter idWorkByTwitter;

    @Autowired
    private MemberWalletService memberWalletService;


    @Autowired
    private MinerWebSocketService minerWebSocketService;


    @Value("${btbank.reward.member:70653}")
    private Long memberId;

    /**
     * 解锁资产
     *
     * @return true
     * @author shenzucai
     * @time 2019.10.24 16:41
     */
    @Override
    public Boolean unLockAssert() {
        // 查找所以可以解锁的订单
        // 查找所以可以返还的固定收益
        // 读取派单解锁时间
        Object dispatchUnLockTimeO = btBankConfigService.getConfig(BtBankSystemConfig.UNLOCK_TIME);
        if (Objects.isNull(dispatchUnLockTimeO)) {
            throw new BtBankException(71005, "载入系统配置失败");
        }
        Integer dispatchUnLockTime = Integer.valueOf(String.valueOf(dispatchUnLockTimeO));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date zero = calendar.getTime();
        List<UnlockDTO> unlockDTOS = btBankMinerOrderTransactionService.listUnlockRecords(DateUtil.addMinToDate(zero, -dispatchUnLockTime));
        if (unlockDTOS == null || unlockDTOS.size() < 1) {
            throw new BtBankException(71006, "暂无可解锁订单");
        }

        for (UnlockDTO unlockDTO : unlockDTOS) {
            getService().autoUnlockAssert(unlockDTO);
        }

        //add|edit|del by  shenzucai 时间： 2019.10.28  原因：增加订单超时取消
        getService().autoCancel(zero);
        return Boolean.TRUE;
    }

    /**
     * 取消超时订单
     *
     * @param zero
     * @return true
     * @author shenzucai
     * @time 2019.10.28 10:41
     */
    @Async
    public void autoCancel(Date zero) {
        btBankMinerOrderService.lambdaUpdate().set(BtBankMinerOrder::getStatus, 5)
                .eq(BtBankMinerOrder::getStatus, 0)
                .lt(BtBankMinerOrder::getCreateTime, zero).update();
    }

    /**
     * 解锁
     *
     * @param unlockDTO
     * @return true
     * @author shenzucai
     * @time 2019.10.24 23:03
     */
    public void autoUnlockAssert(UnlockDTO unlockDTO) {
        switch (unlockDTO.getType()) {
            case 0:
                // 抢单，派单
                BtBankMinerOrderTransaction btBankMinerOrderTransaction1 = btBankMinerOrderTransactionService.getById(unlockDTO.getId());
                Boolean updateOrder = Boolean.FALSE;
                // 修改订单状态
                if (btBankMinerOrderTransaction1.getType().equals(1)) {
                    getService().unlockOrder(btBankMinerOrderTransaction1
                            , 3
                            , 1
                            , MinerOrderTransactionType.SECKILLED_ORDER_FINISHED
                            , "添加抢单订单记录失败"
                            , MinerBalanceTransactionType.GRAB_PRINCIPAL_TRANSFER_OUT
                            , "添加抢单本金转出记录失败"
                            , MinerBalanceTransactionType.GRAB_COMMISSION_TRANSFER_OUT
                            , "添加抢单本金佣金记录失败");

                } else {
                    // 派单
                    getService().unlockOrder(btBankMinerOrderTransaction1
                            , 4
                            , 2
                            , MinerOrderTransactionType.DISPATCHED_ORDER_FINISHED
                            , "添加派单订单记录失败"
                            , MinerBalanceTransactionType.DISPATCH_PRINCIPAL_TRANSFER_OUT
                            , "添加派单本金转出记录失败"
                            , MinerBalanceTransactionType.DISPATCH_COMMISSION_TRANSFER_OUT
                            , "添加派单佣金记录失败");
                }


                break;
            case 1:
                getService().unlockFixed(unlockDTO);

                break;
            default:
                break;
        }
    }

    /**
     * 固定本金和固定收益
     *
     * @param unlockDTO
     * @return true
     * @author shenzucai
     * @time 2019.10.25 11:43
     */
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void unlockFixed(UnlockDTO unlockDTO) {
        // 读取派单收益比例
        Object fixedScaleO = btBankConfigService.getConfig(BtBankSystemConfig.FIXED_COMMISSION_RATE);
        if (Objects.isNull(fixedScaleO)) {
            throw new BtBankException("载入系统配置失败");
        }
        BigDecimal fixedScale = new BigDecimal(String.valueOf(fixedScaleO));


        // 固定
        BtBankMinerBalanceTransaction btBankMinerBalanceTransaction = btBankMinerBalanceTransactionService.getById(unlockDTO.getId());
        BigDecimal temp = BigDecimalUtil.mul2down(btBankMinerBalanceTransaction.getBalance(), fixedScale);

        BtBankMinerBalanceTransaction btBankMinerBalanceTransactionZ = new BtBankMinerBalanceTransaction();
        btBankMinerBalanceTransactionZ.setId(idWorkByTwitter.nextId());
        btBankMinerBalanceTransactionZ.setMemberId(btBankMinerBalanceTransaction.getMemberId());
        btBankMinerBalanceTransactionZ.setType(MinerBalanceTransactionType.TRANSFER_OUT.getValue());
        btBankMinerBalanceTransactionZ.setBalance(BigDecimal.ZERO);
        btBankMinerBalanceTransactionZ.setMoney(btBankMinerBalanceTransaction.getBalance());
        btBankMinerBalanceTransactionZ.setCreateTime(new Date());
        btBankMinerBalanceTransactionZ.setRefId(btBankMinerBalanceTransaction.getId());

        Boolean balanceTransactionZ = btBankMinerBalanceTransactionService.save(btBankMinerBalanceTransactionZ);

        if (!balanceTransactionZ) {
            throw new BtBankException("添加本金转出记录失败");
        }

        BtBankMinerBalanceTransaction btBankMinerBalanceTransactionF = new BtBankMinerBalanceTransaction();
        btBankMinerBalanceTransactionF.setId(idWorkByTwitter.nextId());
        btBankMinerBalanceTransactionF.setMemberId(btBankMinerBalanceTransaction.getMemberId());
        btBankMinerBalanceTransactionF.setType(MinerBalanceTransactionType.FIEXD_COMMISSION_TRANSFER_OUT.getValue());
        btBankMinerBalanceTransactionF.setBalance(BigDecimal.ZERO);
        btBankMinerBalanceTransactionF.setMoney(temp);
        btBankMinerBalanceTransactionF.setCreateTime(new Date());
        btBankMinerBalanceTransactionF.setRefId(btBankMinerBalanceTransaction.getId());

        Boolean balanceTransactionF = btBankMinerBalanceTransactionService.save(btBankMinerBalanceTransactionF);

        if (!balanceTransactionF) {
            throw new BtBankException("添加本金佣金转出记录失败");
        }

        BtBankMinerBalanceTransaction btBankMinerBalanceTransactionF1 = new BtBankMinerBalanceTransaction();
        btBankMinerBalanceTransactionF1.setId(idWorkByTwitter.nextId());
        btBankMinerBalanceTransactionF1.setMemberId(btBankMinerBalanceTransaction.getMemberId());
        btBankMinerBalanceTransactionF1.setType(MinerBalanceTransactionType.FIEXD_COMMISSION_TRANSFER_IN.getValue());
        btBankMinerBalanceTransactionF1.setBalance(BigDecimal.ZERO);
        btBankMinerBalanceTransactionF1.setMoney(btBankMinerBalanceTransactionF.getMoney());
        btBankMinerBalanceTransactionF1.setCreateTime(new Date());
        btBankMinerBalanceTransactionF1.setRefId(btBankMinerBalanceTransaction.getId());

        Boolean balanceTransactionF1 = btBankMinerBalanceTransactionService.save(btBankMinerBalanceTransactionF1);

        if (!balanceTransactionF1) {
            throw new BtBankException("添加本金佣金转入记录失败");
        }

        Boolean btBankMinerBalance = btBankMinerBalanceTransactionService.lambdaUpdate()
                .setSql("balance = balance - " + btBankMinerBalanceTransaction.getBalance().toPlainString())
                .ge(BtBankMinerBalanceTransaction::getBalance, btBankMinerBalanceTransaction.getBalance())
                .eq(BtBankMinerBalanceTransaction::getId, btBankMinerBalanceTransaction.getId())
                .update();

        if (!btBankMinerBalance) {
            throw new BtBankException("余额记录变动失败");
        }

        Boolean balance = btBankMinerBalanceService.lambdaUpdate()
                .setSql("balance_amount = balance_amount - " + btBankMinerBalanceTransaction.getBalance().toPlainString())
                .setSql("got_reward_sum = got_reward_sum + " + temp.toPlainString())
                .ge(BtBankMinerBalance::getBalanceAmount, btBankMinerBalanceTransaction.getBalance())
                .eq(BtBankMinerBalance::getMemberId, btBankMinerBalanceTransaction.getMemberId())
                .update();

        if (!balance) {
            throw new BtBankException("余额变动失败");
        }


        // 远程扣减资产
        MessageRespResult reduceResult =
                memberWalletService.optionMemberWalletBalance(memberId, "BT", "BT", temp.negate(), 0L, "矿池佣金");
        Boolean succeedRe = (Boolean) reduceResult.getData();
        if (!succeedRe) {
            log.warn("transfer amount into miner pool failed. memberId({}) amount({})", btBankMinerBalanceTransaction.getMemberId(), temp);
            throw new BtBankException(MessageCode.ACCOUNT_BALANCE_INSUFFICIENT);
        }
        // 远程扣减资产
        MessageRespResult respResult =
                memberWalletService.optionMemberWalletBalance(btBankMinerBalanceTransaction.getMemberId(), "BT", "BT", btBankMinerBalanceTransaction.getBalance().add(temp), 0L, "矿池划转到btbank");
        Boolean succeeded = (Boolean) respResult.getData();
        if (!succeeded) {
            log.warn("transfer amount into miner pool failed. memberId({}) amount({})", btBankMinerBalanceTransaction.getMemberId(), btBankMinerBalanceTransaction.getBalance().add(temp));
            throw new BtBankException(MessageCode.ACCOUNT_BALANCE_INSUFFICIENT);
        }
    }

    /**
     * 处理订单解锁
     *
     * @param btBankMinerOrderTransaction1
     * @param i
     * @param i2
     * @param seckilledOrderFinished
     * @param orderComment
     * @param grabPrincipalTransferOut
     * @param orderAmountComment
     * @param grabCommissionTransferOut
     * @param orderRewardComment
     * @return true
     * @author shenzucai
     * @time 2019.10.25 11:42
     */
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void unlockOrder(BtBankMinerOrderTransaction btBankMinerOrderTransaction1
            , int i
            , int i2
            , MinerOrderTransactionType seckilledOrderFinished
            , String orderComment
            , MinerBalanceTransactionType grabPrincipalTransferOut
            , String orderAmountComment
            , MinerBalanceTransactionType grabCommissionTransferOut
            , String orderRewardComment) {
        Boolean updateOrder;// 抢单
        updateOrder = btBankMinerOrderService.lambdaUpdate()
                .set(BtBankMinerOrder::getProcessTime, new Date())
                .set(BtBankMinerOrder::getStatus, i)
                .eq(BtBankMinerOrder::getId, btBankMinerOrderTransaction1.getMinerOrderId())
                .eq(BtBankMinerOrder::getStatus, i2).update();
        if (updateOrder) {
            // 更新订单流水
            BtBankMinerOrderTransaction btBankMinerOrderTransaction = new BtBankMinerOrderTransaction();

            btBankMinerOrderTransaction.setId(idWorkByTwitter.nextId());
            btBankMinerOrderTransaction.setCreateTime(new Date());
            btBankMinerOrderTransaction.setMinerOrderId(btBankMinerOrderTransaction1.getMinerOrderId());
            btBankMinerOrderTransaction.setMemberId(btBankMinerOrderTransaction1.getMemberId());
            btBankMinerOrderTransaction.setRewardAmount(btBankMinerOrderTransaction1.getRewardAmount());
            btBankMinerOrderTransaction.setMoney(btBankMinerOrderTransaction1.getMoney());
            btBankMinerOrderTransaction.setType(seckilledOrderFinished.getValue());
            btBankMinerOrderTransaction.setUnlockTime(btBankMinerOrderTransaction1.getUnlockTime());

            Boolean updateOrderTransaction = btBankMinerOrderTransactionService.save(btBankMinerOrderTransaction);

            if (!updateOrderTransaction) {
                throw new BtBankException(71007, orderComment);
            }

            BtBankMinerBalanceTransaction btBankMinerBalanceTransaction = new BtBankMinerBalanceTransaction();
            btBankMinerBalanceTransaction.setId(idWorkByTwitter.nextId());
            btBankMinerBalanceTransaction.setMemberId(btBankMinerOrderTransaction1.getMemberId());
            btBankMinerBalanceTransaction.setType(grabPrincipalTransferOut.getValue());
            btBankMinerBalanceTransaction.setBalance(BigDecimal.ZERO);
            btBankMinerBalanceTransaction.setMoney(btBankMinerOrderTransaction1.getMoney());
            btBankMinerBalanceTransaction.setCreateTime(new Date());
            btBankMinerBalanceTransaction.setOrderTransactionId(btBankMinerOrderTransaction.getId());
            btBankMinerBalanceTransaction.setRefId(btBankMinerOrderTransaction.getMinerOrderId());

            Boolean balanceTransaction = btBankMinerBalanceTransactionService.save(btBankMinerBalanceTransaction);

            if (!balanceTransaction) {
                throw new BtBankException(71008, orderAmountComment);
            }

            BtBankMinerBalanceTransaction btBankMinerBalanceTransaction1 = new BtBankMinerBalanceTransaction();
            btBankMinerBalanceTransaction1.setId(idWorkByTwitter.nextId());
            btBankMinerBalanceTransaction1.setMemberId(btBankMinerOrderTransaction1.getMemberId());
            btBankMinerBalanceTransaction1.setType(grabCommissionTransferOut.getValue());
            btBankMinerBalanceTransaction1.setBalance(BigDecimal.ZERO);
            btBankMinerBalanceTransaction1.setMoney(btBankMinerOrderTransaction1.getRewardAmount());
            btBankMinerBalanceTransaction1.setCreateTime(new Date());
            btBankMinerBalanceTransaction1.setOrderTransactionId(btBankMinerOrderTransaction.getId());
            btBankMinerBalanceTransaction.setRefId(btBankMinerOrderTransaction.getMinerOrderId());

            Boolean balanceTransaction1 = btBankMinerBalanceTransactionService.save(btBankMinerBalanceTransaction1);

            if (!balanceTransaction1) {
                throw new BtBankException(71009, orderRewardComment);
            }

            Boolean balance = btBankMinerBalanceService.lambdaUpdate()
                    .setSql("lock_amount = lock_amount - " + btBankMinerOrderTransaction1.getMoney().toPlainString())
                    .setSql("processing_reward_sum = processing_reward_sum - " + btBankMinerOrderTransaction1.getRewardAmount().toPlainString())
                    .setSql("got_reward_sum = got_reward_sum + " + btBankMinerOrderTransaction1.getRewardAmount().toPlainString())
                    .ge(BtBankMinerBalance::getLockAmount, btBankMinerBalanceTransaction1.getMoney())
                    .ge(BtBankMinerBalance::getProcessingRewardSum, btBankMinerOrderTransaction1.getRewardAmount())
                    .eq(BtBankMinerBalance::getMemberId, btBankMinerOrderTransaction1.getMemberId())
                    .update();

            if (!balance) {
                throw new BtBankException(71010, "余额变动失败");
            }

            // 远程扣减资产
            MessageRespResult reduceResult =
                    memberWalletService.optionMemberWalletBalance(memberId, "BT", "BT", btBankMinerOrderTransaction1.getRewardAmount().negate(), 0L, "矿池佣金");
            Boolean succeedRe = (Boolean) reduceResult.getData();
            if (!succeedRe) {
                log.warn("transfer amount into miner pool failed. memberId({}) amount({})", btBankMinerBalanceTransaction.getMemberId(), btBankMinerOrderTransaction1.getRewardAmount());
                throw new BtBankException(MessageCode.ACCOUNT_BALANCE_INSUFFICIENT);
            }

            // 远程扣减资产
            MessageRespResult respResult =
                    memberWalletService.optionMemberWalletBalance(btBankMinerBalanceTransaction1.getMemberId(), "BT", "BT", btBankMinerOrderTransaction1.getMoney().add(btBankMinerOrderTransaction1.getRewardAmount()), 0L, "矿池划转到btbank");
            Boolean succeeded = (Boolean) respResult.getData();
            if (!succeeded) {
                log.warn("transfer amount into miner pool failed. memberId({}) amount({})", btBankMinerBalanceTransaction1.getMemberId(), btBankMinerOrderTransaction1.getMoney().add(btBankMinerOrderTransaction1.getRewardAmount()));
                throw new BtBankException(MessageCode.ACCOUNT_BALANCE_INSUFFICIENT);
            }
        }
    }

    /**
     * 自动派单（需要在解锁资产后，且建议两者时间间隔长）
     * 作为派单定时任务，将24小时无人抢单的订单，根据规则分派一个符合条件的矿工（防止订单积压）。
     * 矿工的【矿池可用】必须大于等于【订单金额】，没有符合条件的失败，等待下次执行
     * 24小时内(派单，抢单)总金额最少的
     * 24小时内(派单，抢单)次数最少的
     *
     * @return true
     * @author shenzucai
     * @time 2019.10.24 16:51
     */
    @Override
    public Boolean autoDispatch() {
        // 读取派单开关
        Object dispatchSwitchO = btBankConfigService.getConfig(BtBankSystemConfig.DISPATCH_SWITCH);
        if (Objects.isNull(dispatchSwitchO)) {
            throw new BtBankException(BtBankMsgCode.FAILED_TO_LOAD_SYSTEM_CONFIGURATION);
        }
        Boolean dispatchSwitch = Integer.valueOf(String.valueOf(dispatchSwitchO)) == 1 ? Boolean.TRUE : Boolean.FALSE;
        if (!dispatchSwitch) {
            throw new BtBankException(BtBankMsgCode.TURN_IN_SWITCH_OFF);
        }

        // 读取派单时长
        Object dispatchTimeO = btBankConfigService.getConfig(BtBankSystemConfig.DISPATCH_TIME);
        if (Objects.isNull(dispatchTimeO)) {
            throw new BtBankException(BtBankMsgCode.FAILED_TO_LOAD_SYSTEM_CONFIGURATION);
        }
        Long dispatchTime = Long.valueOf(String.valueOf(dispatchTimeO));

        // 读取派单收益比例
        Object dispatchScaleO = btBankConfigService.getConfig(BtBankSystemConfig.DISPATCH_COMMISSION_RATE);
        if (Objects.isNull(dispatchScaleO)) {
            throw new BtBankException(BtBankMsgCode.FAILED_TO_LOAD_SYSTEM_CONFIGURATION);
        }
        BigDecimal dispatchScale = new BigDecimal(String.valueOf(dispatchScaleO));


        // 读取派单解锁时间
        Object dispatchUnLockTimeO = btBankConfigService.getConfig(BtBankSystemConfig.UNLOCK_TIME);
        if (Objects.isNull(dispatchUnLockTimeO)) {
            throw new BtBankException(BtBankMsgCode.FAILED_TO_LOAD_SYSTEM_CONFIGURATION);
        }
        Integer dispatchUnLockTime = Integer.valueOf(String.valueOf(dispatchUnLockTimeO));

        // 查找符合派单的订单
        List<BtBankMinerOrder> btBankMinerOrders = btBankMinerOrderService.listDispatchOrder(dispatchTime);
        if (btBankMinerOrders == null || btBankMinerOrders.size() < 1) {
            throw new BtBankException(BtBankMsgCode.NO_ORDER_IN_LINE_WITH_THE_ORDER);
        }
        btBankMinerOrders.stream().forEach(btBankMinerOrder -> {
            // 开始匹配
            BtBankMinerBalance btBankMinerBalance = btBankMinerBalanceService.dispatchMiner(btBankMinerOrder.getMoney());
            if (Objects.isNull(btBankMinerBalance)) {
                log.info("未找到合适的矿工进行派单 {}", btBankMinerOrder);
            } else {
                List<BtBankMinerBalanceTransaction> btBankMinerBalanceTransactions = btBankMinerBalanceTransactionService.lambdaQuery()
                        .gt(BtBankMinerBalanceTransaction::getBalance, BigDecimal.ZERO)
                        .eq(BtBankMinerBalanceTransaction::getType, 1)
                        .eq(BtBankMinerBalanceTransaction::getMemberId, btBankMinerBalance.getMemberId())
                        .orderByAsc(BtBankMinerBalanceTransaction::getCreateTime).list();
                //进行匹配操作，异步处理
                getService().disPatchOrderWithMiner(btBankMinerOrder, btBankMinerBalance, dispatchScale, dispatchUnLockTime, btBankMinerBalanceTransactions);
            }
        });
        return Boolean.TRUE;
    }


    /**
     * 异步处理订单匹配
     *
     * @param btBankMinerOrder
     * @param btBankMinerBalance
     * @param dispatchScale
     * @param dispatchUnLockTime
     * @return true
     * @author shenzucai
     * @time 2019.10.24 21:17
     */
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void disPatchOrderWithMiner(final BtBankMinerOrder btBankMinerOrder
            , final BtBankMinerBalance btBankMinerBalance
            , final BigDecimal dispatchScale
            , final Integer dispatchUnLockTime
            , List<BtBankMinerBalanceTransaction> btBankMinerBalanceTransactions) {

        // 修改订单
        Boolean updateOrder = btBankMinerOrderService.lambdaUpdate().set(BtBankMinerOrder::getStatus, 2)
                .set(BtBankMinerOrder::getProcessTime, new Date())
                .set(BtBankMinerOrder::getMemberId, btBankMinerBalance.getMemberId())
                .eq(BtBankMinerOrder::getId, btBankMinerOrder.getId())
                .eq(BtBankMinerOrder::getStatus, 0).update();
        if (!updateOrder) {
            throw new BtBankException(BtBankMsgCode.FAILED_TO_MODIFY_THE_ORDER);
        }


        minerWebSocketService.sendNewOrderStatusToAllClient(btBankMinerOrder);

        BtBankMinerOrderTransaction btBankMinerOrderTransaction = new BtBankMinerOrderTransaction();

        btBankMinerOrderTransaction.setId(idWorkByTwitter.nextId());
        btBankMinerOrderTransaction.setCreateTime(new Date());
        btBankMinerOrderTransaction.setMinerOrderId(btBankMinerOrder.getId());
        btBankMinerOrderTransaction.setMemberId(btBankMinerBalance.getMemberId());
        btBankMinerOrderTransaction.setRewardAmount(BigDecimalUtil.mul2down(btBankMinerOrder.getMoney(), dispatchScale, 8));
        btBankMinerOrderTransaction.setMoney(btBankMinerOrder.getMoney());
        btBankMinerOrderTransaction.setType(MinerOrderTransactionType.DISPATCHED_ORDER.getValue());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date zero = calendar.getTime();
        btBankMinerOrderTransaction.setUnlockTime(DateUtil.addMinToDate(zero, dispatchUnLockTime));

        Boolean updateOrderTransaction = btBankMinerOrderTransactionService.save(btBankMinerOrderTransaction);

        if (!updateOrderTransaction) {
            throw new BtBankException(BtBankMsgCode.FAILED_TO_ADD_ORDER_RECORD);
        }

        BtBankMinerBalanceTransaction btBankMinerBalanceTransaction = new BtBankMinerBalanceTransaction();
        btBankMinerBalanceTransaction.setId(idWorkByTwitter.nextId());
        btBankMinerBalanceTransaction.setMemberId(btBankMinerBalance.getMemberId());
        btBankMinerBalanceTransaction.setType(MinerBalanceTransactionType.DISPATCHED_LOCKS.getValue());
        btBankMinerBalanceTransaction.setBalance(BigDecimal.ZERO);
        btBankMinerBalanceTransaction.setMoney(btBankMinerOrder.getMoney());
        btBankMinerBalanceTransaction.setCreateTime(new Date());
        btBankMinerBalanceTransaction.setOrderTransactionId(btBankMinerOrderTransaction.getId());
        btBankMinerBalanceTransaction.setRefId(btBankMinerOrder.getId());

        Boolean balanceTransaction = btBankMinerBalanceTransactionService.save(btBankMinerBalanceTransaction);

        if (!balanceTransaction) {
            throw new BtBankException(BtBankMsgCode.FAILED_TO_ADD_BALANCE_RECORD);
        }

        BtBankMinerBalanceTransaction btBankMinerBalanceTransaction2 = new BtBankMinerBalanceTransaction();
        btBankMinerBalanceTransaction2.setId(idWorkByTwitter.nextId());
        btBankMinerBalanceTransaction2.setMemberId(btBankMinerBalance.getMemberId());
        btBankMinerBalanceTransaction2.setType(MinerBalanceTransactionType.DISPATCH_COMMISSION_TRANSFER_IN.getValue());
        btBankMinerBalanceTransaction2.setBalance(BigDecimal.ZERO);
        btBankMinerBalanceTransaction2.setMoney(btBankMinerOrderTransaction.getRewardAmount());
        btBankMinerBalanceTransaction2.setCreateTime(new Date());
        btBankMinerBalanceTransaction2.setOrderTransactionId(btBankMinerOrderTransaction.getId());
        btBankMinerBalanceTransaction2.setRefId(btBankMinerOrder.getId());

        Boolean balanceTransaction1 = btBankMinerBalanceTransactionService.save(btBankMinerBalanceTransaction2);

        if (!balanceTransaction1) {
            throw new BtBankException(BtBankMsgCode.FAILED_TO_ADD_BALANCE_RECORD);
        }

        Boolean balance = btBankMinerBalanceService.lambdaUpdate()
                .setSql("balance_amount = balance_amount - " + btBankMinerOrder.getMoney().toPlainString())
                .setSql("lock_amount = lock_amount + " + btBankMinerOrder.getMoney().toPlainString())
                .setSql("processing_reward_sum = processing_reward_sum + " + btBankMinerOrderTransaction.getRewardAmount().toPlainString())
                .ge(BtBankMinerBalance::getBalanceAmount, btBankMinerOrder.getMoney())
                .eq(BtBankMinerBalance::getMemberId, btBankMinerBalance.getMemberId()).update();

        if (!balance) {
            throw new BtBankException(BtBankMsgCode.BALANCE_CHANGE_FAILED);
        }

        if (btBankMinerBalanceTransactions == null || btBankMinerBalanceTransactions.size() < 1) {
            throw new BtBankException(MessageCode.ACCOUNT_BALANCE_INSUFFICIENT);
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (BtBankMinerBalanceTransaction btBankMinerBalanceTransaction1 : btBankMinerBalanceTransactions) {

            BigDecimal tempBalance = btBankMinerOrder.getMoney().subtract(totalAmount);
            if (tempBalance.compareTo(btBankMinerBalanceTransaction1.getBalance()) <= 0) {
                Boolean aBoolean = btBankMinerBalanceTransactionService.lambdaUpdate()
                        .setSql("balance = balance - " + tempBalance.toPlainString())
                        .ge(BtBankMinerBalanceTransaction::getBalance, tempBalance)
                        .eq(BtBankMinerBalanceTransaction::getId, btBankMinerBalanceTransaction1.getId()).update();
                if (aBoolean) {
                    totalAmount = BigDecimalUtil.add(totalAmount, tempBalance);
                    break;
                } else {
                    continue;
                }
            } else {
                Boolean aBoolean1 = btBankMinerBalanceTransactionService.lambdaUpdate()
                        .setSql("balance = balance - " + btBankMinerBalanceTransaction1.getBalance().toPlainString())
                        .eq(BtBankMinerBalanceTransaction::getId, btBankMinerBalanceTransaction1.getId())
                        .eq(BtBankMinerBalanceTransaction::getBalance, btBankMinerBalanceTransaction1.getBalance()).update();
                if (aBoolean1) {
                    totalAmount = BigDecimalUtil.add(totalAmount, btBankMinerBalanceTransaction1.getBalance());
                } else {
                    continue;
                }
            }
        }

        if (totalAmount.compareTo(btBankMinerOrder.getMoney()) < 0) {
            throw new BtBankException(BtBankMsgCode.INSUFFICIENT_BALANCE_RECORD_AVAILABLE);
        }

    }

    public ScheduleServiceImpl getService() {
        return SpringContextUtil.getBean(ScheduleServiceImpl.class);
    }


}
