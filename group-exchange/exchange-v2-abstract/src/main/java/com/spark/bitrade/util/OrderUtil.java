package com.spark.bitrade.util;

import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.constants.ExchangeOrderMsgCode;
import com.spark.bitrade.entity.constants.ExchangeConstants;

import java.util.regex.Pattern;

/**
 *  
 *
 * @author young
 * @time 2019.12.17 14:03
 */
public class OrderUtil {
    final static String REGEX_ORDERID_FORMAT = "[A-Z]\\d+";

    public static void checkOrderIdFormat(String orderId) {
        //订单格式：E1168423154092716041  R716391206783830510272513
        AssertUtil.notNull(orderId, CommonMsgCode.INVALID_PARAMETER);

        if (!Pattern.matches(REGEX_ORDERID_FORMAT, orderId)) {
            ExceptionUitl.throwsMessageCodeException(ExchangeOrderMsgCode.BAD_ORDER);
        }
    }
}
