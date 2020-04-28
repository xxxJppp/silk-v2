package com.spark.bitrade.util;

import cn.jpush.api.push.model.*;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 极光推送消息体构建工具类
 * @author daring5920
 * @time 2019.05.22 17:28
 */
public class JGPushObjectUtils {


    /*
    * 推送给多个标签（只要在任何一个标签范围内都满足）：在深圳、广州、或者北京
        {
            "audience" : {
                "tag" : [ "深圳", "广州", "北京" ]
            }
        }
        推送给多个标签（需要同时在多个标签范围内）：在深圳并且是“女”
        {
            "audience" : {
                "tag_and" : [ "深圳", "女" ]
            }
        }
        推送给多个别名：
        {
            "audience" : {
                "alias" : [ "4314", "892", "4531" ]
            }
        }
        推送给多个注册 ID：
        {
            "audience" : {
                "registration_id" : [ "4312kjklfds2", "8914afd2", "45fdsa31" ]
            }
        }
        可同时推送指定多类推送目标：在深圳或者广州，并且是 “女” “会员”
        {
            "audience" : {
                "tag" : [ "深圳", "广州" ],
                "tag_and" : [ "女", "会员"]
            }
        }*/

    /**
     * 快捷地构建推送对象：所有平台，所有设备，内容为 ALERT 的通知
     *
     * @param
     * @return true
     * @author daring5920
     * @time 2019.05.22 17:28
     */
    public static PushPayload buildPushObjectAllAllAlert(String alert) {
        return PushPayload.alertAll(alert);
    }

    /**
     * 目标是 tags 的设备 通知 ALERT，并且标题为 TITLE。
     * @author daring5920
     * @time 2019.05.22 17:38
     * @param tags
     * @param platform
     * @param alert
     * @param title
     * @return true
     */
    public static PushPayload buildPushObjectTagAlertWithTitle(List<String> tags, Platform platform,String alert, String title) {
        return PushPayload.newBuilder()
                .setPlatform(platform)
                .setAudience(Audience.tag(tags))
                .setNotification(Notification.newBuilder()
                        .addPlatformNotification(AndroidNotification.newBuilder().setAlert(alert).setTitle(title).build())
                        .addPlatformNotification(IosNotification.newBuilder().setAlert(alert).build())
                        .build())
                .build();
    }


    /**
     *构建推送对象：推送目标是 tags 的交集，推送内容同时包括通知与消息 -
     * 通知信息是 alert，角标数字为 badge，通知声音为 "happy"，并且附加字段 extras；
     * 消息内容是 message。通知是 APNs 推送通道的，消息是 JPush 应用内消息通道的。APNs 的推送环境是“生产”（如果不显式设置的话，Library 会默认指定为开发）
     * @author daring5920
     * @time 2019.05.22 17:43
     * @param tags
     * @param extras
     * @param platform
     * @param alert
     * @param message
     * @param badge
     * @return true
     */
    public static PushPayload buildPushObjectTagAndAlertWithExtrasAndMessage(List<String> tags, Map<String,String> extras, Platform platform, String alert, String message,Integer badge) {
        return PushPayload.newBuilder()
                .setPlatform(platform)
                .setAudience(Audience.tag_and(tags))
                .setNotification(Notification.newBuilder()
                        .addPlatformNotification(IosNotification.newBuilder()
                                .setAlert(alert)
                                .setBadge(badge)
                                .setSound("happy")
                                .addExtras(extras)
                                .build())
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setAlert(alert)
                                .addExtras(extras)
                                .build())
                        .build())
                .setMessage(Message.content(message))
                .setOptions(Options.newBuilder()
                        .setApnsProduction(true)
                        .build())
                .build();
    }



    /**
     *构建推送对象：推送目标是 tags 的交集，推送内容同时包括通知与消息 -
     * 通知信息是 alert，角标数字为 badge，通知声音为 "happy"，并且附加字段 extras；
     * 消息内容是 message。通知是 APNs 推送通道的，消息是 JPush 应用内消息通道的。APNs 的推送环境是“生产”（如果不显式设置的话，Library 会默认指定为开发）
     * @author daring5920
     * @time 2019.05.22 17:43
     * @param tags
     * @param extras
     * @param platform
     * @param alert
     * @param message
     * @param badge
     * @return true
     */
    public static PushPayload buildPushObject(List<String> tags,List<String> alias, Map<String,String> extras, Platform platform, String alert, String message,Integer badge,Boolean apnsProduction) {

        PushPayload.Builder pushPayloadBuilder = PushPayload.newBuilder()
                .setPlatform(platform);
        Audience.Builder audienceBuilder = Audience.newBuilder();
        if(tags != null && tags.size() > 0){
            audienceBuilder.addAudienceTarget(AudienceTarget.tag(tags));
        }
        if(alias != null && alias.size() > 0){
            audienceBuilder.addAudienceTarget(AudienceTarget.alias(alias));
        }
        pushPayloadBuilder.setAudience(audienceBuilder.build());

        if(StringUtils.isNotEmpty(alert)){
            pushPayloadBuilder.setNotification(Notification.newBuilder()
                    .addPlatformNotification(IosNotification.newBuilder()
                            .setAlert(alert)
                            .setBadge(badge)
                            .setSound("happy")
                            .addExtras(extras)
                            .build())
                    .addPlatformNotification(AndroidNotification.newBuilder()
                            .setAlert(alert)
                            .setStyle(1)
                            .addExtras(extras)
                            .setBigText(alert)
                            .build())
                    .build());
        }

        if(StringUtils.isNotEmpty(message)){
            pushPayloadBuilder.setMessage(Message.content(message));
        }
        return pushPayloadBuilder.setOptions(Options.newBuilder()
                        .setApnsProduction(apnsProduction)
                        .build())
                .build();
    }


    /**
     *构建推送对象：推送目标是 （tags 的并集）交（alias 的并集），
     * 推送内容是 - 内容为 message 的消息，并且附加字段 extras。
     * @author daring5920
     * @time 2019.05.22 17:49
     * @param tags
     * @param alias
     * @param extras
     * @param platform
     * @param message
     * @return true
     */
    public static PushPayload buildPushObjectAudienceMoreMessageWithExtras(List<String> tags, List<String> alias,Map<String,String> extras, Platform platform, String message) {
        return PushPayload.newBuilder()
                .setPlatform(platform)
                .setAudience(Audience.newBuilder()
                        .addAudienceTarget(AudienceTarget.tag(tags))
                        .addAudienceTarget(AudienceTarget.alias(alias))
                        .build())
                .setMessage(Message.newBuilder()
                        .setMsgContent(message)
                        .addExtras(extras)
                        .build())
                .build();
    }

    /**
     *内容包含SMS信息
     * @author daring5920
     * @time 2019.05.22 17:56
     * @param alias
     * @param extras
     * @param platform
     * @param message
     * @param title
     * @return true
     */
    public static PushPayload buildPushObjectWithSMS(List<String> alias,Map<String,String> extras, Platform platform, String message,String title) {

        SMS sms = SMS.newBuilder()
                .setDelayTime(1000)
                .setTempID(2000)
                .addParas(extras)
                .build();
        return PushPayload.newBuilder()
                .setPlatform(platform)
                .setAudience(Audience.alias(alias))
                .setMessage(Message.newBuilder()
                        .setTitle(title)
                        .setMsgContent(message).build())
                .setSMS(sms).build();

    }
}
