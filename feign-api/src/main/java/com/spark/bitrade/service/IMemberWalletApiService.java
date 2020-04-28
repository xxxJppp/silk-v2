package com.spark.bitrade.service;

import com.spark.bitrade.constant.FeignServiceConstant;
import com.spark.bitrade.entity.MemberWallet;
import com.spark.bitrade.entity.WalletChangeRecord;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.trans.WalletExchangeEntity;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.vo.MemberWalletCountVo;
import com.spark.bitrade.vo.MemberWalletVo;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

/**
 * 提供账户 API服务
 *
 * @author yangch
 * @time 2019.06.20 11:35
 */
@FeignClient(FeignServiceConstant.ACCOUNT_SERVER)
public interface IMemberWalletApiService {

    /**
     * 个人钱包账户资金变动接口，如个人账户的平账、冻结、解冻等
     *
     * @param tradeEntity 个人账户交易信息
     * @return
     * @author yangch
     * @time 2019.06.20 11:45
     **/
    @PostMapping(value = "/acct/v2/memberWallet/trade")
    MessageRespResult<Boolean> trade(@RequestBody WalletTradeEntity tradeEntity);


    /**
     * tcc try预留业务资源接口
     * 备注：预处理账的可用数量（try处理时需要将可用资金放到到冻结资金里，冻结和锁仓资金不做特殊处理），记录资产变更流水记录
     * 1）交易可用金额
     * 1.1）交易的可用金额大于0，预处理时，放到 冻结资金里
     * 1.2）交易的可用金额小于0，预处理时，先从 可用资金 中减去交易的可用金额，同时 放到 冻结资金里
     * 2）资产变更流水记录
     * 2.1）tcc状态，默认为 1=try
     *
     * @param tradeEntity 交易实体信息
     * @return
     * @throws MessageCodeException
     */
    @PostMapping("/acct/v2/memberWallet/tradeTccTry")
    MessageRespResult<WalletChangeRecord> tradeTccTry(@RequestBody WalletTradeEntity tradeEntity);

    /**
     * tcc Confirm 确认执行业务操作
     * 备注：处理冻结资金，修改流水记录状态，新增用户资产流水记录
     * 1）冻结资金
     * 1.1）交易的可用金额大于0，确认执行业务时，减少 冻结资产 同时 添加 可用资产
     * 1.2）交易的可用金额小于0，确认执行业务时，减少 冻结资产
     * 2）修改 资产变更流水记录 tcc状态 为 2=confirm
     * 3）保存 用户资产流水记录
     *
     * @param memberId             用户ID
     * @param walletChangeRecordId 资产变更流水记录ID
     * @return
     * @throws MessageCodeException
     */
    @PostMapping("/acct/v2/memberWallet/tradeTccConfirm")
    MessageRespResult<Boolean> tradeTccConfirm(@RequestParam("memberId") Long memberId,
                                               @RequestParam("walletChangeRecordId") Long walletChangeRecordId);

    /**
     * tcc 取消执行业务操作
     * 备注：数据还原，修改资金变更流水记录状态
     * 1）交易的 可用资金、冻结资金、锁仓资金正常还回
     * 2）修改 资产变更流水记录 tcc状态 为 3=cancel
     *
     * @param memberId             用户ID
     * @param walletChangeRecordId 资产变更流水记录ID
     * @return
     * @throws MessageCodeException
     */
    @PostMapping("/acct/v2/memberWallet/tradeTccCancel")
    MessageRespResult<Boolean> tradeTccCancel(@RequestParam("memberId") Long memberId,
                                              @RequestParam("walletChangeRecordId") Long walletChangeRecordId);

    /**
     * 个人币种兑换接口，如币币交易、闪兑等
     *
     * @param exchangeEntity 个人账户兑换信息
     * @return
     * @author yangch
     * @time 2019-06-25 09:39:41
     **/
    @PostMapping(value = "/acct/v2/memberWallet/exchange")
    MessageRespResult<Boolean> exchange(@RequestBody WalletExchangeEntity exchangeEntity);

    /**
     * 根据传入的用户和币种id获取钱包信息
     *
     * @param memberId
     * @param coinId
     * @return true
     * @author shenzucai
     * @time 2019.07.03 20:33
     */
    @GetMapping(value = "/acct/v2/memberWallet/getWallet")
    MessageRespResult<MemberWallet> getWallet(@RequestParam("memberId") Long memberId, @RequestParam("coinId") String coinId);

    /**
     * 根据传入的用户和币种id获取钱包信息
     *
     * @param memberId
     * @param coinId
     * @return true
     * @author shenzucai
     * @time 2019.07.03 20:33
     */
    @GetMapping(value = "/acct/v2/memberWallet/getWalletByUnit")
    MessageRespResult<MemberWallet> getWalletByUnit(@RequestParam("memberId") Long memberId, @RequestParam("unit") String coinId);


    /**
     * 根据传入的币种获取币种id
     *
     * @param unit
     * @return true
     * @author shenzucai
     * @time 2019.07.03 20:33
     */
    @GetMapping(value = "/acct/v2/coin/getCoinNameByUnit")
    MessageRespResult<String> getCoinNameByUnit(@RequestParam("unit") String unit);

    @GetMapping(value = "/acct/v2/memberWallet/getSupportCoinByMemberId")
    MessageRespResult<List<MemberWalletVo>>  getSupportCoinByMemberId(@RequestParam("memberId") Long memberId);

    /**
     * 项目方持仓统计
     *
     * @param coinId
     * @param balanceStart  持仓币数下限
     * @param balanceEnd    持仓币数上限
     * @return
     */
    @GetMapping(value = "/acct/v2/memberWallet/getCountMemberWallet")
    MessageRespResult<MemberWalletCountVo> getCountMemberWallet(@RequestParam("coinId") String coinId,
                                                                @RequestParam("balanceStart") BigDecimal balanceStart,
                                                                @RequestParam("balanceEnd") BigDecimal balanceEnd,
                                                                @RequestParam("page") Integer page,
                                                                @RequestParam("pageSize") Integer pageSize);



}
