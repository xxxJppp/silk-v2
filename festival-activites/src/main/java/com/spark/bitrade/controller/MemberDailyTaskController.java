package com.spark.bitrade.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spark.bitrade.biz.MemberDailyTaskBizService;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.vo.MemberTaskVo;
import com.spark.bitrade.web.bind.annotation.MemberAccount;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * <p>
 * 	个人挖矿次数任务
 * </p>
 *
 * @author zhaopeng
 * @since 2020年1月7日
 */
@RestController
@RequestMapping("api/v2/task")
@Api(tags = "每日任务-前端接口")
public class MemberDailyTaskController extends ApiController{

    @Autowired
    private MemberDailyTaskBizService dailyTaskBizService;

    @ApiOperation(value = "获取每日任务列表接口", notes = "获取每日任务列表接口",tags = "每日任务-前端接口")
    @PostMapping("/find")
    public MessageRespResult<List<MemberTaskVo>> findMemberDailyTaskList(@MemberAccount Member member) {
        return MessageRespResult.success("", this.dailyTaskBizService.findMemberDailyTask(member.getId()));
    }
}
