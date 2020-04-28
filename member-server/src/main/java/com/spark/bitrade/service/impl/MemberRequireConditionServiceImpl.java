package com.spark.bitrade.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.spark.bitrade.entity.MemberRecommendCommisionSetting;
import com.spark.bitrade.entity.MemberRequireCondition;
import com.spark.bitrade.mapper.MemberRequireConditionMapper;
import com.spark.bitrade.service.MemberRequireConditionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.utils.KeyGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 会员申请条件 服务实现类
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-18
 */
@Service
@Slf4j
public class MemberRequireConditionServiceImpl extends ServiceImpl<MemberRequireConditionMapper, MemberRequireCondition> implements MemberRequireConditionService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<MemberRequireCondition> getRequireConditionBylevelId(Integer levelId) {
        String key = "";
        List<MemberRequireCondition> memberRequireConditions = null;
        QueryWrapper<MemberRequireCondition> queryWrapper = new QueryWrapper<>();
        if (levelId != null) {
            // 先从缓存中获取
            key = KeyGenerator.getRequireConditionKey(levelId);
            memberRequireConditions = (List<MemberRequireCondition>) redisTemplate.opsForValue().get(key);
            if (memberRequireConditions != null && memberRequireConditions.size() > 0) {
                return memberRequireConditions;
            } else {
                // 缓存中不存在
                queryWrapper.eq(MemberRequireCondition.LEVEL_ID, levelId);
            }
        }
        memberRequireConditions = this.baseMapper.selectList(queryWrapper);
        // 把会员申请条件放入缓存中
        Map<Integer, List<MemberRequireCondition>> groupBy = memberRequireConditions.stream().collect(Collectors.groupingBy(MemberRequireCondition::getLevelId));
        for (Integer k : groupBy.keySet()) {
            List<MemberRequireCondition> requireConditions = groupBy.get(k);
            key = KeyGenerator.getRequireConditionKey(k);
            this.redisTemplate.opsForValue().set(key, requireConditions);
        }
        return memberRequireConditions;
    }


    public boolean updateCache() {
        try {
            String key = KeyGenerator.getRequireConditionKey(999);
            Set<String> keys = redisTemplate.keys(key);
            redisTemplate.delete(keys);
            QueryWrapper<MemberRequireCondition> queryWrapper = new QueryWrapper();
            List<MemberRequireCondition> list = this.baseMapper.selectList(queryWrapper);
            // 把会员申请条件放入缓存中
            Map<Integer, List<MemberRequireCondition>> groupBy = list.stream().collect(Collectors.groupingBy(MemberRequireCondition::getLevelId));
            for (Integer k : groupBy.keySet()) {
                List<MemberRequireCondition> requireConditions = groupBy.get(k);
                key = KeyGenerator.getRequireConditionKey(k);
                this.redisTemplate.opsForValue().set(key, requireConditions);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
