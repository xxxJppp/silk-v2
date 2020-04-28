package com.spark.bitrade.service;

import org.springframework.stereotype.Service;

/**
 * 简单的升级服务接口（通过手工的方式可以停止对内的机器人调用）
 */
@Service
public class UpdatingService {

    /**
     * 在内存中存放是否升级的标志，不用实时的刷新数据库
     */
    private static boolean isUpdating = false;


    /**
     * 是否升级
     *
     * @return
     */
    public boolean isUpdating() {
        return isUpdating;
    }

    /**
     * 刷新是否升级的状态
     *
     * @param flag 0=升级状态，否则未升级
     * @return
     */
    public boolean flushUpdatingStatus(Integer flag) {
        if (flag == null || flag > 0) {
            isUpdating = false;
            return false;
        } else {
            isUpdating = true;
            return true;
        }
    }

}
