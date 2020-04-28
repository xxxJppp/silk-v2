package com.spark.bitrade.mq;

import java.time.Instant;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class DelayTaskMessage implements TaskMessage, Delayed {

    private final String topic;
    private final String body;
    private final long time;

    public DelayTaskMessage(String topic, String body, long timeout) {
        this.topic = topic;
        this.body = body;
        //this.time = System.nanoTime() + timeout;
        this.time = Instant.now().getEpochSecond() + timeout;
    }

    @Override
    public String getRefId() {
        return null;
    }

    @Override
    public String stringify() {
        return body;
    }

    @Override
    public String getTopic() {
        return topic;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        //return unit.convert(this.time - System.nanoTime(), TimeUnit.SECONDS);
        return unit.convert(time - Instant.now().getEpochSecond(), TimeUnit.SECONDS);
    }

    @Override
    public int compareTo(Delayed o) {

//        DelayTaskMessage other = (DelayTaskMessage) o;
//        long diff = time - other.time;
//        if (diff > 0) {
//            return 1;
//        } else if (diff < 0) {
//            return -1;
//        } else {
//            return 0;
//        }

        if (this.getDelay(TimeUnit.SECONDS) > o.getDelay(TimeUnit.SECONDS)) {
            return 1;
        } else if (this.getDelay(TimeUnit.SECONDS) < o.getDelay(TimeUnit.SECONDS)) {
            return -1;
        }
        return 0;
    }

    @Override
    public int hashCode() {
        return body.hashCode();
    }
}
