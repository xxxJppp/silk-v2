package com.spark.bitrade.web.valid;

import com.spark.bitrade.constant.RiskLimitEvent;
import com.spark.bitrade.constants.MsgCode;
import com.spark.bitrade.entity.Member;

/**
 * 限制验证器接口
 *
 * @author archx
 * @since 2019/5/17 18:00
 */
public interface LimitValidator {

    /**
     * 排序标识
     *
     * @return int
     */
    default int order() {
        return -1;
    }

    /**
     * 验证方法
     * <p>
     * 验证通过请返回 com.spark.bitrade.constants.CommonMsgCode#SUCCESS <br/>
     * 验证失败将抛出com.spark.bitrade.exception.MessageCodeException异常
     *
     * @param member 当前操作用户
     * @param limit  注解定义的操作限制
     * @return code
     * @see com.spark.bitrade.constants.MsgCode
     * @see com.spark.bitrade.exception.MessageCodeException
     */
    MsgCode valid(Member member, RiskLimitEvent limit);
}
