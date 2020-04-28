package com.spark.bitrade.job;

import java.time.Instant;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * SyncItem
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/11 10:32
 */
public class SyncItem implements Delayed {

    private final String value;
    private final long time;

    public SyncItem(String value, long time) {
        this.value = value;
        this.time = time;
    }

    public String getValue() {
        return value;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        //return unit.convert(this.time - System.nanoTime(), TimeUnit.SECONDS);
        return unit.convert(time - Instant.now().getEpochSecond(), TimeUnit.SECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        if (this.getDelay(TimeUnit.SECONDS) > o.getDelay(TimeUnit.SECONDS)) {
            return 1;
        } else if (this.getDelay(TimeUnit.SECONDS) < o.getDelay(TimeUnit.SECONDS)) {
            return -1;
        }
        return 0;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
