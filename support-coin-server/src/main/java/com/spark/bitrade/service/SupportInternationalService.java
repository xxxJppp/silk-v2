package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.SupportInternational;

import java.util.List;

/**
 * <p>
 * 国际化资源 服务类
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
public interface SupportInternationalService extends IService<SupportInternational> {

    /**
     * 根据币种 key 查出国际化简介信息
     * @param inteKey
     * @return
     */
    List<SupportInternational> findByinternationalKey(String inteKey);

    /**
     * 根据币种 key 查出单个国际化简介信息
     * @param inteKey
     * @return
     */
    SupportInternational findOneByinternationalKey(String inteKey);
}
