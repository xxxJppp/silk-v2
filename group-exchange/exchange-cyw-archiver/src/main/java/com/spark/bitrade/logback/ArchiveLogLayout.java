package com.spark.bitrade.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.LayoutBase;

import java.sql.Timestamp;

/**
 * ArchiveLogLayout
 *
 * @author Pikachu
 * @since 2019/11/4 14:58
 */
public class ArchiveLogLayout extends LayoutBase<ILoggingEvent> {

    private String projectName;

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public String doLayout(ILoggingEvent event) {

        // P : 项目名称
        // D : 数据内容
        // T : 时间戳


        StringBuilder buf = new StringBuilder();

        buf.append("{");
        buf.append("\"P\":").append("\"").append(projectName).append("\",");
        buf.append("\"D\":");
        buf.append(event.getFormattedMessage());
        buf.append(",");
        buf.append("\"T\":");
        buf.append("\"").append(new Timestamp(event.getTimeStamp())).append("\"");
        buf.append("}");
        buf.append("\r\n");

        return buf.toString();
    }
}
