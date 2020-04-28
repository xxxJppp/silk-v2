package com.spark.bitrade.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 孵化区，基础信息表状态
 *
 * @author liaoqinghui  
 * @time 2019.08.30 10:22  
 */
@Getter
@AllArgsConstructor
public enum IncubatorsBasicStatus implements BaseEnum {
    /**
     * 0-上币待审核
     */
    UP_COIN_PENDING("上币待审核"),
    /**
     * 1-上币审核通过
     */
    UP_COIN_APPROVED("上币审核通过"),
    /**
     * 2-上币审核拒绝
     */
    UP_COIN_REJECTED("上币审核拒绝"),
    /**
     * 3-退出待审核
     */
    EXIT_COIN_PENDING("退出待审核"),
    /**
     * 4-退出审核通过
     */
    EXIT_COIN_APPROVED("退出审核通过"),
    /**
     * 5-退出审核拒绝
     */
    EXIT_COIN_REJECTED("退出审核拒绝"),
    /**
     * 6-已关闭
     */
    CLOSED("已关闭"),
    /**
     * 7-已创建社区
     */
    CREATE_COMMUNITY("已创建社区"),
    /**
     * 8-正常申请
     */
    NORMAL("正常申请"),
    ;

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }
}
