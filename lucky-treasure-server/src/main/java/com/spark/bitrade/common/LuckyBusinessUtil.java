package com.spark.bitrade.common;

import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.util.AssertUtil;
import org.apache.shiro.crypto.hash.SimpleHash;

public class LuckyBusinessUtil {


    /**
     * 验证资金密码
     *
     * @param moneyPassword 前端输入的资金密码
     * @param jyPassword    用户设置的资金密码
     */
    public static void validatePassword(String moneyPassword, String jyPassword, String salt) {
        AssertUtil.hasText(moneyPassword, CommonMsgCode.MISSING_JYPASSWORD);
        AssertUtil.hasText(jyPassword, CommonMsgCode.NO_SET_JYPASSWORD);
        String jyPass = new SimpleHash("md5", moneyPassword, salt, 2).toHex().toLowerCase();
        AssertUtil.isTrue(jyPassword.equals(jyPass), CommonMsgCode.ERROR_JYPASSWORD);
    }


}
