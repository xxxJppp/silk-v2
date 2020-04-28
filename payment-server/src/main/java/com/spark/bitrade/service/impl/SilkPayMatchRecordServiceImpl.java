package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constant.WalletChangeType;
import com.spark.bitrade.constants.CommandCode;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.constants.PSMsgCode;
import com.spark.bitrade.emums.MqttAnswerType;
import com.spark.bitrade.entity.*;
import com.spark.bitrade.entity.dto.SilkPayAccountDto;
import com.spark.bitrade.mapper.SilkPayAccountMapper;
import com.spark.bitrade.mapper.SilkPayMatchRecordMapper;
import com.spark.bitrade.mapper.SilkPayOrderMapper;
import com.spark.bitrade.mapper.SilkPayUserConfigMapper;
import com.spark.bitrade.mqtt.MqttSender;
import com.spark.bitrade.service.*;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * 付款匹配记录(SilkPayMatchRecord)表服务实现类
 *
 * @author wsy
 * @since 2019-07-18 10:38:51
 */
@Service("silkPayMatchRecordService")
@Slf4j
public class SilkPayMatchRecordServiceImpl extends ServiceImpl<SilkPayMatchRecordMapper, SilkPayMatchRecord> implements SilkPayMatchRecordService {

    @Autowired
    private SilkPayAccountService silkPayAccountService;

    @Autowired
    private SilkPayDeviceService silkPayDeviceService;

    @Autowired
    private MqttSender mqttSender;

    @Autowired
    private IMemberWalletApiService iMemberWalletApiService;

    @Resource
    private SilkPayOrderMapper silkPayOrderMapper;

    @Resource
    private SilkPayMatchRecordMapper silkPayMatchRecordMapper;
    @Resource
    private SilkPayUserConfigMapper silkPayUserConfigMapper;
    @Resource
    private SilkPayAccountMapper silkPayAccountMapper;
    @Resource
    private ISilkDataDistApiService silkDataDistApiService;

    private SilkPayAccount filter(List<SilkPayAccountDto> list) {
        List<Map<String, Object>> running = silkPayMatchRecordMapper.getRunningPay();
        List<Map<String, Object>> count48 = silkPayMatchRecordMapper.getCount48();

        List<Long[]> sort = new ArrayList<>();
        list.forEach(i -> {
            long _sort = list.indexOf(i);
            // 排序正在进行的订单
            Optional<Map<String, Object>> optional = running.stream().filter(r -> r.get("matchAccount").equals(i.getId())).findFirst();
            _sort += optional.map(r -> running.indexOf(r) + 1000).orElse(0);
            // 排序48小时内订单
            optional = count48.stream().filter(c -> c.get("matchAccount").equals(i.getId())).findFirst();
            _sort += optional.map(count48::indexOf).orElse(0);

            sort.add(new Long[]{i.getId(), _sort});
        });

        Optional<Long[]> optional = sort.stream().min(Comparator.comparing(o -> o[1]));
        return optional.map(longs -> {
            Optional<SilkPayAccountDto> _op = list.stream().filter(i -> i.getId().equals(longs[0])).findFirst();
            return _op.orElse(list.get(0));
        }).orElseGet(() -> list.get(0));
    }

