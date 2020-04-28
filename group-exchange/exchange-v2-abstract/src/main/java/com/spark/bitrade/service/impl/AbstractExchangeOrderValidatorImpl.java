package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constant.OrderValidateStatus;
import com.spark.bitrade.dao.ExchangeOrderDetailRepository;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.ExchangeOrderDetail;
import com.spark.bitrade.entity.ExchangeWalletWalRecord;
import com.spark.bitrade.entity.constants.BusinessErrorMonitorType;
import com.spark.bitrade.entity.constants.WalTradeType;
import com.spark.bitrade.service.BusinessErrorMonitorService;
import com.spark.bitrade.service.ExchangeOrderService;
import com.spark.bitrade.service.ExchangeOrderValidator;
import com.spark.bitrade.service.ExchangeWalletWalRecordService;
import com.spark.bitrade.trans.Tuple2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单验证消费
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/29 10:18
 */
@Slf4j
public abstract class AbstractExchangeOrderValidatorImpl implements ExchangeOrderValidator {

    protected ExchangeWalletWalRecordService walRecordService;
    protected ExchangeOrderDetailRepository orderDetailRepository;
    protected BusinessErrorMonitorService businessErrorMonitorService;
    protected ExchangeOrderService orderService;

    /**
     * 验证方法
     *
     * @param orderId 订单ID, refId
     */
    @Override
    public void validate(String orderId) {
        String errorMsg = this.doValidate(orderId);
        if (errorMsg != null) {
            businessErrorMonitorService.add(BusinessErrorMonitorType.EXCHANGE__USER_CHECK_FAIL, orderId, errorMsg);
            updateValidated(orderId, OrderValidateStatus.FAILED);
        }
    }

    @Override
    public String redo(String orderId) {
        return this.doValidate(orderId);
    }

    /**
     * 校验订单
     *
     * @param orderId
     * @return 错误信息，为null时为校验成功
     */
    public String doValidate(String orderId) {
        // 1. 获取所有WAL日志
        List<ExchangeWalletWalRecord> records = getCywWalletWalRecords(orderId);

        // tips：下单、撤单
        if (records.size() == 2) {
            // 下单和撤单记录
            boolean flagPlace = false, flagCancel = false;
            for (ExchangeWalletWalRecord record : records) {
                if (record.getTradeType().equals(WalTradeType.PLACE_ORDER)) {
                    flagPlace = true;
                }
                if (record.getTradeType().equals(WalTradeType.CANCEL_ORDER)) {
                    flagCancel = true;
                }
            }

            if (!flagPlace || !flagCancel) {
                // 正常成交订单最少存在2条流水
                return "1、账户流水校验失败：正常成交订单最少存在2条流水";
            }
        } else {
            // 已成交订单的验证
            if (records == null || records.size() < 3) {
                // 正常成交订单最少存在3条流水（，最少3条流水（tips：下单、成交）
                return "1、账户流水校验失败：正常成交订单最少存在3条流水";
            }

            // 2. 确定订单和方向
            Tuple2<ExchangeWalletWalRecord, ExchangeOrderDirection> tuple2 = getDirection(records);
            if (tuple2 == null) {
                // 未找到订单记录
                return "2、账户流水校验失败：未找到订单记录";
            }

            // 3. 确定成交记录信息
            List<ExchangeWalletWalRecord> turnovers = records.stream().filter(wal -> wal.getTradeType() == WalTradeType.TURNOVER)
                    .collect(Collectors.toList());

            if (turnovers.size() == 0 || turnovers.size() % 2 != 0) {
                // 成交记录不匹配，成交记录必定为双数
                return "3、账户成交流水校验失败：成交记录不匹配，成交记录必定为双数";
            }

            // 4. 获取所有成交流水
            List<ExchangeOrderDetail> details = orderDetailRepository.findAllByOrderId(orderId);
            if (details == null || details.size() * 2 != turnovers.size()) {
                // 正常成交订单最少存在1条流水， 一条流水对应两条wal日志记录
                return "4、订单成交流水校验失败：正常成交订单最少存在1条流水， 一条流水对应两条wal日志记录";
            }

            // 3. 比对订单成交数量
            if (!checkOrderAmountAndTurnover(tuple2, turnovers, details)) {
                // 流水不匹配
                return "5、订单成交明细流水校验失败：成交明细流水和wal日志记录的账不匹配";
            }

            // 4. 核算冻结余额是否全部释放

            // 下单记录
            // ExchangeWalletWalRecord place = tuple2.getFirst();

            BigDecimal sum = BigDecimal.ZERO;
            for (ExchangeWalletWalRecord record : records) {
                sum = sum.add(record.getTradeFrozen());
            }

            int compare = sum.compareTo(BigDecimal.ZERO);

            if (compare != 0) {
                // 存在数据丢失或订单不完整
                return "6、账户流水校验失败：存在数据丢失或订单不完整";
            }
        }

        // 5. 全部验证完成， 迁移数据
        if (updateValidated(orderId, OrderValidateStatus.SUCCESS)) {
            log.info("[ order_id = {} ] 订单校验完成", orderId);

            return null;
        } else {
            return "7、通过校验， 更新校验状态失败";
        }
    }

