package com.spark.bitrade.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.spark.bitrade.api.client.TransferClient;
import com.spark.bitrade.api.vo.TransferDirectVo;
import com.spark.bitrade.constant.WalletType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.constants.ExchangeOrderMsgCode;
import com.spark.bitrade.controller.ApiController;
import com.spark.bitrade.entity.ExchangeWallet;
import com.spark.bitrade.entity.ExchangeWalletSyncRecord;
import com.spark.bitrade.entity.ExchangeWalletWalRecord;
import com.spark.bitrade.service.ExchangeWalletOperations;
import com.spark.bitrade.service.ExchangeWalletService;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.vo.WalletAssetsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 内部接口控制器
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/2 15:00
 */
@RestController
@RequestMapping("/internal")
public class InternalController extends ApiController {

    private TransferClient transferClient;
    private ExchangeWalletOperations walletOperations;
    private ExchangeWalletService walletService;


    /**
     * 手动同步
     *
     * @param memberId id
     * @param coinUnit token
     * @return resp
     */
    @GetMapping("/sync")
    public MessageRespResult<ExchangeWalletSyncRecord> sync(@RequestParam("memberId") Long memberId,
                                                            @RequestParam("coinUnit") String coinUnit) {
        Optional<ExchangeWalletSyncRecord> sync = walletOperations.sync(memberId, coinUnit);
        return sync.map(this::success).orElse(failed());
    }

    /**
     * 内部转账接口
     *
     * @param memberId id
     * @param coinUnit coin
     * @param from     from
     * @param to       to
     * @param amount   amount
     * @return refId
     */
    @RequestMapping("/transfer")
    public MessageRespResult<String> transfer(@RequestParam("memberId") Long memberId,
                                              @RequestParam("coinUnit") String coinUnit,
                                              @RequestParam("from") WalletType from,
                                              @RequestParam("to") WalletType to,
                                              @RequestParam("amount") BigDecimal amount) {
        // 骚操作，自己转自己
        if (from == to) {
            throw ExchangeOrderMsgCode.UNSUPPORTED.asException();
        }

        // 不是币币账户划转请求
        if (from != WalletType.EXCHANGE && to != WalletType.EXCHANGE) {
            throw ExchangeOrderMsgCode.UNSUPPORTED.asException();
        }

        // 与资金账户进行划转
        if (from == WalletType.FUND || to == WalletType.FUND) {

            TransferDirectVo direct = null;
            if (from == WalletType.EXCHANGE) {
                direct = TransferDirectVo.OUT;
            } else {
                direct = TransferDirectVo.IN;
            }

            // 发起划转
            ExchangeWalletWalRecord record = transferClient.transfer(memberId, coinUnit, amount, direct);
            return success(record.getRefId());
        }

        // TODO 其他账户划转支持
        return failed(CommonMsgCode.FAILURE);
    }

    /**
     * 查询钱包余额
     *
     * @param memberId 会员ID
     * @param coinUnit 币种
     * @return balance
     */
    @GetMapping("/balance")
    public MessageRespResult<ExchangeWallet> balance(@RequestParam("memberId") Long memberId,
                                                     @RequestParam("coinUnit") String coinUnit) {
        Optional<ExchangeWallet> balance = walletOperations.balance(memberId, coinUnit);
        return balance.map(this::success).orElse(this.failed());
    }

    /**
     * 资产列表
     *
     * @param memberId 会员id
     * @return list
     */
    @GetMapping("/assets")
    public MessageRespResult<List<WalletAssetsVo>> assets(@RequestParam("memberId") Long memberId) {

        QueryWrapper<ExchangeWallet> query = new QueryWrapper<>();
        query.eq("member_id", memberId);

        List<ExchangeWallet> list = walletService.list(query);

        if (list == null || list.isEmpty()) {
            return success(Collections.emptyList());
        }

        // 转换
        List<WalletAssetsVo> collect = list.stream().map(wallet -> {
            WalletAssetsVo vo = new WalletAssetsVo();
            vo.setMemberId(wallet.getMemberId());
            vo.setCoinUnit(wallet.getCoinUnit());
            vo.setBalance(wallet.getBalance());
            vo.setFrozen(wallet.getFrozenBalance());
            vo.setLocked(BigDecimal.ZERO);
            return vo;
        }).collect(Collectors.toList());

        return success(collect);
    }

    @Autowired
    public void setTransferClient(TransferClient transferClient) {
        this.transferClient = transferClient;
    }

    @Autowired
    public void setWalletOperations(ExchangeWalletOperations walletOperations) {
        this.walletOperations = walletOperations;
    }

    @Autowired
    public void setWalletService(ExchangeWalletService walletService) {
        this.walletService = walletService;
    }

}
