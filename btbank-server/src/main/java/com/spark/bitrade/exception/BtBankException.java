package com.spark.bitrade.exception;

import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.constants.MsgCode;
import com.spark.bitrade.enums.MessageCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author davi
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString
public class BtBankException extends RuntimeException {
    private static final long serialVersionUID = -7231025668652771604L;

    private String msg;
    private int code = 500;

    public BtBankException(int code) {
        this.code = 500;
    }

    public BtBankException(MessageCode code) {
        this.code = code.getCode();
        this.msg = code.getDesc();
    }

    public BtBankException(MsgCode code) {
        this.code = code.getCode();
        this.msg = code.getMessage();
    }

    public BtBankException(BtBankMsgCode code) {
        this.code = code.getCode();
        this.msg = code.getMessage();
    }

    public BtBankException(String msg) {
        this.code = 500;
        this.msg = msg;
    }

    public BtBankException(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return this.toString();
    }
}
