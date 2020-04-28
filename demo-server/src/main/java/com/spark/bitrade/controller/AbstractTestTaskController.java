package com.spark.bitrade.controller;

import com.spark.bitrade.task.AbstractTestTask;
import com.spark.bitrade.util.MessageRespResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping
@RestController
public class AbstractTestTaskController {
    @Autowired
    private AbstractTestTask testTask;

    @GetMapping("/testTask")
    public MessageRespResult<String> testTask() {
        log.info("执行抽象任务 => 验证事务是否有效");
        //执行抽象任务，验证事务是否有效（验证结果：有效）
        testTask.executeTask(null);

        return MessageRespResult.success("执行成功");
    }
}
