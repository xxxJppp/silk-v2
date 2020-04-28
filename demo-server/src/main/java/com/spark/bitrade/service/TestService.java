package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.Test;

/**
 * (Test)表服务接口
 *
 * @author young
 * @since 2019-06-09 15:56:37
 */
public interface TestService extends IService<Test> {
    /**
     * 事务测试
     *
     * @return
     */
    boolean testTransaction();

    /**
     * 事务测试
     * 模拟事务中调用 同一类中的多个方法
     *
     * @return
     */
    boolean testTransaction2();

    void saveTest20000();

    void saveTest2000X();
}