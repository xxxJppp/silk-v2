package com.spark.bitrade.redis;

import lombok.Data;

import java.util.List;

/**
 * IncrOrDecrScriptResult
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/5 15:52
 */
@Data
public class IncrOrDecrScriptResult implements ScriptResult<Long> {

    private long state = 0;
    private long value = -1;

    IncrOrDecrScriptResult(List list) {
        if (list == null || list.size() != 2) {
            return;
        }

        Object state = list.get(0);
        Object value = list.get(1);

        if (state instanceof Long) {
            this.state = (long) state;
        }

        if (value instanceof Long) {
            this.value = (long) value;
        }
    }

    @Override
    public boolean isSuccess() {
        return state == 1;
    }

    @Override
    public Long getResult() {
        return value;
    }
}