    /**
     * 订单匹配和拆分
     *
     * @param silkPayOrder
     * @param gpsLocation
     * @return true
     * @author shenzucai
     * @time 2019.07.30 16:54
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean payOrSplitOrderMatchRecord(SilkPayOrder silkPayOrder, GpsLocation gpsLocation) {
        log.info("订单开始匹配：{}", silkPayOrder);
        List<SilkPayAccountDto> list = silkPayAccountService.findMostSuitableAccount(gpsLocation, silkPayOrder.getMoney(), silkPayOrder.getReceiptType());
        AssertUtil.isTrue(list.size() > 0, PSMsgCode.MATCH_ACCOUNT_FAILED);
        SilkPayAccount silkPayAccount = filter(list);

        // 此时以拆分金额为准，进行订单拆分
        AssertUtil.notNull(silkPayAccount, PSMsgCode.MATCH_ACCOUNT_FAILED);
        // 防止高并发出现订单积压问题，如果出现则拒绝当前订单，返回未匹配到核实的账户
        List<SilkPayMatchRecord> silkPayMatchRecordds = baseMapper.selectList(new QueryWrapper<SilkPayMatchRecord>().eq("match_account", silkPayAccount.getNick()).eq("state", 0));
        if (silkPayMatchRecordds != null && silkPayMatchRecordds.size() > 3) {
            log.info("订单积压过多：{}", silkPayOrder);
            AssertUtil.notNull(silkPayAccount, PSMsgCode.MATCH_ACCOUNT_FAILED);
        }
        // 获取clientId
        SilkPayDevice silkPayDevice = silkPayDeviceService.getById(silkPayAccount.getDeviceId());
        AssertUtil.isTrue(silkPayDevice != null && (BooleanEnum.IS_TRUE == silkPayDevice.getState()), PSMsgCode.MATCH_ACCOUNT_FAILED);
        // 额度变化
        Boolean aBoolean = silkPayAccountService.reduceAccountLimit(silkPayAccount.getId(), silkPayOrder.getMoney());
        AssertUtil.isTrue(aBoolean, CommonMsgCode.SERVICE_UNAVAILABLE);
        //冻结相应数字资产
        WalletTradeEntity tradeEntity = new WalletTradeEntity();
        tradeEntity.setType(TransactionType.DIRECT_PAY);
        tradeEntity.setRefId(silkPayOrder.getId().toString());
        tradeEntity.setChangeType(WalletChangeType.TRADE);
        tradeEntity.setMemberId(silkPayOrder.getMemberId());
        tradeEntity.setComment(TransactionType.DIRECT_PAY.name());
        tradeEntity.setTradeBalance(silkPayOrder.getAmount().negate());
        // tradeEntity.setTradeFrozenBalance(silkPayOrder.getAmount());
        tradeEntity.setCoinUnit(silkPayOrder.getCoinId());
        MessageRespResult<String> coinIdResult = iMemberWalletApiService.getCoinNameByUnit(silkPayOrder.getCoinId());
        AssertUtil.isTrue(coinIdResult != null && coinIdResult.isSuccess(), PSMsgCode.REMOTE_SERVICE_FAILED);
        tradeEntity.setCoinId(coinIdResult.getData());
        MessageRespResult<WalletChangeRecord> changeRecordMessageRespResult = iMemberWalletApiService.tradeTccTry(tradeEntity);
        AssertUtil.isTrue(changeRecordMessageRespResult != null && changeRecordMessageRespResult.isSuccess(), PSMsgCode.REMOTE_SERVICE_FAILED);
        WalletChangeRecord walletChangeRecord = changeRecordMessageRespResult.getData();
        log.info("--余额预处理---余额变动记录id--{}----用户id---{}", walletChangeRecord.getId(), silkPayOrder.getMemberId());

        List<SilkPayMatchRecord> silkPayMatchRecords = new ArrayList<>();

        try {

            AssertUtil.notNull(silkPayDevice, PSMsgCode.GET_DEVICE_SERIAL_NO_FAILED);
            // 此时需要进行订单拆分，产生多笔流水
            if (silkPayOrder.getMoney().compareTo(silkPayAccount.getQuotaSplit()) == 1) {
                Integer splitCount = silkPayOrder.getMoney().divide(silkPayAccount.getQuotaSplit(), 0, BigDecimal.ROUND_UP).intValue();
                List<SilkPayMatchRecord> silkPayMatchRecordList = new ArrayList<>(splitCount);
                for (int i = 1; i <= splitCount; i++) {
                    SilkPayMatchRecord silkPayMatchRecord = new SilkPayMatchRecord();
                    if (i == splitCount) {
                        silkPayMatchRecord.setId(IdWorker.getId());
                        silkPayMatchRecord.setOrderSn(silkPayOrder.getId());
                        silkPayMatchRecord.setMatchAccount(silkPayAccount.getId());
                        silkPayMatchRecord.setPaymentType(silkPayOrder.getReceiptType());
                        silkPayMatchRecord.setOrderMoney(silkPayOrder.getMoney());
                        // 付款金额先默认为订单金额，后续可能发生变化，请在发生变化的地方进行更新m订单金额，n为拆分次数-1，j为查分金额，m-n*j 即可得到最后一次拆分的金额
                        BigDecimal amount = silkPayOrder.getMoney().subtract(silkPayAccount.getQuotaSplit().setScale(2, BigDecimal.ROUND_DOWN).multiply(new BigDecimal(splitCount - 1)));
                        silkPayMatchRecord.setPaymentMoney(amount.setScale(2, BigDecimal.ROUND_DOWN));
                        silkPayMatchRecord.setState(0);
                    } else {
                        silkPayMatchRecord.setId(IdWorker.getId());
                        silkPayMatchRecord.setOrderSn(silkPayOrder.getId());
                        silkPayMatchRecord.setMatchAccount(silkPayAccount.getId());
                        silkPayMatchRecord.setPaymentType(silkPayOrder.getReceiptType());
                        silkPayMatchRecord.setOrderMoney(silkPayOrder.getMoney());
                        // 付款金额先默认为订单金额，后续可能发生变化，请在发生变化的地方进行更新
                        silkPayMatchRecord.setPaymentMoney(silkPayAccount.getQuotaSplit().setScale(2, BigDecimal.ROUND_DOWN));
                        silkPayMatchRecord.setState(0);
                    }
                    silkPayMatchRecordList.add(silkPayMatchRecord);
                }
                // 保存匹配记录
                if (silkPayMatchRecordList != null && silkPayMatchRecordList.size() > 0) {
                    silkPayMatchRecords.addAll(silkPayMatchRecordList);
                    AssertUtil.isTrue(saveBatch(silkPayMatchRecordList), PSMsgCode.MATCH_RECORD_SAVE_FAILED);
                }
            } else {
                // 无需进行订单拆分，产生付款流水
                SilkPayMatchRecord silkPayMatchRecord = new SilkPayMatchRecord();
                silkPayMatchRecord.setId(IdWorker.getId());
                silkPayMatchRecord.setOrderSn(silkPayOrder.getId());
                silkPayMatchRecord.setMatchAccount(silkPayAccount.getId());
                silkPayMatchRecord.setPaymentType(silkPayOrder.getReceiptType());
                silkPayMatchRecord.setOrderMoney(silkPayOrder.getMoney());
                // 付款金额先默认为订单金额，后续可能发生变化，请在发生变化的地方进行更新
                silkPayMatchRecord.setPaymentMoney(silkPayOrder.getMoney());
                silkPayMatchRecord.setState(0);
                silkPayMatchRecords.add(silkPayMatchRecord);
                //保存匹配记录,下发订单
                AssertUtil.isTrue(save(silkPayMatchRecord), PSMsgCode.MATCH_RECORD_SAVE_FAILED);
            }
        } catch (Exception e) {
            // 此时进行资金变动撤销
            MessageRespResult<Boolean> booleanMessageRespResult = iMemberWalletApiService.tradeTccCancel(silkPayOrder.getMemberId(), walletChangeRecord.getId());
            // 额度变化
            Boolean cancaleBoolean = silkPayAccountService.cancelAccountLimit(silkPayAccount.getId(), silkPayOrder);
            AssertUtil.isTrue(cancaleBoolean, CommonMsgCode.SERVICE_UNAVAILABLE);
            if (booleanMessageRespResult == null || !booleanMessageRespResult.isSuccess()) {
                log.info("--撤销余额变动失败---余额变动记录id--{}----用户id---{}", walletChangeRecord.getId(), silkPayOrder.getMemberId());
            }
            throw e;
        }
        silkPayOrder.setPaymentNote(walletChangeRecord.getId().toString());
        silkPayOrder.setState(1);
        // 保存匹配记录，下发订单
        if (silkPayMatchRecords != null && silkPayMatchRecords.size() > 0) {
            for (SilkPayMatchRecord silkPayMatchRecord : silkPayMatchRecords) {
                MqttPayTask mqttPayTask = createDistributeTask(silkPayMatchRecord, silkPayOrder, silkPayAccount);
                mqttSender.sendToUser(silkPayDevice.getSerialNo(), CommandCode.ORDER_DISTRIBUTE.name(), mqttPayTask);
            }
        }
        Boolean orderBoolean = silkPayOrderMapper.updateById(silkPayOrder) > 0 ? true : false;
        AssertUtil.isTrue(orderBoolean, CommonMsgCode.SERVICE_UNAVAILABLE);
        log.info("订单匹配结果：{}", silkPayOrder);
        return true;
    }

    /**
     * 订单下发成功确认
     *
     * @param mqttPayState
     * @return true
     * @author shenzucai
     * @time 2019.08.05 16:42
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void distributeSuccess(MqttPayState mqttPayState) {
        log.info("订单下发成功确认：{}", mqttPayState);
        //判断订单是否是确认的前置状态，下发中
        SilkPayOrder silkPayOrder = silkPayOrderMapper.selectById(mqttPayState.getOrderSn());
        if (silkPayOrder != null) {
            if (silkPayOrder.getState() != 1) {
                return;
            } else {
                // 进行订单更新
                UpdateWrapper<SilkPayMatchRecord> silkPayMatchRecordUpdateWrapper = new UpdateWrapper<SilkPayMatchRecord>()
                        .set("state", 1)
                        .eq("state", 0)
                        .eq("id", mqttPayState.getId())
                        .eq("order_sn", mqttPayState.getOrderSn());
                baseMapper.update(null, silkPayMatchRecordUpdateWrapper);

                QueryWrapper<SilkPayMatchRecord> silkPayMatchRecordQueryWrapper = new QueryWrapper<SilkPayMatchRecord>()
                        .eq("order_sn", mqttPayState.getOrderSn()).eq("state", 0);
                List<SilkPayMatchRecord> silkPayMatchRecords = baseMapper.selectList(silkPayMatchRecordQueryWrapper);
                // 判断是否对订单状态进行更新
                if (silkPayMatchRecords == null || silkPayMatchRecords.size() < 1) {
                    // 条件成立，则对订单状态进行变更
                    UpdateWrapper<SilkPayOrder> silkPayOrderUpdateWrapper = new UpdateWrapper<SilkPayOrder>()
                            .set("state", 2).eq("id", mqttPayState.getOrderSn()).eq("state", 1);
                    silkPayOrderMapper.update(null, silkPayOrderUpdateWrapper);
                }
                log.info("订单下发成功确认：{}", silkPayOrder);
            }

        }
    }


    public void answerSuccess(String clientId, MqttPayState mqttPayState) {
        MqttAnswer answer = new MqttAnswer();
        answer.setCurrentTime(new Date());
        answer.setId(mqttPayState.getId());
        answer.setOrderSn(mqttPayState.getOrderSn());
        answer.setType(MqttAnswerType.PAY_SUCCESS);
        mqttSender.sendToUser(clientId, CommandCode.ANSWER_ORDER_PAY_SUCCESS.name(), answer);
    }

    /**
     * 订单支付成功
     *
     * @param mqttPayState
     * @return true
     * @author shenzucai
     * @time 2019.08.05 17:03
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void paySuccessed(String clientId, MqttPayState mqttPayState) {
        answerSuccess(clientId, mqttPayState);
        log.info("订单支付成功：{}", mqttPayState);
        //判断订单是否已经支付过的
        SilkPayOrder silkPayOrder = silkPayOrderMapper.selectById(mqttPayState.getOrderSn());
        if (silkPayOrder != null) {
            if (silkPayOrder.getState() == 3) {
                return;
            } else {
                // 进行订单更新
                UpdateWrapper<SilkPayMatchRecord> silkPayMatchRecordUpdateWrapper = new UpdateWrapper<SilkPayMatchRecord>()
                        .set("state", 2)
                        .set("payment_note", mqttPayState.getRemark())
                        .set("payment_order_no", mqttPayState.getPaymentOrderNo())
                        .in("state", 0, 1)
                        .eq("id", mqttPayState.getId())
                        .eq("order_sn", mqttPayState.getOrderSn());
                baseMapper.update(null, silkPayMatchRecordUpdateWrapper);

                QueryWrapper<SilkPayMatchRecord> silkPayMatchRecordQueryWrapper = new QueryWrapper<SilkPayMatchRecord>()
                        .eq("order_sn", mqttPayState.getOrderSn()).eq("state", 1);
                List<SilkPayMatchRecord> silkPayMatchRecords = baseMapper.selectList(silkPayMatchRecordQueryWrapper);
                // 判断是否对订单状态进行更新
                if (silkPayMatchRecords == null || silkPayMatchRecords.size() < 1) {
                    // 条件成立，则对订单状态进行变更
                    UpdateWrapper<SilkPayOrder> silkPayOrderUpdateWrapper = new UpdateWrapper<SilkPayOrder>()
                            .set("state", 3)
                            .set("pay_time", mqttPayState.getCurrentTime())
                            .eq("id", mqttPayState.getOrderSn())
                            .in("state", 0, 1, 2);
                    silkPayOrderMapper.update(null, silkPayOrderUpdateWrapper);

                    MessageRespResult<Boolean> booleanMessageRespResult = iMemberWalletApiService.tradeTccConfirm(silkPayOrder.getMemberId(), Long.valueOf(silkPayOrder.getPaymentNote()));
                    log.info("--确认余额变动---余额变动记录id--{}----用户id---{}", silkPayOrder.getPaymentNote(), silkPayOrder.getMemberId());
                    AssertUtil.isTrue(booleanMessageRespResult != null && booleanMessageRespResult.isSuccess(), PSMsgCode.REMOTE_SERVICE_FAILED);

                    // 归集手机到指定账号（memberId: 390106）
                    MessageRespResult<SilkDataDist> respResult = silkDataDistApiService.findOne("SILK_PAY_CONFIG", "INCOME_COLLECT_MEMBER_ID");
                    if (respResult != null && respResult.isSuccess() && respResult.getData() != null) {
                        SilkDataDist silkDataDist = respResult.getData();
                        Long memberId = ConvertUtils.lookup(Long.class).convert(Long.class, silkDataDist.getDictVal());
                        MessageRespResult<String> coinIdResult = iMemberWalletApiService.getCoinNameByUnit(silkPayOrder.getCoinId());
                        if (memberId != null && memberId > 0 && coinIdResult.isSuccess() && coinIdResult.getData() != null) {
                            WalletTradeEntity tradeEntity = new WalletTradeEntity();
                            tradeEntity.setType(TransactionType.DIRECT_PAY_PROFIT);
                            tradeEntity.setRefId(String.valueOf(silkPayOrder.getId()));
                            tradeEntity.setChangeType(WalletChangeType.TRADE);
                            tradeEntity.setTradeBalance(silkPayOrder.getAmount());
                            tradeEntity.setMemberId(memberId);
                            tradeEntity.setCoinUnit(silkPayOrder.getCoinId());
                            tradeEntity.setCoinId(coinIdResult.getData());
                            MessageRespResult<Boolean> result = iMemberWalletApiService.trade(tradeEntity);
                            log.info("--归集收益--> {}, {} ==> {}", memberId, silkPayOrder.getAmount(), result);
                        }
                    }

                    SpringContextUtil.getBean(SilkPayMatchRecordService.class).updateStat(silkPayOrder);
                }
                log.info("订单支付成功完成：{}", silkPayOrder);
            }
        }
        // getSilkPayMatchRecordServiceImpl().asyncDistribute(clientId);
    }

    @Async
    public void updateStat(SilkPayOrder silkPayOrder) {
        // 变更次数、金额统计
        silkPayUserConfigMapper.updateStat(silkPayOrder.getMemberId(), silkPayOrder.getMoney());
        SilkPayMatchRecord silkPayMatchRecord = silkPayMatchRecordMapper.selectOne(new QueryWrapper<SilkPayMatchRecord>().eq("order_sn", silkPayOrder.getId()));
        silkPayAccountMapper.updateStat(silkPayMatchRecord.getMatchAccount(), silkPayOrder.getMoney());
    }

    /**
     * 订单支付失败
     *
     * @param mqttPayState
     * @return true
     * @author shenzucai
     * @time 2019.08.05 17:03
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payFailed(MqttPayState mqttPayState) {
        log.info("订单支付失败：{}", mqttPayState);
        //判断订单是否是付款成功或失败
        SilkPayOrder silkPayOrder = silkPayOrderMapper.selectById(mqttPayState.getOrderSn());
        if (silkPayOrder != null) {
            if (silkPayOrder.getState() == 9 || silkPayOrder.getState() == 3) {
                return;
            } else {
                // 进行订单更新
                UpdateWrapper<SilkPayMatchRecord> silkPayMatchRecordUpdateWrapper = new UpdateWrapper<SilkPayMatchRecord>()
                        .set("state", 9)
                        .set("payment_note", mqttPayState.getRemark())
                        .set("payment_order_no", mqttPayState.getPaymentOrderNo())
                        .in("state", 0, 1)
                        .eq("id", mqttPayState.getId())
                        .eq("order_sn", mqttPayState.getOrderSn());
                baseMapper.update(null, silkPayMatchRecordUpdateWrapper);

                QueryWrapper<SilkPayMatchRecord> silkPayMatchRecordQueryWrapper = new QueryWrapper<SilkPayMatchRecord>()
                        .eq("order_sn", mqttPayState.getOrderSn()).eq("state", 1);
                List<SilkPayMatchRecord> silkPayMatchRecords = baseMapper.selectList(silkPayMatchRecordQueryWrapper);
                // 判断是否对订单状态进行更新
                if (silkPayMatchRecords == null || silkPayMatchRecords.size() < 1) {
                    // 条件成立，则对订单状态进行变更
                    UpdateWrapper<SilkPayOrder> silkPayOrderUpdateWrapper = new UpdateWrapper<SilkPayOrder>()
                            .set("state", 9)
                            .set("close_time", mqttPayState.getCurrentTime())
                            .set("remark", mqttPayState.getRemark())
                            .eq("id", mqttPayState.getOrderSn())
                            .in("state", 0, 1, 2);
                    silkPayOrderMapper.update(null, silkPayOrderUpdateWrapper);

                    Long accountId = baseMapper.selectById(mqttPayState.getId()).getMatchAccount();
                    // 额度变化
                    Boolean cancaleBoolean = silkPayAccountService.cancelAccountLimit(accountId, silkPayOrder);
                    AssertUtil.isTrue(cancaleBoolean, CommonMsgCode.SERVICE_UNAVAILABLE);

                    MessageRespResult<Boolean> booleanMessageRespResult = iMemberWalletApiService.tradeTccCancel(silkPayOrder.getMemberId(), Long.valueOf(silkPayOrder.getPaymentNote()));
                    log.info("--取消余额变动---余额变动记录id--{}----用户id---{}", silkPayOrder.getPaymentNote(), silkPayOrder.getMemberId());
                    AssertUtil.isTrue(booleanMessageRespResult != null && booleanMessageRespResult.isSuccess(), PSMsgCode.REMOTE_SERVICE_FAILED);

                }
                log.info("订单支付失败完成：{}", silkPayOrder);
            }

        }
    }

    /**
     * 创建订单下发通信的消息
     *
     * @param silkPayMatchRecord
     * @param silkPayOrder
     * @return true
     * @author shenzucai
     * @time 2019.08.05 14:13
     */
    private MqttPayTask createDistributeTask(SilkPayMatchRecord silkPayMatchRecord, SilkPayOrder silkPayOrder, SilkPayAccount silkPayAccount) {
        MqttPayTask mqttPayTask = new MqttPayTask();
        mqttPayTask.setId(silkPayMatchRecord.getId());
        mqttPayTask.setOrderSn(silkPayMatchRecord.getOrderSn());
        mqttPayTask.setPaymentType(silkPayOrder.getReceiptType());
        mqttPayTask.setPaymentMoney(silkPayMatchRecord.getPaymentMoney());
        mqttPayTask.setPaymentPassword(silkPayAccount.getPayPwd());
        mqttPayTask.setReceiptName(silkPayOrder.getReceiptName());
        mqttPayTask.setReceiptQrCode(silkPayOrder.getReceiptQrCode());
        mqttPayTask.setPaymentNote(silkPayMatchRecord.getPaymentNote());
        mqttPayTask.setCreateTime(silkPayMatchRecord.getCreateTime());
        mqttPayTask.setCurrentTime(new Date());
        return mqttPayTask;
    }

