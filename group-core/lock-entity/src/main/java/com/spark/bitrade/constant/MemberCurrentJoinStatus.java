package com.spark.bitrade.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.07.26 10:33  
 */
@Getter
@AllArgsConstructor
public enum  MemberCurrentJoinStatus implements BaseEnum {

    NO_JOIN("未加入社区"), //0
    IS_CURRENT_MEMBER("是这个社区的成员"),//1
    OTHER_COMMUNITY_MEMBER("其他社区的成员")//2
    ;

    private String name;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }
}
