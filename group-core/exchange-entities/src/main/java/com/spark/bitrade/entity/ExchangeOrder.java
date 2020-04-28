package com.spark.bitrade.entity;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constant.ExchangeOrderStatus;
import com.spark.bitrade.constant.ExchangeOrderType;
import com.spark.bitrade.constant.OrderValidateStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.beans.Transient;
import java.math.BigDecimal;
import java.util.List;

/**
 * 币币交易订单表实体类
 *
 * @author yangch
 * @since 2019-09-02 11:23:45
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "币币交易订单表")
public class ExchangeOrder {

    /**
     * 订单号
     */
    @TableId
    @ApiModelProperty(value = "订单号", example = "")
    private String orderId;

    /**
     * 会员ID
     */
    @ApiModelProperty(value = "会员ID", example = "")
    private Long memberId;

    /**
     * 交易数量
     */
    @ApiModelProperty(value = "交易数量", example = "")
    private BigDecimal amount;

    /**
     * 订单方向 0买，1卖
     */
    @ApiModelProperty(value = "订单方向 0买，1卖", example = "")
    private ExchangeOrderDirection direction;

    /**
     * 挂单价格
     */
    @ApiModelProperty(value = "挂单价格", example = "")
    private BigDecimal price;

    /**
     * 交易对
     */
    @ApiModelProperty(value = "交易对", example = "")
    private String symbol;

    /**
     * 挂单类型，0市价，1限价
     */
    @ApiModelProperty(value = "挂单类型，0市价，1限价", example = "")
    private ExchangeOrderType type;

    /**
     * 交易币
     */
    @ApiModelProperty(value = "交易币", example = "")
    private String coinSymbol;

    /**
     * 结算币
     */
    @ApiModelProperty(value = "结算币", example = "")
    private String baseSymbol;

    /**
     * 订单状态 0交易，1完成，2取消，3超时
     */
    @ApiModelProperty(value = "订单状态 0交易，1完成，2取消，3超时", example = "")
    private ExchangeOrderStatus status;

    /**
     * 下单时间
     */
    @ApiModelProperty(value = "下单时间", example = "")
    private Long time;

    /**
     * 交易完成时间
     */
    @ApiModelProperty(value = "交易完成时间", example = "")
    private Long completedTime;

    /**
     * 交易取消时间
     */
    @ApiModelProperty(value = "交易取消时间", example = "")
    private Long canceledTime;

    /**
     * 成交量
     */
    @ApiModelProperty(value = "成交量", example = "")
    private BigDecimal tradedAmount;

    /**
     * 成交额，对市价买单有用
     */
    @ApiModelProperty(value = "成交额，对市价买单有用", example = "")
    private BigDecimal turnover;

    /**
     * 买入或卖出量 对应的 冻结币数量
     */
    @ApiModelProperty(value = "买入或卖出量 对应的 冻结币数量", example = "")
    private BigDecimal freezeAmount;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    /**
     * 校验状态
     */
    @ApiModelProperty(value = "校验状态：0 未校验， 1 校验通过， 2 校验失败", example = "")
    private OrderValidateStatus validated;

    public boolean completed() {
        if (status != ExchangeOrderStatus.TRADING) {
            return true;
        } else {
            if (type == ExchangeOrderType.MARKET_PRICE && direction == ExchangeOrderDirection.BUY) {
                return amount.compareTo(turnover) <= 0;
            } else {
                return amount.compareTo(tradedAmount) <= 0;
            }
        }
    }
}