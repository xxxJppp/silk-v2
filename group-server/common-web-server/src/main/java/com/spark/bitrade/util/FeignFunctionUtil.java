package com.spark.bitrade.util;

import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.constants.MsgCode;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * FeignFunctionUtil
 *
 * @author archx
 * @since 2019/6/20 14:42
 */
public class FeignFunctionUtil {

    public static <T> Optional<T> get(Supplier<MessageRespResult<T>> supplier, Consumer<MsgCode> errFunc) {
        MsgCode err;
        try {
            MessageRespResult<T> result = supplier.get();

            if (result.isSuccess()) {
                return Optional.ofNullable(result.getData());
            }
            err = CommonMsgCode.of(result.getCode(), result.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            err = CommonMsgCode.of(500, ex.getMessage());
        }
        errFunc.accept(err);
        return Optional.empty();
    }
}
