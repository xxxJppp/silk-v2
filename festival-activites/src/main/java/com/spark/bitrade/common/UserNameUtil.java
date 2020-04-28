/**
 * silktrader-platform-v2
 * <p>
 * Copyright 2014 Acooly.cn, Inc. All rights reserved.
 *
 * @author SilkTouch
 * @date 2020-01-09 17:57
 */
package com.spark.bitrade.common;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author 舒世平
 * @date 2020-01-09 17:57
 */
@Slf4j
public class UserNameUtil {
    /**
     * 手机号码隐藏
     * @param phone
     * @return
     */
    public String getPassPhone(String phone){
        StringBuilder str = new StringBuilder();
        int lenth = phone.length();
        if(lenth >= 3){
            int endLen = lenth / 3 * 2;
            String s = phone.substring(0,lenth/3);
            str.append(s);
            if(lenth % 3 != 0){
                endLen = endLen + 1;
            }
            for(int i = 0 ;i < lenth - endLen; i++){
                str.append("*");
            }
            s = phone.substring((lenth - (lenth%3 != 0 ? 1:0) - (lenth/3)),lenth);
            str.append(s);
        }else{
            str.append(phone);
        }
        return str.toString();
    }
    /**
     * 邮箱隐藏
     * @param mail
     * @return
     */
    public String getPathMail(String mail){
        mail = mail.replaceAll("(\\w?)(\\w+)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)", "$1****$3$4");
        return mail;
    }
}
