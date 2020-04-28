package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.entity.constants.BusinessErrorMonitorType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 业务异常告警表表实体类
 *
 * @author yangch
 * @since 2019-09-17 16:45:45
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "业务异常告警表")
public class BusinessErrorMonitor {

    @TableId(type = IdType.NONE)
    @ApiModelProperty(value = "", example = "")
    private Long id;

    @ApiModelProperty(value = "异常时间", example = "")
    private Date createTime;

    /**
     * 异常描述
     */
    @ApiModelProperty(value = "异常描述", example = "")
    private String errorMsg;

    /**
     * 输入参数数据
     */
    @ApiModelProperty(value = "输入参数数据", example = "")
    private String inData;

    /**
     * 处理内容描述
     */
    @ApiModelProperty(value = "处理内容描述", example = "")
    private String maintenanceDescription;

    /**
     * 处理人员ID，管理后端admin表
     */
    @ApiModelProperty(value = "", example = "")
    private Long maintenanceId;

    /**
     * 处理结果描述
     */
    @ApiModelProperty(value = "处理结果描述", example = "")
    private String maintenanceResult;

    @ApiModelProperty(value = "", example = "")
    private BooleanEnum maintenanceStatus;

    @ApiModelProperty(value = "", example = "")
    private Date maintenanceTime;

    /**
     * 业务类型描述（如：枚举，成交明细）
     */
    @ApiModelProperty(value = "", example = "")
    private BusinessErrorMonitorType type;


}