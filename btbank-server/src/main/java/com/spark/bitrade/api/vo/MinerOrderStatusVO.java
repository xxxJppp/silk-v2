package com.spark.bitrade.api.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author ww
 * @time 2019.10.30 16:35
 */

@Data
@ApiModel(value = "主动推送的订单信息类")
public class MinerOrderStatusVO {
    @ApiModelProperty(value = "null")
    private Long id;

    /**
     * 订单状态:(0 新订单，1 已抢单，2 已派单,3抢单结算完成，4，派单结算完成)
     */
    @ApiModelProperty(value = "订单状态:(0 新订单，1 已抢单，2 已派单,3抢单结算完成，4，派单结算完成)")
    private Integer status;

    /**
     * 操作时间
     */
    @ApiModelProperty(value = "操作时间")
    private Date processTime;

    /**
     * 订单金额
     */
    @TableField(value = "money")
    @ApiModelProperty(value = "订单金额")
    private BigDecimal money;
    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;


}
