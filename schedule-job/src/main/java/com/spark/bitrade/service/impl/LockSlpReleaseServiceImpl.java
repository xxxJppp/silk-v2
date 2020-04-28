package com.spark.bitrade.service.impl;

import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.LockSlpReleasePlan;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.service.ILockSlpService;
import com.spark.bitrade.service.LockSlpReleaseService;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.vo.JobReceipt;
import com.spark.bitrade.vo.SlpReleaseJobParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * LockSlpReleaseServiceImpl
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/7/9 10:24
 */
@Slf4j
@Service
public class LockSlpReleaseServiceImpl implements LockSlpReleaseService {

    private ILockSlpService lockSlpService;

    @Autowired
    public void setLockSlpService(ILockSlpService lockSlpService) {
        this.lockSlpService = lockSlpService;
    }

    @Override
    public List<LockSlpReleasePlan> getPendingReleasePlan(SlpReleaseJobParam param) {

        MessageRespResult<List<LockSlpReleasePlan>> result = lockSlpService.getLockSlpReleasePlan(param.getLimit());

        if (result.isSuccess()) {
            return result.getData();
        }
        throw new MessageCodeException(CommonMsgCode.of(result.getCode(), result.getMessage()));
    }

    @Override
    public JobReceipt doRelease(LockSlpReleasePlan plan, String datetime) {

        MessageRespResult<JobReceipt> result = lockSlpService.releaseLockSlpReleasePlan(plan.getId(), datetime);
        if (result.isSuccess()) {
            return result.getData();
        }
        log.error("执行释放失败 [ plan_id = {}, code = {}, err = '{}' ]", plan.getId(), result.getCode(), result.getMessage());
        throw new MessageCodeException(CommonMsgCode.of(result.getCode(), result.getMessage()));
    }

    @Override
    public JobReceipt doReleaseCompleteCheck() {
        MessageRespResult<JobReceipt> result = lockSlpService.releaseCompletedCheck();
        if (result.isSuccess()) {
            return result.getData();
        }
        throw new MessageCodeException(CommonMsgCode.of(result.getCode(), result.getMessage()));
    }
}
