package com.spark.bitrade.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 *  * 申述类型
 *  * @author daring5920
 *  * @time 2018/9/7 10:32 
 *  
 */
@AllArgsConstructor
@Getter
public enum AppealType implements BaseEnum {

    RELEASE("请求放币"),

    CANCLE("请求取消订单"),

    OTHER("其他");

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }

    public static AppealType getByOrdinal(Integer ordinal) {
        AppealType[] values = AppealType.values();
        for (AppealType type : values) {
            if (type.getOrdinal() == ordinal) {
                return type;
            }
        }
        return null;
    }
}
