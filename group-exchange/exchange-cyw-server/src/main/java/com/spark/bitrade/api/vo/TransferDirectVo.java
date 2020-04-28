package com.spark.bitrade.api.vo;

import java.math.BigDecimal;

/**
 * 转账方向
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/10 15:32
 */
public enum TransferDirectVo {
    IN(1, "转入"), OUT(-1, "转出");

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
}
