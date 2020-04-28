package com.spark.bitrade.api.vo;

import com.spark.bitrade.entity.constants.WalTradeType;

import java.math.BigDecimal;

/**
 * 转账方向
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/10 15:32
 */
public enum TransferDirectVo {

    NONE(0, "未知"), IN(1, "转入"), OUT(-1, "转出");

    private final int direct;
    private final String desc;

    TransferDirectVo(int direct, String desc) {
        this.direct = direct;
        this.desc = desc;
    }

    /**
     * 转换数量, WAL记录
     *
     * @param amount amount
     * @return amount
     */
    public BigDecimal wal(BigDecimal amount) {
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }
        if (direct > 0) {
            return amount.abs();
        }
        return amount.abs().negate();
    }

    /**
     * 转换数量, Trade主账户记录
     *
     * @param amount amount
     * @return amount
     */
    public BigDecimal trade(BigDecimal amount) {
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }
        if (direct > 0) {
            return amount.abs().negate();
        }
        return amount.abs();
    }

    public int getDirect() {
        return direct;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 可用性验证
     *
     * @param type type
     * @return bool
     */
    public boolean isAvailable(WalTradeType type) {
        if (this == NONE || type == null) {
            return false;
        }

        boolean ret = false;
        switch (type) {
            case TRANSFER:
            case TRANSFER_EXCHANGE:
            case TRANSFER_OTC:
            case TRANSFER_HQB:
                ret = true;
                break;
            default: // false;
        }
        return ret;
    }

    public static TransferDirectVo of(int ordinal) {
        for (TransferDirectVo value : values()) {
            if (value.ordinal() == ordinal) {
                return value;
            }
        }
        return NONE;
    }
}
