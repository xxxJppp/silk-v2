package com.spark.bitrade.web.resubmit;

/**
 *  禁止重复提交接口
 *
 * @author young
 * @time 2019.07.18 20:02
 */
public interface IForbidResubmit {

    /**
     * 验证
     * @param key
     * @param interdictTime
     * @return
     */
    boolean validate(String key, long interdictTime);
}
