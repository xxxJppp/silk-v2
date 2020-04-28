package com.spark.bitrade.job.archive;

/**
 * ArchiveJobDef
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/10/23 10:31
 */
public interface ArchiveJobDef {

    /**
     * 归档任务 Offset Key
     */
    String ARCHIVE_OFFSET_PREFIX_KEY = "data:archive:offset";

    /**
     * 归档任务 Prefix Key
     */
    String ARCHIVE_TASK_PREFIX_KEY = "data:archive:task";

    /**
     * 归档任务 锁前缀
     */
    String ARCHIVE_LOCK_PREFIX_KEY = "data:archive:lock";
}
