package com.spark.bitrade.redis;

import lombok.Data;

import java.util.List;

/**
 * PlaceScriptResult
 *
 * @author yangch
 * @since 2019/9/5 15:52
 */
@Data
public class PlaceScriptResult implements ScriptResult<Long> {
    private long state = 0;

    PlaceScriptResult(List list) {
        if (list == null || list.size() != 1) {
            return;
        }

        Object state = list.get(0);

        if (state instanceof Long) {
            this.state = (long) state;
        }
    }

    @Override
    public boolean isSuccess() {
        return state == 1;
    }

    @Override
    public Long getResult() {
        return null;
    }
}
