package com.spark.bitrade.entity;

import com.spark.bitrade.constant.LoginType;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @since 2018年01月08日
 */
@Data
public class LoginByPhone {
    /**
     * 大陆手机号码11位数，匹配格式：前三位固定格式+后8位任意数
     * 此方法中前三位格式有：
     * 13+任意数
     * 15+除4的任意数
     * 18+任意数
     * 17+任意数
     * 147
     */
    @NotBlank(message = "{LoginByPhone.phone.null}")
    private String phone;

    @NotBlank(message = "{LoginByPhone.password.null}")
    private String password;

    @NotBlank(message = "{LoginByPhone.username.null}")
    @Length(min = 3, max = 64, message = "{LoginByPhone.username.length}")
    private String username;

    @NotBlank(message = "{LoginByPhone.country.null}")
    private String country;

    @NotBlank(message = "{LoginByPhone.code.null}")
    private String code;

    private String promotion;

    private LoginType loginType;

    /**
     * 钱包标志id
     */
    private String walletMarkId;
}
