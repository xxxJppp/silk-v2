package com.spark.bitrade.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 挖矿返回Vo
 *
 * @author: Zhong Jiang
 * @date: 2019-12-31 16:38
 */
@Data
public class MiningMineralVo {

    @ApiModelProperty(value = "矿石名称")
    private String mineralName;

    @ApiModelProperty(value = "项目方介绍")
    private String projectIntroduce;

    @ApiModelProperty(value = "项目方币种")
    private String  projectCoin;

    @ApiModelProperty(value = "99 标识 已经被挖空")
    private Integer mineraIsEmpty = 0;

    @Override
    public String toString() {
        return "MiningMineralVo{" +
                "mineralName='" + mineralName + '\'' +
                ", projectIntroduce='" + projectIntroduce + '\'' +
                ", projectCoin='" + projectCoin + '\'' +
                '}';
    }
}
