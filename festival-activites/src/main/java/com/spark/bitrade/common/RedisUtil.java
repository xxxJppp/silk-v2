package com.spark.bitrade.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spark.bitrade.common.customer.EventConsumer;
import com.spark.bitrade.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
    }

    /**
     * 生成令牌token
     *
     * @return
     * @author zhaopeng
     * @since 2019年12月31日
     */
    public String getMineralToken() {
        long inc = increment(ReidsKeyGenerator.synthesisToken(), 1);
        return "MINERAL-" + (inc % 10000000 + 100000000);
    }

    /**
     * 数据自增
     *
     * @param key
     * @param delta
     * @return
     * @author zhaopeng
     * @since 2019年12月31日
     */
    public long increment(String key, long delta) {
        return this.redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 集合截取
     * 用于在合成奖牌之后控制展示数据长度
     *
     * @param key
     * @param length
     * @author zhaopeng
     * @since 2019年12月31日
     */
    public void trim(String key, int length) {
        this.redisTemplate.opsForList().trim(key, 0, length);
    }

    /**
     * 锁自增
     *
     * @param key
     * @param time
     * @return
     * @author zhaopeng
     * @since 2019年12月30日
     */
    public long incrementLock(String key, long time) {
        long inc = this.redisTemplate.opsForValue().increment(key, 1);
        this.redisTemplate.expire(key, time, TimeUnit.SECONDS);
        return inc;
    }

    /**
     * 矿石种类新增 （矿石名称）
     *
     * @param mineralName
     * @author zhaopeng
     * @since 2019年12月30日
     */
    public void updateMineralType(String... mineralName) {
        this.redisTemplate.opsForSet().add(ReidsKeyGenerator.getMineralType(), mineralName);
    }

    /**
     * 获得矿石种类列表
     *
     * @return
     * @author zhaopeng
     * @since 2019年12月30日
     */
    public Set<Object> mineralTypeName() {
        return this.redisTemplate.opsForSet().members(ReidsKeyGenerator.getMineralType());
    }

    /**
     * 矿石收集完成人数 //令牌已生成
     *
     * @return
     * @author zhaopeng
     * @since 2019年12月30日
     */
    public long completeMemberCount() {
        if (keyExist(ReidsKeyGenerator.getCompleteMemberCount())) {
            return Long.parseLong(this.redisTemplate.opsForValue().get(ReidsKeyGenerator.getCompleteMemberCount()).toString());
        }
        return 0L;
    }

    /**
     * 获取任务完成总次数
     *
     * @param key
     * @param memberId
     * @return
     * @author zhaopeng
     * @since 2020年1月8日
     */
    public String memberTaskCount(String key, String memberId) {
        switch (key) {
            case EventConsumer.TASK_REGIST_STATUS:
                if (keyExist(ReidsKeyGenerator.getTaskRegistStatus(memberId))) {
                    return get(ReidsKeyGenerator.getTaskRegistStatus(memberId)).toString();
                }
                return "0";
            case EventConsumer.TASK_EXCHANGE_STATUS:
                if (keyExist(ReidsKeyGenerator.getTaskExchangeStatus(memberId))) {
                    return get(ReidsKeyGenerator.getTaskExchangeStatus(memberId)).toString();
                }
                return "0";
            case EventConsumer.TASK_LOGIN_STATUS:
                if (keyExist(ReidsKeyGenerator.getTaskLoginStatus(memberId))) {
                    return get(ReidsKeyGenerator.getTaskLoginStatus(memberId)).toString();
                }
                return "0";
            case EventConsumer.TASK_OTC_STATUS:
                if (keyExist(ReidsKeyGenerator.getTaskOtcStatus(memberId))) {
                    return get(ReidsKeyGenerator.getTaskOtcStatus(memberId)).toString();
                }
                return "0";
            case EventConsumer.TASK_PUT_STATUS:
                if (keyExist(ReidsKeyGenerator.getTaskPutStatus(memberId))) {
                    return get(ReidsKeyGenerator.getTaskPutStatus(memberId)).toString();
                }
                return "0";
            case EventConsumer.TASK_RECHARGE_STATUS:
                if (keyExist(ReidsKeyGenerator.getTaskRechargeStatus(memberId))) {
                    return get(ReidsKeyGenerator.getTaskRechargeStatus(memberId)).toString();
                }
                return "0";
        }
        return "0";
    }

    /**
     * 令牌合成后续缓存处理
     *
     * @param member
     * @author zhaopeng
     * @since 2019年12月31日
     */
    public void synthesis(Member member) {
        increment(ReidsKeyGenerator.getCompleteMemberCount(), 1);
        setVal(ReidsKeyGenerator.getSynthesisMemberIdSetKey(), member.getId());
        String msg = "";
        UserNameUtil userNameUtil = new UserNameUtil();
        if (StringUtils.hasText(member.getUsername())) {
            //有昵称
            if(member.getUsername().equals(member.getMobilePhone())){
                msg = userNameUtil.getPassPhone(member.getUsername());
            }else if(member.getUsername().equals(member.getEmail())){
                msg = userNameUtil.getPathMail(member.getUsername());
            }else{
                msg = member.getUsername();
            }
        } else if (StringUtils.hasText(member.getMobilePhone())) {
            //有电话
            msg = userNameUtil.getPassPhone(member.getMobilePhone());
        } else if (StringUtils.hasText(member.getEmail())) {
            //有邮箱
            msg = userNameUtil.getPathMail(member.getEmail());
        }
        lSet(ReidsKeyGenerator.getSynthesisMemberTop10(), msg + "_" + System.currentTimeMillis());
        trim(ReidsKeyGenerator.getSynthesisMemberTop10(), 9);//仅保留最新10条
        delKey(ReidsKeyGenerator.getMineralLock(member.getId().toString())); //除去消费锁
    }


    /**
     * 获取set
     *
     * @param key
     * @return
     * @author zhaopeng
     * @since 2019年12月31日
     */
    public Set<Object> getSet(String key) {
        return this.redisTemplate.opsForSet().members(key);
    }

    /**
     * 设置set 集合值
     *
     * @param key
     * @param val
     * @return
     * @author zhaopeng
     * @since 2019年12月31日
     */
    public void setVal(String key, Object val) {
        this.redisTemplate.opsForSet().add(key, val);
    }

    /**
     * Set中包含值
     *
     * @param key
     * @param val
     * @return
     * @author zhaopeng
     * @since 2019年12月31日
     */
    public boolean setHasVal(String key, Object val) {
        return this.redisTemplate.opsForSet().isMember(key, val);
    }

    /**
     * 键存在
     *
     * @param keys
     * @return
     * @author zhaopeng
     * @since 2019年12月30日
     */
    public boolean keyExist(String... keys) {
        for (String key : keys) {
            if (!this.redisTemplate.hasKey(key)) return false;
        }
        return true;
    }

    /**
     * set长度
     *
     * @param key
     * @return
     * @author zhaopeng
     * @since 2019年12月31日
     */
    public long getKeyLength(String key) {
        if (keyExist(key)) {
            return this.redisTemplate.opsForSet().size(key);
        }
        return 0l;
    }

    /**
     * 删除键
     *
     * @param key
     * @author zhaopeng
     * @since 2019年12月31日
     */
    public void delKey(String key) {
        this.redisTemplate.delete(key);
    }

    /**
     * 原子递增 key 的 value 必须为数字类型
     *
     * @param key
     * @param delta 递增因子
     * @return
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }


    /**
     * 原子递减 key 的 value 必须为数字类型
     *
     * @param key
     * @param delta 要减少几(小于0)
     * @return
     */
    public long decr(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key, long count, Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 普通缓存获取
     *
     * @param key key
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除 key
     *
     * @param key 可以一个，或者多个
     */
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean rSet(String key, Object value) {
        redisTemplate.opsForList().rightPush(key, value);
        return true;
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, Object value) {
        redisTemplate.opsForList().leftPush(key, value);
        return true;
    }

    public Object rGet(String key) {
    	return redisTemplate.opsForList().rightPop(key);
    }
    
    /**
     * 普通缓存放入
     *
     * @param key
     * @param value
     * @return true成功 false失败
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 带时间存入
     * @param key
     * @param value
     * @param time
     * @return
     * @author zhaopeng
     * @since 2020年2月3日
     */
    public boolean set(String key , Object value , long time) {
    	 try {
             redisTemplate.opsForValue().set(key, value);
        	 this.redisTemplate.expire(key, time, TimeUnit.SECONDS);
             return true;
         } catch (Exception e) {
             e.printStackTrace();
             return false;
         }
    }
    
    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * set长度
     * @param key
     * @return
     * @author zhaopeng
     * @since 2020年1月15日
     */
    public long setLength(String key) {
    	if(keyExist(key)) {
    		return this.redisTemplate.opsForSet().size(key);
    	}
    	return 0l;
    }
}
