package com.spark.bitrade.biz;

import com.spark.bitrade.common.NewYearExceptionMsg;
import com.spark.bitrade.common.RedisUtil;
import com.spark.bitrade.common.ReidsKeyGenerator;
import com.spark.bitrade.entity.NewYearConfig;
import com.spark.bitrade.entity.NewYearMemberInfo;
import com.spark.bitrade.entity.NewYearMineral;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.service.NewYearConfigService;
import com.spark.bitrade.service.NewYearMemberInfoService;
import com.spark.bitrade.service.NewYearMineralService;
import com.spark.bitrade.util.GeneratorUtil;
import com.spark.bitrade.vo.MiningMineralVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 立即挖矿
 *
 * @author: Zhong Jiang
 * @date: 2019-12-30 18:29
 */
@Slf4j
@Service
public class MiningActivityBizService {

    @Autowired
    private NewYearMineralService mineralService;

    @Autowired
    private NewYearConfigService configService;

    @Autowired
    private MemberMineralBizService mineralBizService;

    @Autowired
    private NewYearMemberInfoService memberInfoService;

    @Autowired
    private RedisUtil redisUtil;

    @PostConstruct
    public void init() {
        this.initMineralSurplusNum();
    }


    public synchronized MiningMineralVo doMiningActivity(Long memberId) {
        MiningMineralVo mineralVo = null;
        this.validActivity(memberId);
        this.initMineralSurplusNum();
        Long size = redisUtil.lGetListSize(ReidsKeyGenerator.getMineralNumberNotZeroKey());
        if (size <= 0) {
            log.info("\n\n\n 矿石已经挖完了 ======= \n\n\n "  );
            mineralVo = new MiningMineralVo();
            // 矿石已经被挖完
            mineralVo.setMineraIsEmpty(99);
            return mineralVo;
        }
        int index = GeneratorUtil.getRandomNumber(0, Math.toIntExact(size) - 1);
        log.info("\n\n\n 随机数为：" + index);
        int type = (int) redisUtil.lGetIndex(ReidsKeyGenerator.getMineralNumberNotZeroKey(), index);
        log.info("======== 挖到的矿石类型 {} =========", type);
        mineralVo = this.deductMinera(memberId, type);
        return mineralVo;
    }

    /**
     * 挖到矿石减去缓存中相应的矿石
     * @param type
     */
    private MiningMineralVo deductMinera(Long memberId, int type) {
        MiningMineralVo miningMineralVo = null;
//        long surplus = redisUtil.decr(ReidsKeyGenerator.getMineralKey(type), -1);
        redisUtil.lRemove(ReidsKeyGenerator.getMineralNumberNotZeroKey(), 1, type);
//        if (surplus < 0) {
//            redisUtil.incr(ReidsKeyGenerator.getMineralKey(type), 1);
//            this.doMiningActivity(memberId);
//        }
        try {
            miningMineralVo = mineralBizService.doMiningAfter(memberId, type);
        } catch (Exception e) {
//            redisUtil.incr(ReidsKeyGenerator.getMineralKey(type), 1);
            redisUtil.rSet(ReidsKeyGenerator.getMineralNumberNotZeroKey(), type);
            throw new MessageCodeException(NewYearExceptionMsg.MINING_FAIL);
        }
        return miningMineralVo;
    }


    /**
     * 校验活动
     */
    private void validActivity(Long memberId) {
        NewYearConfig activity = configService.findNewYearConfig().get(0);
        if (activity == null) {
            throw new MessageCodeException(NewYearExceptionMsg.ACTIVITY_IS_NULL);
        }
        if (new Date().compareTo(activity.getMineralStartTime()) < 0) {
            throw new MessageCodeException(NewYearExceptionMsg.ACTIVITY_IS_NOT_START);
        }
        if (new Date().compareTo(activity.getMineralEndTime() ) >= 0) {
            throw new MessageCodeException(NewYearExceptionMsg.ACTIVITY_IS_END);
        }
        // 校验用户剩余挖矿次数
        NewYearMemberInfo record = memberInfoService.findRecordByMemberId(memberId);
        if (record == null) {
            throw new MessageCodeException(NewYearExceptionMsg.MINING_FAIL);
        }
        if (record != null && record.getDigTimes() <=0) {
            throw new MessageCodeException(NewYearExceptionMsg.MINING_NUMBER_IS_ZERO);
        }
    }

    // 随机方式
    /*private void initMineralSurplusNum() {
        log.info("=========== init Mineral number ============");
        String findKey = ReidsKeyGenerator.getMineralKey(999);
        Object obj = redisUtil.get(findKey);
        if (redisUtil.lGet(ReidsKeyGenerator.getMineralNumberNotZeroKey(), 0, 1) != null) {
            redisUtil.del(ReidsKeyGenerator.getMineralNumberNotZeroKey());
        }
        if (obj == null) {
            List<NewYearMineral> list = mineralService.findMineralList();
            list.forEach( item -> {
                Integer surplus = item.getTotal() - item.getCost();
                if (surplus != 0) {
                    redisUtil.rSet(ReidsKeyGenerator.getMineralNumberNotZeroKey(), item.getMineralType());
                    redisUtil.set(ReidsKeyGenerator.getMineralKey(item.getMineralType()), surplus);
                }
            });
        }
    }*/

    // 权重方式
    private void initMineralSurplusNum() {
        log.info("=========== init Mineral number ============");
        String findKey = ReidsKeyGenerator.getMineralNumberNotZeroKey();
        List<Object> list1 = redisUtil.lGet(findKey, 0, 1);
        if (list1 == null || list1.size() <= 0) {
            List<NewYearMineral> list = mineralService.findMineralList();
            list.forEach( item -> {
                Integer surplus = item.getTotal() - item.getCost();
                if (surplus != 0) {
                    for (int i = 0; i < surplus; i++) {
                        redisUtil.rSet(ReidsKeyGenerator.getMineralNumberNotZeroKey(), item.getMineralType());
                    }
                }
            });
        }
    }
}
