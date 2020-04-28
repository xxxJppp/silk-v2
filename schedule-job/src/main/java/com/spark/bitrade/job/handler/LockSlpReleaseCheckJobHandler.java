package com.spark.bitrade.job.handler;

import com.spark.bitrade.service.LockSlpReleaseService;
import com.spark.bitrade.vo.JobReceipt;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * LockSlpReleaseCheckJobHandler
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/7/20 14:11
 */
@Component
@JobHandler(value = "lockSlpReleaseCheckJobHandler")
public class LockSlpReleaseCheckJobHandler extends IJobHandler {

    private LockSlpReleaseService lockSlpReleaseService;

    @Autowired
    public void setLockSlpReleaseService(LockSlpReleaseService lockSlpReleaseService) {
        this.lockSlpReleaseService = lockSlpReleaseService;
    }

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("开始处理释放完成检查任务");
        JobReceipt jobReceipt = lockSlpReleaseService.doReleaseCompleteCheck();

        String format = String.format("执行结果 [ state = %s, message = '%s' ] ", jobReceipt.getSuccess(), jobReceipt.getMessage());
        XxlJobLogger.log(format);

        return ReturnT.SUCCESS;
    }
}
