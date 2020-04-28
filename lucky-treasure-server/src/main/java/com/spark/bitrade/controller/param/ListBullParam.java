package com.spark.bitrade.controller.param;

import com.spark.bitrade.enums.BullStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ListBullParam extends BasePageParam {

    @ApiModelProperty(value = "状态 0:未开始 1:选牛中  2:赛牛中  3:已结束")
    private BullStatusEnum status;

    @ApiModelProperty(value = "票面币种")
    private String coinUnit;

    @ApiModelProperty(value = "用户ID未登录可不传")
    private Long memberId;

    @ApiModelProperty(value = "为空或0时，加载所有，否则如果memberId不为空，仅加载用户自己的")
    private String onlyMine;
}
