package com.spark.bitrade.util;

import com.spark.bitrade.constants.MsgCode;
import com.spark.bitrade.exception.MessageCodeException;

/**
 *  异常工具类
 *
 * @author young
 * @time 2019.05.09 17:09
 */
public class ExceptionUitl {
    /**
     * 抛出 MessageCodeException异常
     *
     * @param msgCode 错误码
     */
    public static void throwsMessageCodeException(MsgCode msgCode) {
        throw new MessageCodeException(msgCode);
    }

    /**
     * MessageRespResult的结果不成功，则抛出异常
     *
     * @param respResult MessageCodeException异常
     */
    public static void  throwsMessageCodeExceptionIfFailed(MessageRespResult respResult) {
        if (!respResult.isSuccess()) {
            throw newMessageException(respResult);
        }
    }

    /**
     * 构建 MessageCodeException
     * <p>
     * 便于 IDEA 进行提示优化
     *
     * @param msgCode msgCode
     * @return ex
     */
    public static MessageCodeException newMessageException(MsgCode msgCode) {
        return new MessageCodeException(msgCode);
    }

    /**
     * 构建 MessageCodeException
     * <p>
     * 便于 IDEA 进行提示优化
     *
     * @param resp resp
     * @return ex
     */
    public static MessageCodeException newMessageException(final MessageRespResult resp) {
        return new MessageCodeException(new MsgCode() {
            @Override
            public int getCode() {
                return resp.getCode();
            }

            @Override
            public String getMessage() {
                return resp.getMessage();
            }
        });
    }

    /**
     * 构建 MessageCodeException
     * <p>
     * 便于 IDEA 进行提示优化
     *
     * @param code;
     * @param message;
     * @return ex
     */
    public static MessageCodeException newMessageException(final int code, final String message) {
        return new MessageCodeException(new MsgCode() {
            @Override
            public int getCode() {
                return code;
            }

            @Override
            public String getMessage() {
                return message;
            }
        });
    }

}
