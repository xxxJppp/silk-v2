package com.spark.bitrade.task;

import com.spark.bitrade.service.TestService;
import com.spark.bitrade.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AbstractTestTaskImpl<Test, Object> extends AbstractTestTask<Test, Test> {

    @Autowired
    private TestService testService;

    @Override
    AbstractTestTask<Test, Test> getServiceBean() {
        log.info("0.getServiceBean");
        return SpringContextUtil.getBean(AbstractTestTaskImpl.class);
    }

    @Override
    Test converTask(Test message) {
        log.info("1.converTask");
        return null;
    }

    @Override
    boolean checkTask(Test task) {
        log.info("2.checkTask");
        return true;
    }

    @Override
    void executeBusiness(Test task) {
        log.info("3.executeBusiness");
        testService.saveTest20000();
    }

    @Override
    boolean updateTaskStatus(Test task) {
        log.info("4.updateTaskStatus");
        testService.saveTest2000X();

        return false;
    }

    @Override
    Test builderNextTask(Test task) {
        log.info("5.builderNextTask");
        //异常
        int i =1/0;
        return null;
    }

    @Override
    boolean pushMessage(Test nextTask) {
        log.info("6.pushMessage");
        return false;
    }
}
