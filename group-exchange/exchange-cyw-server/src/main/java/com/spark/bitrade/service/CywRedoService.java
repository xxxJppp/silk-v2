package com.spark.bitrade.service;

/**
 *  异常业务重做接口
 *
 * @author young
 * @time 2019.09.30 11:09
 */
public interface CywRedoService {

    /**
     * 重做接口
     *
     * @param id
     * @return
     */
    boolean redo(long id);
}
