package com.spark.bitrade;

import cn.hutool.core.util.StrUtil;
import com.spark.bitrade.util.DesECBUtil;
import com.spark.bitrade.util.MD5Util;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *  响应数据解密测试
 *
 * @author yangch
 * @time 2019.05.07 19:34
 */
public class SecurityDecodeTest {
    public static void main(String[] args) throws Exception {
        String apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ4eHh4IiwiYXVkIjoiMTIzIiwiaXNzIjoidWNlbnRlci1hcGkiLCJleHAiOjE1NjIxMzQ0NzcsInVzZXJJZCI6NzE2MzksImlhdCI6MTU2MDgzODQ3NywidXNlcm5hbWUiOiIxIn0.bqZsADX1mxSo7i2FJf1CUiFsPQohe7idI5qPJFIVZ8w";

        String encryptionKey = StrUtil.reverse(MD5Util.md5Encode(apiKey));
        System.out.println("encryptionKey=" + encryptionKey);

        //3、解密，按需修改
        String responseData = "JJKtHfgxPHk07/aS612w44ghluJ3xn1SXxBY0zaxMIoyZVfCRHOl8wQnVbRjLcXwpwjGkgJOUV8=";
        String inData = readData();
        if (StringUtils.hasText(inData)) {
            responseData = inData;
        }

        System.out.println("响应内容解密：" + DesECBUtil.decryptDES(responseData, encryptionKey));


    }

    public static String readData() {
        System.out.println("Please Enter Decode Data:");
        InputStreamReader is = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(is);
        try {
            String inData = br.readLine();
            System.out.println("input data:" + inData);
            return inData;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
