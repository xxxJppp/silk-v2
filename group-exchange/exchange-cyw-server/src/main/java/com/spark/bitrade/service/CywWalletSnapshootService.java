package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.CywWalletSnapshoot;

import java.util.Date;

/**
 * 机器人钱包每日快照表(CywWalletSnapshoot)表服务接口
 *
 * @author yangch
 * @since 2019-09-25 19:09:43
 */
public interface CywWalletSnapshootService extends IService<CywWalletSnapshoot> {
    /**
     * 快照账户数据
     *
     * @param snapshootTime
     */
    void snapshootAll(Date snapshootTime);
}