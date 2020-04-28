package com.spark.bitrade.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.08.30 10:53  
 */
@Data
public class UpCoinForm {

    /**
     * 项目名称
     */
    @ApiModelProperty(value = "项目名称")
    @NotBlank
    private String proName;

    /**
     * 社区名称
     */
    @ApiModelProperty(value = "社区名称")
    @NotBlank
    private String communityName;

    /**
     * 联系人
     */
    @NotBlank
    @ApiModelProperty(value = "联系人")
    private String contactPerson;

    /**
     * 项目简介
     */
    @NotBlank
    @ApiModelProperty(value = "项目简介")
    private String proDesc;

    /**
     * 联系电话
     */
    @NotBlank
    @ApiModelProperty(value = "联系电话")
    private String telPhone;

    /**
     * 微信号
     */
    @NotBlank
    @ApiModelProperty(value = "微信号")
    private String wechatNum;

    /**
     * 上传微信二维码
     */
    @NotBlank
    @ApiModelProperty(value = "上传微信二维码url")
    private String wechatCode;
    /**
     * 资金密码
     */
    @NotBlank
    @ApiModelProperty(value = "资金密码")
    private String jyPassword;
}
