package com.spark.bitrade.dto;

import com.spark.bitrade.constant.IncubatorsBasicStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 孵化区，入口DTO
 *
 * @author zhongxj  
 * @time 2019.09.02
 */
@Data
public class IncubatorsEntranceDto {
    /**
     * 状态（0-上币待审核；1-上币审核通过；2-上币审核拒绝；7-已创建社区；8-正常申请）
     */
    @ApiModelProperty(value = "状态（0-上币待审核；1-上币审核通过；2-上币审核拒绝；7-已创建社区；8-正常申请）", example = "")
    private IncubatorsBasicStatus incubatorsBasicStatus;
}
