package com.spark.bitrade.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.05 13:44
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SupportCoinRecordsForm implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 中文简介
     */
    @ApiModelProperty(value = "中文简介")
    private String introCn;

    /**
     * 英文简介
     */
    @ApiModelProperty(value = "英文简介")
    private String introEn;

    /**
     * 繁体简介
     */
    @ApiModelProperty(value = "繁体简介")
    private String introHk;

    /**
     * 韩文简介
     */
    @ApiModelProperty(value = "韩文简介")
    private String introKo;

    /**
     * 联系电话
     */
    @NotBlank
    @ApiModelProperty(value = "联系电话")
    private String linkPhone;

    /**
     * 申请人ID
     */
    @ApiModelProperty(value = "申请人ID")
    private String memberId;

    /**
     * 上币申请ID(support_up_coin_apply)
     */
    @ApiModelProperty(value = "上币申请ID")
    private String upCoinId;

    /**
     * 申请币种名称
     */
    @ApiModelProperty(value = "上币申请ID")
    private String coinName;

    /**
     * 区号
     */
    @ApiModelProperty(value = "区号")
    private String areaCode;

}
