package com.spark.bitrade.biz;

import com.alibaba.fastjson.JSONObject;
import com.spark.bitrade.constant.NoticeExtrasType;
import com.spark.bitrade.messager.MemberMailEntity;
import com.spark.bitrade.messager.NoticeEntity;
import com.spark.bitrade.messager.NoticeType;
import com.spark.bitrade.messager.SysNoticeEntity;
import com.spark.bitrade.service.IJpushService;
import com.spark.bitrade.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class LuckPushService {

    @Autowired
    private IJpushService jpushService;

    @Async
    public void sendStationMessage(String content, String title, Long memberId,Long luckId,Integer luckType){

        MemberMailEntity memberMailEntity=new MemberMailEntity();
        memberMailEntity.setSubject(title);
        memberMailEntity.setContent(content);
        memberMailEntity.setToMemberId(memberId);
        MessageResult messageResult = jpushService.sendMemberMailEntity(memberMailEntity);
        log.info(title+",调用通知保存，返回结果：{}", messageResult);
        Long id=null;
        if (messageResult.isSuccess()) {
            Object obj = messageResult.getData();
            if (obj != null) {
                JSONObject json = (JSONObject) JSONObject.toJSON(obj);
                if (json.containsKey("id")) {
                    id = Long.valueOf(json.get("id").toString());
                }
            }
        }else {
            log.info("发送消息失败!");
            return;
        }
        NoticeEntity noticeEntity = new NoticeEntity();
        noticeEntity.setNoticeType(NoticeType.SYS_NOTICE);
        SysNoticeEntity sysNoticeEntity = new SysNoticeEntity();
        sysNoticeEntity.setMemberId(memberId);
        sysNoticeEntity.setSubNoticeType(NoticeType.SYS_NOTICE_MAIL);
        sysNoticeEntity.setTitle(title);
        sysNoticeEntity.setSubTitle(content);

        Map<String, Object> extras = sysNoticeEntity.getExtras();
        extras.put("id", id);
        extras.put("luckId",luckId);
        extras.put("luckType",luckType);

        noticeEntity.setData(sysNoticeEntity);
        log.info("事件：{}============推送通知内容：{}", title, noticeEntity);
        jpushService.sendNoticeEntity(noticeEntity);




    }
}
