package com.spark.bitrade;

import java.text.MessageFormat;

/**
 *  
 *
 * @author yangch
 * @time 2019.05.08 20:35
 */
public class Test {
    public static void main(String[] args) {
//        String qrCode = "http://silktraderpriv.oss-cn-hongkong.aliyuncs.com/2019/05/08/55f85f34-7bf6-4bd3-a8e4-d2cd989c14d3.jpg?Expires=1557302406&OSSAccessKeyId=LTAIgslrcDq69ahL&Signature=s%2FKWnL2K7zYwr%2F%2BPSn5p7OPvlno%3D";
//        System.out.println(qrCode.split("[?]")[0].split("[|/]", 4)[3]);

        // 格式化
        int age = 1273;
        String name = "My";
        Object[] testArgs = {new Long(age), name};
//        MessageFormat form = new MessageFormat(" {1} {0}");
//        System.out.println(form.format(testArgs));

        System.out.println(MessageFormat.format(" {0}", null));
        System.out.println(MessageFormat.format(" {0}", name));
        System.out.println(MessageFormat.format(" {1} {0}", testArgs));
    }
}
