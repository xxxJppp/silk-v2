package com.spark.bitrade.job;

import com.spark.bitrade.redis.PalceService;
import com.spark.bitrade.service.CywWalletSnapshootService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 *  每日快照任务
 *
 * @author young
 * @time 2019.09.26 17:59
 */
@Component
@Slf4j
public class CywWalletSnapshootJob {
    @Autowired
    private PalceService palceService;
    @Autowired
    private CywWalletSnapshootService cywWalletSnapshootService;

    /**
     * 每日下午4点半执行快照任务
     */
//    @Scheduled(cron = "0 0/20 * * * *")
//    @Scheduled(cron = "0/20 * * * * *")
    @Scheduled(cron = "0 30 16 * * *")
    public void snapshoot() {
        if (!palceService.place("lock:job:snapshoot", 60)) {
            log.info("job >>> 未获取任务锁，退出任务");
            return;
        }

        // 分布式并发控制
        log.info("job >>> 开始执行钱包账户的快照任务......................");

        // 执行快照任务
        try {
            cywWalletSnapshootService.snapshootAll(new Date());
        } catch (Exception ex) {
            log.error("执行快照任务失败", ex);
        }
        log.info("job >>> 完成执行钱包账户的快照任务......................");
    }
}