    @Async
    @Override
    public void asyncDistribute(String clientId) {
        log.info("asyncDistribute clientId {}", clientId);
        // 下发该设备的下发中的订单
        SilkPayMatchRecord silkPayMatchRecord = silkPayMatchRecordMapper.findZeroRecords(clientId);
        if (Objects.isNull(silkPayMatchRecord)) {
            return;
        }
        log.info("asyncDistribute silkPayMatchRecord {}", silkPayMatchRecord);
        SilkPayOrder silkPayOrder = silkPayOrderMapper.selectById(silkPayMatchRecord.getOrderSn());
        if (Objects.isNull(silkPayOrder)) {
            return;
        }
        log.info("asyncDistribute silkPayOrder {}", silkPayOrder);
        SilkPayAccount silkPayAccount = silkPayAccountService.getById(silkPayMatchRecord.getMatchAccount());
        if (!Objects.isNull(silkPayMatchRecord) && !Objects.isNull(silkPayOrder) && !Objects.isNull(silkPayAccount)) {
            log.info("asyncDistribute ok {}", clientId);
            MqttPayTask mqttPayTask = createDistributeTask(silkPayMatchRecord, silkPayOrder, silkPayAccount);
            mqttSender.sendToUser(clientId, CommandCode.ORDER_DISTRIBUTE.name(), mqttPayTask);
        }
    }


    SilkPayMatchRecordServiceImpl getSilkPayMatchRecordServiceImpl() {
        return SpringContextUtil.getBean(SilkPayMatchRecordServiceImpl.class);
    }
}