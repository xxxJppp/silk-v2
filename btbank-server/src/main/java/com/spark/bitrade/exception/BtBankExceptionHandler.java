package com.spark.bitrade.exception;

import com.spark.bitrade.util.MessageRespResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author davi
 */
@Slf4j
@RestControllerAdvice
public class BtBankExceptionHandler {

    @ExceptionHandler(BtBankException.class)
    public MessageRespResult handleBtBankException(BtBankException e) {
        log.error("handleBtBankException:", e);
        return MessageRespResult.error(e.getCode(), e.getMsg());
    }
}
