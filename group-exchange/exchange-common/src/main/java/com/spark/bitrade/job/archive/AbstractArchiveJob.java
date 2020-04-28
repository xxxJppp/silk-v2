package com.spark.bitrade.job.archive;

import com.spark.bitrade.job.ArchiveJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * 归档任务抽象
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/25 14:19
 */
@Slf4j
public abstract class AbstractArchiveJob<T> implements ArchiveJob<T>, InitializingBean, DisposableBean {

    protected final StringRedisTemplate redisTemplate;
    private boolean runnable = true;


    public AbstractArchiveJob(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 获取任务列表key
     *
     * @return string
     */
    protected abstract String getTaskListKey();

    /**
     * 具体执行方法
     */
    protected abstract void handle(T value);

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }

    /**
     * 统计进行中的任务数量
     *
     * @return count
     */
    protected long countTaskSize() {
        final String key = getTaskListKey();
        SetOperations<String, String> operations = redisTemplate.opsForSet();

        Long size = operations.size(key);
        if (size == null) {
            return 0;
        }
        return size;
    }

    /**
     * 获取全局锁
     *
     * @param key     key
     * @param timeout 超时时间，单位：秒
     * @return bool
     */
    protected boolean getGlobalLock(final String key, final long timeout) {


        // 当前版本的 RedisTemplate 未实现 set KEY "1" NX EX 60
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        // operations.setIfAbsent("key","v","NX", "EX",SECONDS); !!! not implementation

        Long incr = operations.increment(key, 1L);

        if (incr != null && incr == 1L) {
            redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
            return true;
        } else {
            // 程序异常结束后可能造成死锁，再次加锁是判定是否死锁
            Long expire = redisTemplate.getExpire(key);
            // 死锁
            if (expire != null && expire == -1) {
                redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
            }
        }
        return false;
    }

    @Override
    public void start() {
        this.runnable = true;

        Thread thread = new Thread(() -> {
            long millis = 500;
            long times = 0;
            while (runnable) {
                try {
                    T fetch = fetch();

                    if (fetch == null) {
                        // 阻塞并逐渐增加阻塞时间
                        if (times < 60) {
                            times++;
                        }
                        LockSupport.parkNanos(millis * times * 1000000);
                        log.info("获取任务 >>> 轮空次数={}", times);
                        continue;
                    }

                    times = 0;
                    handle(fetch);
                } catch (Throwable t) {
                    log.error("任务执行异常", t);
                }
            }
        });
        thread.setName(getName());
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void stop() {
        this.runnable = false;
    }

    @Override
    public void destroy() throws Exception {
        this.stop();
    }
}
