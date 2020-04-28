package com.spark.bitrade.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * DelayWalSyncJob
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/11 10:26
 */
@Slf4j
@Component
public class DelayWalSyncJobImpl extends AbstractDelayWalSyncJobImpl implements InitializingBean, DisposableBean {
    @Override
    public void destroy() throws Exception {
        super.destroy();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
    }
}
