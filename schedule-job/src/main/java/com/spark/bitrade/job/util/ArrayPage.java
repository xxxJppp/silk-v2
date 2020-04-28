package com.spark.bitrade.job.util;

import java.util.List;
import java.util.function.Consumer;

/**
 * ArrayPage
 *
 * @author Archx[archx@foxmail.com]
 * @since 2020/1/19 15:06
 */
public class ArrayPage<E> {

    private final List<E> collections;
    private final int size;

    public ArrayPage(List<E> collections, int size) {
        this.collections = collections;
        this.size = size;
    }

    public void forEach(Consumer<List<E>> accept) {
        int total = collections.size();
        int from = 0;
        while (from < total) {
            int to = from + this.size;
            if (to > total) {
                to = total;
            }
            accept.accept(collections.subList(from, to));
            from = to;
        }
    }
}
