package com.spark.bitrade.util;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jiguang.common.resp.DefaultResult;
import cn.jpush.api.JPushClient;
import cn.jpush.api.device.TagAliasResult;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.report.ReceivedsResult;
import cn.jpush.api.schedule.ScheduleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 极光推送请求工具类
 *
 * @author daring5920
 * @time 2019.05.22 17:28
 */
public class JGPushRequestUtils {

    private static final Logger log = LoggerFactory.getLogger(JGPushRequestUtils.class);

    /**
     * 推送PushPayload
     *
     * @param jpushClient
     * @param pushPayload
     * @return true
     * @author daring5920
     * @time 2019.05.22 18:04
     */
    public static PushResult pushPayload(JPushClient jpushClient, PushPayload pushPayload) {
        try {

            PushResult result = jpushClient.sendPush(pushPayload);
            return result;
        } catch (APIConnectionException e) {
            log.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            log.error("Error response from JPush server. Should review and fix it. ", e);
            log.info("HTTP Status: " + e.getStatus());
            log.info("Error Code: " + e.getErrorCode());
            log.info("Error Message: " + e.getErrorMessage());
        }

        return null;
    }


    /**
     * 统计获取样例
     *
     * @param jpushClient
     * @param receiveds
     * @return true
     * @author daring5920
     * @time 2019.05.22 18:04
     */
    public static ReceivedsResult getReportReceiveds(JPushClient jpushClient, String receiveds) {
        try {

            ReceivedsResult result = jpushClient.getReportReceiveds(receiveds);
            return result;
        } catch (APIConnectionException e) {
            log.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            log.error("Error response from JPush server. Should review and fix it. ", e);
            log.info("HTTP Status: " + e.getStatus());
            log.info("Error Code: " + e.getErrorCode());
            log.info("Error Message: " + e.getErrorMessage());
        }
        return null;

    }

    /**
     * Tag/Alias 样例
     *
     * @param jpushClient
     * @param registrationId
     * @return true
     * @author daring5920
     * @time 2019.05.22 18:04
     */
    public static TagAliasResult getDeviceTagAlias(JPushClient jpushClient, String registrationId) {
        try {

            TagAliasResult result = jpushClient.getDeviceTagAlias(registrationId);
            return result;
        } catch (APIConnectionException e) {
            log.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            log.error("Error response from JPush server. Should review and fix it. ", e);
            log.info("HTTP Status: " + e.getStatus());
            log.info("Error Code: " + e.getErrorCode());
            log.info("Error Message: " + e.getErrorMessage());
        }
        return null;

    }


    /**
     * 绑定手机号
     *
     * @param jpushClient
     * @param registrationId
     * @param mobile
     * @return true
     * @author daring5920
     * @time 2019.05.22 18:04
     */
    public static DefaultResult bindMobile(JPushClient jpushClient, String registrationId, String mobile) {
        try {

            DefaultResult result = jpushClient.bindMobile(registrationId, mobile);
            return result;
        } catch (APIConnectionException e) {
            log.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            log.error("Error response from JPush server. Should review and fix it. ", e);
            log.info("HTTP Status: " + e.getStatus());
            log.info("Error Code: " + e.getErrorCode());
            log.info("Error Message: " + e.getErrorMessage());
        }
        return null;

    }


    /**
     * 定时推送
     *
     * @param jpushClient
     * @param name
     * @param time        "2016-07-30 12:30:25"
     * @param pushPayload
     * @return true
     * @author daring5920
     * @time 2019.05.22 18:11
     */
    public static ScheduleResult createSingleSchedule(JPushClient jpushClient, String name, String time, PushPayload pushPayload) {
        try {

            ScheduleResult result = jpushClient.createSingleSchedule(name, time, pushPayload);
            return result;
        } catch (APIConnectionException e) {
            log.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            log.error("Error response from JPush server. Should review and fix it. ", e);
            log.info("HTTP Status: " + e.getStatus());
            log.info("Error Code: " + e.getErrorCode());
            log.info("Error Message: " + e.getErrorMessage());
        }
        return null;

    }
}
