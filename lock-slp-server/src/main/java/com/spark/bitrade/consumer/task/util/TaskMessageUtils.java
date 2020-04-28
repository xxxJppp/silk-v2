package com.spark.bitrade.consumer.task.util;

import com.spark.bitrade.constants.LockSlpConstant;
import com.spark.bitrade.entity.LockSlpReleaseTask;
import com.spark.bitrade.entity.LockSlpUpdateTask;
import com.spark.bitrade.mq.*;

import java.util.ArrayList;

/**
 * TaskMessageUtils
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/7/3 11:56
 */
public final class TaskMessageUtils {

    private TaskMessageUtils() {
    }

    public static TaskMessage buildUpdateTask(LockSlpUpdateTask next, ArrayList<Long> chainIds) {

        UpdateTaskMessage message = new UpdateTaskMessage();
        message.setRefId(next.getId() + "");
        message.setType(UpdateTaskMessageType.HANDLE_UPDATE_TASK);
        message.setAcyclicRecommendChain(chainIds);

        return message.toTaskMessage(LockSlpConstant.KAFKA_MSG_UPDATE_TASK);
    }

    public static TaskMessage buildReleaseTask(LockSlpReleaseTask next, ReleaseTaskMessageType type, ArrayList<Long> chainIds) {
        ReleaseTaskMessage message = new ReleaseTaskMessage();
        message.setRefId(next.getId() + "");
        message.setType(type);
        message.setAcyclicRecommendChain(chainIds);

        return message.toTaskMessage(LockSlpConstant.KAFKA_MSG_RELEASE_TASK);
    }

}
