package com.spark.bitrade.dsc;

import com.spark.bitrade.dsc.annotation.DscEntity;
import com.spark.bitrade.dsc.api.MessagePusher;
import com.spark.bitrade.dsc.exception.DscValidateException;
import com.spark.bitrade.dsc.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.function.Supplier;

/**
 * DscValidateHelper
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/11/19 15:22
 */
@Slf4j
public class DscValidateHelper {

    private final DscContext dscContext;
    private final MessagePusher messagePusher;

    public DscValidateHelper(DscContext dscContext, MessagePusher messagePusher) {
        this.dscContext = dscContext;
        this.messagePusher = messagePusher;
    }

    /**
     * 更新签名
     *
     * @param memberId memberId
     * @param errMsg   错误消息
     * @param entity   bean
     */
    public void udpate(Long memberId, String errMsg, Object entity) {

        if (notDscEntity(entity)) {
            return;
        }

        try {
            DscEntityResolver resolver = dscContext.getDscEntityResolver(entity);
            resolver.update();

        } catch (RuntimeException ex) {
            log.error("dsc update error -> {}", ex.getMessage());

            String msg = "更新签名、密文内容报错！";
            if (!StringUtils.isEmpty(errMsg)) {
                msg = errMsg;
            }

            AlarmMonitor alarm = generateAlarmMonitor(AlarmType.DSC_UPDATE, memberId, msg);

            messagePusher.push(alarm);

            throw ex;
        }
    }

    /**
     * 验证签名
     * <p>
     * 出错抛出异常
     *
     * @param memberId mid
     * @param errMsg   error msg
     * @param supplier 函数
     * @param <T>      泛型
     * @return bean
     */
    public <T> T validate(Long memberId, String errMsg, Supplier<T> supplier) {
        T entity = supplier.get();
        if (notDscEntity(entity)) {
            return entity;
        }

        try {
            DscEntityResolver resolver = dscContext.getDscEntityResolver(entity);
            if (!resolver.validate()) {
                // 验签失败
                log.warn("验签失败 resolver = {}", resolver.stringify());
                throw new DscValidateException("签名错误");
            }

        } catch (RuntimeException ex) {
            String msg = "'签名验证失败'";
            if (!StringUtils.isEmpty(errMsg)) {
                msg = errMsg;
            }

            AlarmMonitor alarm = generateAlarmMonitor(AlarmType.DSC_VERIFY_SIGNATURE, memberId, msg);

            messagePusher.push(alarm);

            throw ex;
        }
        return entity;
    }

    /**
     * 验证签名
     *
     * @param memberId mid
     * @param errMsg   error msg
     * @param entity   entity
     * @return bool
     */
    public boolean validate(Long memberId, String errMsg, Object entity) {
        if (notDscEntity(entity)) {
            return false;
        }

        try {
            validate(memberId, errMsg, () -> entity);
            return true;
        } catch (RuntimeException ex) {
            // ignore
            return false;
        }
    }

    private boolean notDscEntity(Object bean) {
        if (bean == null) {
            return true;
        }

        Class<?> clazz = bean.getClass();
        DscEntity entity = clazz.getDeclaredAnnotation(DscEntity.class);

        return Utils.isJavaClass(clazz) || entity == null;
    }

    /**
     * 生成告警消息
     *
     * @param type     类型
     * @param memberId 会员ID
     * @param errMsg   错误消息
     * @return am
     */
    private AlarmMonitor generateAlarmMonitor(
            AlarmType type,
            Long memberId,
            String errMsg) {

        // 告警
        AlarmMonitor am = new AlarmMonitor();
        am.setAlarmType(type);
        am.setStatus(BooleanStatus.IS_FALSE);
        am.setMemberId(memberId == null ? -1L : memberId);
        am.setAlarmMsg(errMsg);

        return am;
    }
}
