package com.spark.bitrade.job.handler;

import com.spark.bitrade.entity.LockSlpReleasePlan;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.service.LockSlpReleaseService;
import com.spark.bitrade.vo.JobReceipt;
import com.spark.bitrade.vo.SlpReleaseJobParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * LockSlpReleaseJobHandler
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/7/9 10:58
 */
@JobHandler(value = "lockSlpReleaseJobHandler")
@Component
public class LockSlpReleaseJobHandler extends IJobHandler {

    private LockSlpReleaseService lockSlpReleaseService;

    @Autowired
    public void setLockSlpReleaseService(LockSlpReleaseService lockSlpReleaseService) {
        this.lockSlpReleaseService = lockSlpReleaseService;
    }

    /**
     * 任务参数说明
     * <p>
     * 默认参数[] = ["NOW"]
     * 指定日期 ["2019-07-09 11:03:33"]
     *
     * @param param datetime
     * @return state
     * @throws Exception ex
     */
    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("开始处理任务每日释放任务");

        // 解析参数
        SlpReleaseJobParam jp = SlpReleaseJobParam.of(param);

        // 查询计划
        List<LockSlpReleasePlan> plans = lockSlpReleaseService.getPendingReleasePlan(jp);

        // 遍历执行

        if (plans != null) {
            for (LockSlpReleasePlan plan : plans) {
                // 已经处理过则跳过
                if (jp.isReleased(plan.getReleaseCurrentDate())) {
                    String log = String.format("[ datetime = %s ] 返还计划 [ id = %d, coin_unit = %s  ] 已处理",
                            jp.getDatetime(), plan.getId(), plan.getCoinUnit());
                    XxlJobLogger.log(log);
                    continue;
                }
                try {
                    JobReceipt receipt = lockSlpReleaseService.doRelease(plan, jp.getDatetime());
                    String format = String.format("[ datetime = %s ] 返还计划 [ id = %d, coin_unit = %s  ] 处理结果 -> %s",
                            jp.getDatetime(), plan.getId(), plan.getCoinUnit(), receipt.getSuccess());

                    XxlJobLogger.log(format);
                } catch (MessageCodeException ex) {
                    String log = String.format("执行释放失败 [ plan_id = %s, code = %d, err = '%s' ]", plan.getId(), ex.getCode(), ex.getMessage());
                    XxlJobLogger.log(log);
                }
            }
        }

        return ReturnT.SUCCESS;
    }
}
