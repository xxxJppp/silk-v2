package com.spark.bitrade.uitl;


import org.springframework.util.StringUtils;

/**
 * @author shenzucai
 * @time 2019.07.26 15:11
 */
public class PayTypeUtil {

    public static Integer getPayType(String content) {
        //0-微信 1-支付宝
        if (StringUtils.isEmpty(content)) {
            return -1;
        } else if (content.toLowerCase().contains("wxp://")) {
            return 0;
        } else if (content.toLowerCase().contains("https://qr.alipay.com/") || content.toLowerCase().contains("https://d.alipay.com/")) {
            return 1;
        } else {
            return -1;
        }
    }
}
