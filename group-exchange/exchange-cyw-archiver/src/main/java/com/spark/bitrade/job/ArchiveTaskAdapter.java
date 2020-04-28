package com.spark.bitrade.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.spark.bitrade.logback.ArchiveData;
import com.spark.bitrade.support.ArchiveScheduleService;
import com.spark.bitrade.support.ArchiveTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 归档任务适配器
 *
 * @author Pikachu
 * @since 2019/11/4 15:49
 */
public abstract class ArchiveTaskAdapter implements ArchiveTask, InitializingBean {

    private final Logger LOGGER = LoggerFactory.getLogger("ARCHIVE");
    private ArchiveScheduleService scheduleService;

    @Autowired
    public void setScheduleService(ArchiveScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    /**
     * 记录日志
     *
     * @param data 数据
     */
    protected void log(ArchiveData data) {
        // JSON.DEFFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        if (data != null) {
            LOGGER.info("{}", JSON.toJSONString(data,
                    SerializerFeature.WriteDateUseDateFormat,
                    SerializerFeature.WriteBigDecimalAsPlain));
        }
    }

    /**
     * 记录日志
     *
     * @param type  类型
     * @param clazz 类
     * @param data  数据
     * @param <T>   泛型
     */
    protected <T> void log(String type, Class<T> clazz, T data) {
        if (data != null) {
            ArchiveData ad = new ArchiveData(type, clazz.getName(), data);
            log(ad);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        scheduleService.schedule(this);
    }
}
