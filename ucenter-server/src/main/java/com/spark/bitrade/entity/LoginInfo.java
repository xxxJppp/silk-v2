package com.spark.bitrade.entity;

import com.spark.bitrade.constant.BooleanStringEnum;
import com.spark.bitrade.constant.MemberLevelEnum;
import lombok.Data;

/**
 * @author Zhang Jinwei
 * @date 2018年01月31日
 */
@Data
public class LoginInfo {
    private String username;
    private Location location;
    private MemberLevelEnum memberLevel;
    private String token;
    private String accessToken;
    private String realName;
    private Country country;
    private String avatar;
    private String promotionCode;
    private long id;
    /**
     * 手机号
     */
    private String phone;
    private String email;
    /**
     * 是否开启手机认证
     */
    private BooleanStringEnum isOpenPhoneLogin = BooleanStringEnum.IS_FALSE;
    /**
     * 是否开启google认证
     */
    private BooleanStringEnum isOpenGoogleLogin = BooleanStringEnum.IS_FALSE;
    /**
     * 是否开启手机提币认证
     */
    private BooleanStringEnum isOpenPhoneUpCoin = BooleanStringEnum.IS_FALSE;
    /**
     * 是否开启google提币认证
     */
    private BooleanStringEnum isOpenGoogleUpCoin = BooleanStringEnum.IS_FALSE;

}
