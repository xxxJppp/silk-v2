package com.spark.bitrade.api;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.constants.ExchangeCywMsgCode;
import com.spark.bitrade.controller.ApiController;
import com.spark.bitrade.entity.CywWallet;
import com.spark.bitrade.entity.CywWalletWalRecord;
import com.spark.bitrade.entity.constants.CywLockStatus;
import com.spark.bitrade.entity.constants.CywProcessStatus;
import com.spark.bitrade.entity.constants.WalTradeType;
import com.spark.bitrade.service.CywWalletOperations;
import com.spark.bitrade.service.CywWalletService;
import com.spark.bitrade.uitl.CywWalletUtils;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.BigDecimalUtil;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Optional;

/**
 * 内部接口控制器
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/2 15:00
 */
@RestController
@RequestMapping("/internal")
public class InternalController extends ApiController {

    private CywWalletService walletService;
    private CywWalletOperations walletOperations;

    /**
     * 查询钱包余额
     *
     * @param memberId 会员ID
     * @param coinUnit 币种
     * @return balance
     */
    @GetMapping("/balance")
    public MessageRespResult<BigDecimal> balance(@RequestParam("memberId") Long memberId,
                                                 @RequestParam("coinUnit") String coinUnit) {
        Optional<BigDecimal> balance = walletOperations.balance(memberId, coinUnit);
        return success(balance.orElse(BigDecimal.ZERO));
    }

    /**
     * 转入
     *
     * @param memberId 会员ID
     * @param coinUnit 币种
     * @param amount   数量
     * @param refId    关联的转账业务id
     * @return resp
     */
    @PostMapping("/ex_cyw/internal/transIn")
    MessageRespResult<CywWalletWalRecord> transferIn(@RequestParam("memberId") Long memberId,
                                                     @RequestParam("coinUnit") String coinUnit,
                                                     @RequestParam("amount") BigDecimal amount,
                                                     @RequestParam("refId") String refId) {

        CywWalletWalRecord record = new CywWalletWalRecord();

        AssertUtil.isTrue(BigDecimalUtil.gt0(amount), ExchangeCywMsgCode.ILLEGAL_TRANS_AMOUNT);

        return doTransfer(memberId, coinUnit, amount, refId, record, "转入");
    }

    /**
     * 转出
     *
     * @param memberId 会员ID
     * @param coinUnit 币种
     * @param amount   数量
     * @param refId    关联的转账业务id
     * @return resp
     */
    @PostMapping("/ex_cyw/internal/transOut")
    MessageRespResult<CywWalletWalRecord> transferOut(@RequestParam("memberId") Long memberId,
                                                      @RequestParam("coinUnit") String coinUnit,
                                                      @RequestParam("amount") BigDecimal amount,
                                                      @RequestParam("refId") String refId) {
        CywWalletWalRecord record = new CywWalletWalRecord();

        // 转为负数
        amount = CywWalletUtils.negativeOf(amount);

        return doTransfer(memberId, coinUnit, amount, refId, record, "转出");
    }

    private MessageRespResult<CywWalletWalRecord> doTransfer(Long memberId, String coinUnit, BigDecimal amount,
                                                             String refId, CywWalletWalRecord record, String remark) {

        Optional<CywWallet> optional = walletService.findOne(memberId, coinUnit);

        // 钱包不存在
        if (!optional.isPresent()) {
            throw ExchangeCywMsgCode.WALLET_NOT_FOUNT.asException();
        }

        // 账户已锁定
        if (optional.get().getIsLock() != CywLockStatus.UNLOCK) {
            throw ExchangeCywMsgCode.WALLET_LOCKED.asException();
        }

        record.setId(IdWorker.getId());
        record.setMemberId(memberId);
        record.setCoinUnit(coinUnit);
        record.setRefId(refId);
        record.setTradeBalance(amount);
        record.setTradeType(WalTradeType.TRANSFER);
        record.setStatus(CywProcessStatus.NOT_PROCESSED);
        record.setRemark(remark);
        record.setCreateTime(Calendar.getInstance().getTime());

        Optional<CywWalletWalRecord> booking = walletOperations.booking(record);

        if (booking.isPresent()) {
            return success(booking.get());
        }
        return failed(CommonMsgCode.FAILURE);
    }

    @Autowired
    public void setWalletService(CywWalletService walletService) {
        this.walletService = walletService;
    }

    @Autowired
    public void setWalletOperations(CywWalletOperations walletOperations) {
        this.walletOperations = walletOperations;
    }
}
