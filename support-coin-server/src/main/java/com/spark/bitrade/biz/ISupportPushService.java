package com.spark.bitrade.biz;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.11.06 17:02  
 */
public interface ISupportPushService {

    /**
     * 推送系统消息
     * @param content
     * @param title
     * @param memberId
     */
    void sendStationMessage(String content, String title, Long memberId);

}
