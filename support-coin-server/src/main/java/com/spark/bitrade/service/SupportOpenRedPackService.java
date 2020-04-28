package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.SupportOpenRedPack;

/**
 * <p>
 * 红包开通申请表 服务类
 * </p>
 *
 * @author qhliao
 * @since 2020-02-19
 */
public interface SupportOpenRedPackService extends IService<SupportOpenRedPack> {

    SupportOpenRedPack findByProjectCoin(String projectCoin);

}
