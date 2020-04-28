package com.spark.bitrade.support;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

import java.util.Date;

/**
 * 归档定制触发器
 *
 * @author Pikachu
 * @since 2019/11/4 11:12
 */
public interface ArchiveTrigger extends Trigger {

    /**
     * 下次任务执行日期
     *
     * @param date 上次执行完成日期
     * @return the next execution time
     */
    Date nextExecutionTime(Date date);

    @Override
    default Date nextExecutionTime(TriggerContext triggerContext) {
        Date date = triggerContext.lastCompletionTime();
        if (date != null) {
            Date scheduled = triggerContext.lastScheduledExecutionTime();
            if (scheduled != null && date.before(scheduled)) {
                // Previous task apparently executed too early...
                // Let's simply use the last calculated execution time then,
                // in order to prevent accidental re-fires in the same second.
                date = scheduled;
            }
        } else {
            date = new Date();
        }
        return nextExecutionTime(date);
    }
}
