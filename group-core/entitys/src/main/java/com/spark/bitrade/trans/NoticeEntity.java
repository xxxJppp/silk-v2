package com.spark.bitrade.trans;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *  otc通知实体
 *
 * @author young
 * @time 2019.05.17 18:04
 */
@Data
@AllArgsConstructor
public class NoticeEntity implements Comparable {
    /**
     * 通知ID
     */
    private String id;
    /**
     * 通知时间
     */
    private long time;
    /**
     * 通知类型，0=未知/1=聊天/2=事件/3=事件+聊天
     */
    private int type;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else {
            if (this == obj) {
                //实例相同
                return true;
            } else if (this.id == null) {
                //ID不存在
                return false;
            } else if (obj instanceof NoticeEntity) {
                NoticeEntity c = (NoticeEntity) obj;
                if (this.id.equals(c.id)) {
                    //ID相同
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    /**
     * 倒序排序
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(Object o) {
        NoticeEntity c = (NoticeEntity) o;
        if (this.time < c.time) {
            return 1;
        } else if (this.time > c.time) {
            return -1;
        }
        return 0;
    }
}