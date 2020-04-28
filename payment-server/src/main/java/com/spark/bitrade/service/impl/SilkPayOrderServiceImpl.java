package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constants.CommandCode;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.constants.PSMsgCode;
import com.spark.bitrade.entity.*;
import com.spark.bitrade.entity.vo.SilkPayOrderVo;
import com.spark.bitrade.mapper.SilkPayAccountMapper;
import com.spark.bitrade.mapper.SilkPayOrderMapper;
import com.spark.bitrade.service.ICoinExchange;
import com.spark.bitrade.service.IMemberWalletApiService;
import com.spark.bitrade.service.SilkPayMatchRecordService;
import com.spark.bitrade.service.SilkPayOrderService;
import com.spark.bitrade.uitl.PayTypeUtil;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.MessageRespResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * 支付订单(SilkPayOrder)表服务实现类
 *
 * @author wsy
 * @since 2019-07-18 10:39:01
 */
@Service("silkPayOrderService")
public class SilkPayOrderServiceImpl extends ServiceImpl<SilkPayOrderMapper, SilkPayOrder> implements SilkPayOrderService {

    @Autowired
    private ICoinExchange iCoinExchange;

    @Autowired
    private IMemberWalletApiService iMemberWalletApiService;

    @Autowired
    private SilkPayMatchRecordService silkPayMatchRecordService;

    @Autowired
    private SilkPayAccountMapper silkPayAccountMapper;
    /**
     * @param gpsLocation    位置（经纬度）
     * @param silkPayCoin 配置币种
     * @param member         用户
     * @param receiptContent 接收的二维码内容
     * @param receiptName    收款人名称
     * @param amount         金额
     * @param unit           币种
     * @return true
     * @author shenzucai
     * @time 2019.07.30 16:03
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SilkPayOrder createOrder(GpsLocation gpsLocation,SilkPayCoin silkPayCoin, SilkPayUserConfig userConfig, Member member, String receiptContent, String receiptName, BigDecimal amount, String unit) {
        //进行解限操作
        silkPayAccountMapper.resetSurplus();
        // 创建订单
        SilkPayOrder silkPayOrder = new SilkPayOrder();
        silkPayOrder.setId(IdWorker.getId());
        silkPayOrder.setMemberId(member.getId());
        silkPayOrder.setMemberName(member.getUsername());
        silkPayOrder.setMemberRealName(member.getRealName());
        silkPayOrder.setCoinId(unit);
        silkPayOrder.setMoney(amount);
        silkPayOrder.setReceiptType(PayTypeUtil.getPayType(receiptContent));
        silkPayOrder.setReceiptName(receiptName);
        silkPayOrder.setReceiptQrCode(receiptContent);
        if(!Objects.isNull(gpsLocation)) {
            silkPayOrder.setPaymentLocation(gpsLocation.toString());
        }
        silkPayOrder.setState(0);
        silkPayOrder.setHandleType(0);

        // 获取当前币种的cny价值
        MessageRespResult<BigDecimal>  bigDecimalMessageRespResult = iCoinExchange.getCnyExchangeRate(StringUtils.lowerCase(unit));
        AssertUtil.isTrue(bigDecimalMessageRespResult != null && bigDecimalMessageRespResult.isSuccess(), PSMsgCode.GET_RATE_FAILED);
        BigDecimal marketPrice = bigDecimalMessageRespResult.getData().setScale(silkPayCoin.getScale(),BigDecimal.ROUND_DOWN);

        // 费率下调计算(价格降低 - 低价买入)
        BigDecimal rate = BigDecimal.ONE.subtract(silkPayCoin.getRateReductionFactor());
        BigDecimal price = marketPrice.multiply(rate);

        // 对应币的数量，价格舍，数量增
        BigDecimal coinAmount = amount.divide(price,silkPayCoin.getScale(),BigDecimal.ROUND_UP);

        // 收益币数量
        BigDecimal profitAmount = coinAmount.subtract(amount.divide(marketPrice, silkPayCoin.getScale(), BigDecimal.ROUND_UP));

        // 判断用户当前余额是否足够
        MessageRespResult<MemberWallet> walletMessageRespResult = iMemberWalletApiService.getWalletByUnit(member.getId(),unit);
        AssertUtil.isTrue(walletMessageRespResult != null && walletMessageRespResult.isSuccess(),PSMsgCode.REMOTE_SERVICE_FAILED);
        AssertUtil.isTrue(coinAmount.compareTo(walletMessageRespResult.getData().getBalance()) != 1, PSMsgCode.BALANCE_NOT_ENOUGH);
        silkPayOrder.setAmount(coinAmount);
        silkPayOrder.setProfitAmount(profitAmount);
        silkPayOrder.setRateReductionFactor(silkPayCoin.getRateReductionFactor());
        silkPayOrder.setDealPrice(price);
        silkPayOrder.setMarketPrice(marketPrice);
        silkPayOrder.setCreateTime(new Date());
        // 保存创建的订单
        AssertUtil.isTrue(baseMapper.insert(silkPayOrder)>0,PSMsgCode.BUILD_ORDER_FAILED);
        // 2, 进行付款匹配（订单金额，地理位置，付款方式）及付款拆分,用户资金预变动
        Boolean aBoolean = silkPayMatchRecordService.payOrSplitOrderMatchRecord(silkPayOrder, gpsLocation);
        AssertUtil.isTrue(aBoolean, PSMsgCode.BUILD_ORDER_FAILED);
        return silkPayOrder;
    }

    @Override
    public SilkPayOrderVo getByOrderSn(String order_sn) {
        return this.baseMapper.selectByOrderSn(order_sn);
    }
}