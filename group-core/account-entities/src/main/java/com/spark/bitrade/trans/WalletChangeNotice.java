package com.spark.bitrade.trans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 *  钱包账户余额变更通知
 *
 * @author yangch
 * @time 2019.01.23 09:15
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "钱包账户余额变更通知")
public class WalletChangeNotice {
    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID", example = "")
    long memberId;

    /**
     * 币种ID
     */
    @ApiModelProperty(value = "币种ID", example = "")
    private String coinId;

    /**
     * 币种单位简称（冗余）
     */
    @ApiModelProperty(value = "币种单位简称", example = "")
    private String coinUnit;

    /**
     * 通知来源类型
     */
    @ApiModelProperty(value = "通知来源类型", example = "")
    private NoticeType type = NoticeType.initiative;

    /**
     * 通知来源类型
     *   主动通知：余额变化后主动的通知
     *   被动通知：通过监控程序发现超时未处理时的被动通知
     */
    @Getter
    @AllArgsConstructor
    public enum NoticeType {
        /**
         * 主动通知：余额变化后主动的通知
         */
        initiative("主动通知"),

        /**
         * 被动通知：通过监控程序发现超时未处理时的被动通知
         */
        passive("被动通知");

        String desc;
    }
}
