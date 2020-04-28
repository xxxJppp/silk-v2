package com.spark.bitrade;

import cn.hutool.core.util.StrUtil;
import com.spark.bitrade.jwt.HttpJwtToken;
import com.spark.bitrade.jwt.MemberClaim;
import com.spark.bitrade.service.impl.SecurityServiceImpl;
import com.spark.bitrade.util.DesECBUtil;
import com.spark.bitrade.util.MD5Util;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;

/**
 *  请求加密测试
 *
 * @author yangch
 * @time 2019.05.07 19:34
 */
public class SecurityEncodeTest {
    public static void main(String[] args) throws Exception {
        //String apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ4eHh4IiwiYXVkIjoiMTIzIiwiaXNzIjoidWNlbnRlci1hcGkiLCJleHAiOjE1NjA4MzY5ODMsInVzZXJJZCI6MSwiaWF0IjoxNTYwODM2OTgzLCJ1c2VybmFtZSI6IjEifQ.6f21qfgSp21movs83S_YNt04AnOysqNlZCguAZkcIgg";
        String apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ4eHh4IiwiYXVkIjoiMTIzIiwiaXNzIjoidWNlbnRlci1hcGkiLCJleHAiOjE1NjIxMzQ0NzcsInVzZXJJZCI6NzE2MzksImlhdCI6MTU2MDgzODQ3NywidXNlcm5hbWUiOiIxIn0.bqZsADX1mxSo7i2FJf1CUiFsPQohe7idI5qPJFIVZ8w";
        Long time = System.currentTimeMillis();
        //应用ID
        String appId="1";

        SecurityServiceImpl securityService = new SecurityServiceImpl();
        MemberClaim claim = HttpJwtToken.getInstance().verifyToken(apiKey);

        String apiSecret = securityService.apiSecretArithmetic(apiKey, claim.userId);
        String apkSecretSalt = claim.userId.toString();


        //1、请求时间，按需修改
        String inTime = readData("输入请求时间戳[可选]：");
        if (StringUtils.hasText(inTime)) {
            time = Long.parseLong(inTime);
        }

        //加解密=MD5(apiKey)
        //String encryptionKey = MD5Util.md5Encode(apkSecretSalt.concat(apiKey));
        String encryptionKey = StrUtil.reverse(MD5Util.md5Encode(apiKey));
        System.out.println("encryptionKey=" + encryptionKey);

        //2、请求参数，按需修改
//        String encryptData = "id=1&key1=valu1";
        String encryptData = "";
//        String encryptData = "";
        String inEncryptData = readData("输入请求参数[可选]：");
        if (StringUtils.hasText(inEncryptData)) {
            encryptData = inEncryptData;
        }


        //请求的加密数据(并url编码)
        String requstData = DesECBUtil.encryptDES(encryptData, encryptionKey);

        //签名 sign=Md5(apiKey+time+apiSecret2+请求数据)
        String apiSecret2 = MD5Util.md5Encode(apkSecretSalt.concat(apiSecret));
        String signSourceData = appId.concat(apiKey).concat(time.toString()).concat(apiSecret2);
        if (StringUtils.isEmpty(encryptData)) {
            signSourceData = signSourceData.concat("");
        } else {
            signSourceData = signSourceData.concat(requstData);
        }

        System.out.println("\n\n一：参考数据");
        System.out.println("1）apiSecret=" + apiSecret);
        System.out.println("2）apiSecret2=" + apiSecret2);
        System.out.println("3）signSourceData=" + signSourceData);
        System.out.println("4）原始请求数据:" + encryptData);
        System.out.println("4) 请求内容加密(未URL编码)：" + requstData);

        System.out.println("\n\n二：请求头数据");
        System.out.println("1.[apiKey]:" + apiKey);
        System.out.println("2.[apiTime]:" + time);
        System.out.println("3.[apiSign]：" + MD5Util.md5Encode(signSourceData));
        System.out.println("\n三：请求数据加密(URL编码)");
        System.out.println("4.data=" + URLEncoder.encode(requstData, "UTF-8"));

    }

    public static String readData(String tip) {
        if (StringUtils.hasText(tip)) {
            System.out.println(tip);
        } else {
            System.out.println("Please Enter Data:");
        }

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
