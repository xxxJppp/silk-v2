package com.spark.bitrade.trans;

/**
 * Tuple2
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/7/17 19:01
 */
public class Tuple2<T1, T2> {

    private final T1 first;
    private final T2 second;

    public Tuple2(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    public T1 getFirst() {
        return first;
    }

    public T2 getSecond() {
        return second;
    }
}
