package com.spark.bitrade.entity;

import com.spark.bitrade.constant.LoginType;
import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @since 2017年12月29日
 */
@Data
public class LoginByEmail {

    @NotBlank(message = "{LoginByEmail.email.null}")
    @Email(message = "{LoginByEmail.email.format}")
    private String email;

    @NotBlank(message = "{LoginByEmail.password.null}")
    private String password;

    @NotBlank(message = "{LoginByEmail.username.null}")
    @Length(min = 3, max = 64, message = "{LoginByEmail.username.length}")
    private String username;

    private String country;

    private String code;

    private String promotion;

    private LoginType loginType;

    /**
     * 钱包标志id
     */
    private String walletMarkId;
}
