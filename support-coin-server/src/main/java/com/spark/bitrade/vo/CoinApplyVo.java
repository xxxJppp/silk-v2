package com.spark.bitrade.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spark.bitrade.entity.SupportUpCoinApply;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.05 10:06
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@ApiModel(description = "上币申请Vo")
public class CoinApplyVo extends SupportUpCoinApply {
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
     * 有效用户数
     */
    @ApiModelProperty(value = "有效用户数")
    private Integer effectiveUserNum;

    /**
     * 交易对信息
     */
    @ApiModelProperty(value = "币种基本信息修改申请界面 交易对显示  如：BTT/TTBT")
    private List<CoinMatchVo> coinMatchList = new ArrayList<>();

    /**
     * 项目方名称
     */
    @ApiModelProperty(value = "项目方名称")
    private String projectName;

    /**
     * 币种名称
     */
    @ApiModelProperty(value = "币种名称")
    private String coinName;

}
