package com.spark.bitrade.biz.Impl;

import com.alibaba.fastjson.JSON;
import com.spark.bitrade.biz.IRequireConditionService;
import com.spark.bitrade.constant.BizTypeEnum;
import com.spark.bitrade.constant.FlagDiscountEnum;
import com.spark.bitrade.constant.MemberLevelTypeEnum;
import com.spark.bitrade.constant.PayTypeEnum;
import com.spark.bitrade.entity.MemberRequireCondition;
import com.spark.bitrade.service.MemberRequireConditionService;
import com.spark.bitrade.utils.KeyGenerator;
import com.spark.bitrade.utils.MemberUtil;
import com.spark.bitrade.vo.RequireConditionVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.20 09:53
 */
@Service
@Slf4j
public class RequireConditionServiceImpl implements IRequireConditionService {

    @Autowired
    private MemberRequireConditionService requireConditionService;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<RequireConditionVo> getRequireConditionVoList() {
        List<RequireConditionVo> resultVo = new ArrayList<>();
        String key = KeyGenerator.getRequireConditionKey(999);
        Set<String> keys = redisTemplate.keys(key);
        List<MemberRequireCondition> conditions = new ArrayList<>();
        if (keys.isEmpty() ) {
            conditions = requireConditionService.getRequireConditionBylevelId(null);
        } else {
            List<List<MemberRequireCondition>> conditionsa = redisTemplate.opsForValue().multiGet(keys);
            for (List<MemberRequireCondition> list : conditionsa) {
                conditions.addAll(list);
            }
        }
        Map<Integer, List<MemberRequireCondition>> groupBy = conditions.stream().collect(Collectors.groupingBy(MemberRequireCondition::getLevelId));
        for (Integer k : groupBy.keySet()) {
            List<MemberRequireCondition> requireConditions = groupBy.get(k);
            RequireConditionVo requireConditionVo = new RequireConditionVo();
            requireConditionVo.setLevelName(MemberUtil.getValueByCode(k));
            requireConditionVo.setLevelId(k);
            for (MemberRequireCondition requireCondition : requireConditions) {
                if (requireCondition.getDuration() != null) requireConditionVo.setDuration(requireCondition.getDuration());
                if (requireCondition.getType() == PayTypeEnum.BUY.getCode()) {
                    requireConditionVo.setBuyAmount(requireCondition.getQuantity());
                    requireConditionVo.setUnit(requireCondition.getUnit());
                    requireConditionVo.setBuyDiscountFlag(requireCondition.getFlagDiscount());
                    if (requireCondition.getFlagDiscount() == FlagDiscountEnum.OPENING.getCode()) {
                        requireConditionVo.setBuyDiscountFlag(requireCondition.getFlagDiscount());
                        requireConditionVo.setDiscountbuyAmount(requireCondition.getQuantity().multiply((BigDecimal.ONE.subtract(requireCondition.getDiscount()))));
                    }
                } else if (requireCondition.getType() == PayTypeEnum.LOCK.getCode()) {
                    requireConditionVo.setLockAmount(requireCondition.getQuantity());
                    requireConditionVo.setUnit(requireCondition.getUnit());
                    requireConditionVo.setLockDiscountFlag(requireCondition.getFlagDiscount());
                    if (requireCondition.getFlagDiscount() == FlagDiscountEnum.OPENING.getCode()) {
                        requireConditionVo.setLockDiscountFlag(requireCondition.getFlagDiscount());
                        requireConditionVo.setDiscountLockAmount(requireCondition.getQuantity().multiply((BigDecimal.ONE.subtract(requireCondition.getDiscount()))));
                    }
                } else {
                    requireConditionVo.setCommunityNumber(requireCondition.getQuantity());
                }
            }
            resultVo.add(requireConditionVo);
        }
        return resultVo;
    }


    @Override
    public Boolean updateRequireConditionCache() {
        return requireConditionService.updateCache();
    }

}
