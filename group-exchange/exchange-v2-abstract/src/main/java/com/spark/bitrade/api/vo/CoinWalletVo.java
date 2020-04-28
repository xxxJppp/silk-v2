package com.spark.bitrade.api.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spark.bitrade.entity.Coin;
import com.spark.bitrade.entity.ExchangeWallet;
import com.spark.bitrade.entity.constants.ExchangeLockStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * CoinWalletVo
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/11/21 14:18
 */
@Data
@ApiModel(description = "币币交易钱包视图对象")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoinWalletVo {

    /**
     * ID
     */
    @ApiModelProperty(value = "ID", example = "")
    private String id;

    /**
     * 钱包地址
     */
    @ApiModelProperty(value = "钱包地址", example = "")
    private String address;

    /**
     * 余额
     */
    @ApiModelProperty(value = "余额", example = "")
    private BigDecimal balance;

    /**
     * 冻结余额
     */
    @ApiModelProperty(value = "冻结余额", example = "")
    private BigDecimal frozenBalance;

    /**
     * 会员ID
     */
    @ApiModelProperty(value = "会员ID", example = "")
    private Long memberId;

    /**
     * 币种
     */
    @ApiModelProperty(value = "币种", example = "")
    private String coinUnit;

    /**
     * 是否锁定
     */
    @ApiModelProperty(value = "是否锁定", example = "")
    private ExchangeLockStatus isLock;

    /**
     * 币种对象
     */
    @ApiModelProperty(value = "币种对象", example = "")
    private Coin coin;

    /**
     * 构建VO
     *
     * @param coin 币种
     * @return vo
     */
    public static CoinWalletVo of(Coin coin) {
        CoinWalletVo vo = new CoinWalletVo();
        vo.setCoin(coin);

        vo.setBalance(BigDecimal.ZERO);
        vo.setFrozenBalance(BigDecimal.ZERO);
        vo.setCoinUnit(coin.getUnit());

        return vo;
    }

    /**
     * 拷贝属性
     *
     * @param wallet 钱包
     * @return vo
     */
    public CoinWalletVo copy(ExchangeWallet wallet) {
        this.setId(wallet.getId());
        this.setAddress(wallet.getAddress());
        this.setBalance(wallet.getBalance());
        this.setFrozenBalance(wallet.getFrozenBalance());
        this.setMemberId(wallet.getMemberId());
        this.setCoinUnit(wallet.getCoinUnit());
        this.setIsLock(wallet.getIsLock());
        return this;
    }
}
