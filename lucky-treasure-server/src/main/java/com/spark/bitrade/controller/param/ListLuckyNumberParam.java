package com.spark.bitrade.controller.param;

import com.spark.bitrade.enums.NumberStatusEnum;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ListLuckyNumberParam extends BasePageParam {

    @ApiModelProperty(value = "用户id，可为空，传入ID时 会增加自己的信息 反之没有")
    private Long memberId;

    @ApiModelProperty(value = "状态 0:未开始 1:进行中  2:已结束")
    private NumberStatusEnum status;

    @ApiModelProperty(value = "票面币种")
    private String coinUnit;
    
    @ApiModelProperty(value = "游戏id，可为空，传入时，查询指定游戏信息")
    private Long gameId;
    
    @ApiModelProperty(value = "为空或0时，加载所有，否则如果memberId不为空，仅加载用户自己的")
    private String onlyMine;
}