    /**
     * 比对订单成交数量和成交额
     *
     * @return
     */
    protected boolean checkOrderAmountAndTurnover(Tuple2<ExchangeWalletWalRecord, ExchangeOrderDirection> tuple2,
                                                  List<ExchangeWalletWalRecord> turnovers,
                                                  List<ExchangeOrderDetail> details) {
        // 交易对
        String symbol = details.get(0).getSymbol();
        // 下单币种
        String palaceSymbol = tuple2.getFirst().getCoinUnit();

        // WAL 余额 和 冻结
        BigDecimal balance = BigDecimal.ZERO;
        BigDecimal frozen = BigDecimal.ZERO;
        for (ExchangeWalletWalRecord record : turnovers) {
            balance = balance.add(record.getTradeBalance()).add(record.getFee());
            frozen = frozen.add(record.getTradeFrozen());
        }

        // 详情的 amount=交易数量 和 turnover=成交额
        BigDecimal amount = BigDecimal.ZERO;
        BigDecimal turnover = BigDecimal.ZERO;
        for (ExchangeOrderDetail detail : details) {
            amount = amount.add(detail.getAmount());
            turnover = turnover.add(detail.getTurnover());
        }

        // 校验：成交额和交易数量
        if (symbol.endsWith(palaceSymbol)) {
            // tips：下单币种 为基币时 为买入行为

            // 买单 amount = balance, turnover = frozen
            return amount.compareTo(balance.abs()) == 0 && turnover.compareTo(frozen.abs()) == 0;
        } else {
            // 卖单 amount = frozen, turnover = balance
            return amount.compareTo(frozen.abs()) == 0 && turnover.compareTo(balance.abs()) == 0;
        }
    }

    /**
     * 获取所有WAL日志
     *
     * @param orderId
     * @return
     */
    protected List<ExchangeWalletWalRecord> getCywWalletWalRecords(String orderId) {
        QueryWrapper<ExchangeWalletWalRecord> query = new QueryWrapper<>();
        query.eq("ref_id", orderId);

        return walRecordService.list(query);
    }

    protected boolean updateValidated(String refId, OrderValidateStatus status) {
        UpdateWrapper<ExchangeOrder> update = new UpdateWrapper<>();
        update.eq("order_id", refId).set("validated", status);
        return orderService.update(update);
    }

    /**
     * 获取订单方向
     * <p>
     * 若返回结果为 NULL 则表示未找到下单记录
     *
     * @param records wal日志
     * @return null or tuple2[wal,direction]
     */
    protected Tuple2<ExchangeWalletWalRecord, ExchangeOrderDirection> getDirection(List<ExchangeWalletWalRecord> records) {

        // 下单记录
        ExchangeWalletWalRecord wal = null;
        for (ExchangeWalletWalRecord record : records) {
            if (record.getTradeType() == WalTradeType.PLACE_ORDER) {
                wal = record;
                break;
            }
        }

        if (wal == null) {
            return null;
        }

        // 确定方式
        // ref_id = S232835760372318208_BTCUSDT
        // coin_unit = BTC 卖单
        // coin_unit = USDT 买单
// 无法判断订单方向
//        String coinUnit = wal.getCoinUnit();
//        String refId = wal.getRefId();
//
//        if (refId.endsWith(coinUnit)) {
//            return new Tuple2<>(wal, ExchangeOrderDirection.BUY);
//        }
//        return new Tuple2<>(wal, ExchangeOrderDirection.SELL);
        return new Tuple2<>(wal, null);
    }


    // -----------------------------------------
    // > S E T T E R S
    // -----------------------------------------

    @Autowired
    public void setWalRecordService(ExchangeWalletWalRecordService walRecordService) {
        this.walRecordService = walRecordService;
    }

    @Autowired
    public void setOrderDetailRepository(ExchangeOrderDetailRepository orderDetailRepository) {
        this.orderDetailRepository = orderDetailRepository;
    }

    @Autowired
    public void setBusinessErrorMonitorService(BusinessErrorMonitorService businessErrorMonitorService) {
        this.businessErrorMonitorService = businessErrorMonitorService;
    }

    @Autowired
    public void setOrderService(ExchangeOrderService orderService) {
        this.orderService = orderService;
    }
}
