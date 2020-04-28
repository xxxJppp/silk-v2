package com.spark.bitrade.biz;

import com.spark.bitrade.common.RedisUtil;
import com.spark.bitrade.common.ReidsKeyGenerator;
import com.spark.bitrade.entity.NewYearConfig;
import com.spark.bitrade.entity.NewYearMemberInfo;
import com.spark.bitrade.service.NewYearConfigService;
import com.spark.bitrade.service.NewYearMemberInfoService;
import com.spark.bitrade.vo.MiningIndexVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 年终集矿石活动配置表 服务类
 * </p>
 *
 * @author qiliao
 * @since 2019-12-30
 */
@Service
public class NewYearConfigBizService {

    @Autowired
    private NewYearConfigService configService;

    @Autowired
    private NewYearMemberInfoService memberInfoService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 查询活动
     * @return
     */
    public MiningIndexVo findActivity(Long memberId) {
        // 初始化每日任务表
        //dailyTaskBizService.generatorMemberDailyTask(memberId);
        NewYearConfig activity = configService.findNewYearConfig().get(0);
        if (activity == null) {
            throw new RuntimeException("活动不存在...");
        }
        NewYearMemberInfo record = memberInfoService.findRecordByMemberId(memberId);
//        if (record == null) {
//            record = new NewYearMemberInfo();
//            record.setMemberId(memberId);
//            record.setDigTimes(0);
//            memberInfoService.save(record);
//        }
        if(record == null) {
            if(this.redisUtil.incrementLock(ReidsKeyGenerator.taskMemberInfoCreateLock(memberId.toString()), 3) <=1) { //初始化信息锁
                record = new NewYearMemberInfo();
                record.setMemberId(memberId);
                record.setCreateTime(new Date());
                this.memberInfoService.save(record);
            }
            else {
                while(this.redisUtil.keyExist(ReidsKeyGenerator.taskMemberInfoCreateLock(memberId.toString()))) {}
            }
        }
        MiningIndexVo vo = new MiningIndexVo();
        vo.setActivityName(activity.getName());
        vo.setDigTimes(record.getDigTimes());
        vo.setEndTime(activity.getMineralEndTime());
        vo.setActivityRule(activity.getActRules());
        return vo;
    }

}
