package com.spark.bitrade.web.resubmit.impl;

import com.spark.bitrade.web.resubmit.IForbidResubmit;

/**
 *  默认的实现
 *
 * @author young
 * @time 2019.07.18 23:49
 */
public class ForbidResubmitDefaultImpl implements IForbidResubmit {
    @Override
    public boolean validate(String key, long interdictTime) {
        //可以实现基于单机版的重复提交
        return true;
    }
}
