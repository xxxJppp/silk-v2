package com.spark.bitrade.common;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 
 * <p>
 * 幸运宝redis工具
 * </p>
 *
 * @author Administrator
 * @since 2019年12月16日
 */
@Component
public class LuckyGameRedisUtil {

	private RedisTemplate<String, String> redisTemplate;
	
	 @Autowired
     public void setRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        this.redisTemplate = new StringRedisTemplate(redisConnectionFactory);
    }
	/**
	 * 自增因子
	 */
	public final static long DELTA_ADD = 1;
	/**
	 * 自减因子
	 */
	public final static long DELTA_SUB = -1;
	/**
	 * 全部活动   %s 用户id
	 * key_userid ----> [gameid1,gameid2]
	 */
	public final static String ACTIVITY_RECORD_ALL_QUERY = "lucky:activity:allgame:%s"; //用set保存全部活动id ， 方便在新加入购买操作的时候过滤已加入过的活动
	/**
	 * 我的活动   %s 用户id
	 * key_userid ----> [gameid1_1,gameid1_2]
	 */
	public final static String ACTIVITY_RECORD_MY_QUERY = "lucky:activity:mygame:%s"; //用set保存我的活动变更id,_1代表状态,_2代表分享
	/**
	 * 活动加入人   %s 活动id
	 * key_gameid ----> [userid1,userid2]
	 */
	public final static String ACTIVITY_JOIN_MEMBER_COUNT = "lucky:activity:join:member:%s"; //用set保存全部加入人id，过滤重复购买
	/**
	 * 活动已购票数   %s 活动id
	 * key_gameid ----> 已购票数
	 */
	public final static String ACTIVITY_JOIN_TICKET_COUNT = "lucky:activity:join:ticket:%s";
	/**
	 * 活动对应票号生成策略主键
	 */
	public final static String ACTIVITY_TICKET_ID = "lucky:activity:ticket:id";
	/**
	 * 指定活动已加入票号 %s活动id
	 */
	public final static String ACTIVITY_GAME_JOIN_TICKET = "lucky:activity:ticket:join:%s";//set集合 保存已加入当前活动的票号
	/**
	 * 指定活动分享锁 % 活动id_参与id
	 */
	public final static String ACTIVITY_GAME_APPEND_WX_LOCK = "lucky:activity:appendwx:lock:%s";
	/**
	 * 指定活动定时任务锁 % 活动id_参与id
	 */
	public final static String ACTIVITY_GAME_STATE_CHANGE_LOCK = "lucky:activity:state:change:lock:%s";
	/**
	 * 指定活动当前状态 %s 活动id
	 * 活动状态仅记录未结算的活动，结算完成后，移除活动状态缓存
	 */
	public final static String ACTIVITY_GAME_STATE = "lucky:activity:state:%s";
	/**
	 * 键自增/减
	 * @param key
	 * @param delta
	 * @return
	 */
	public long increment(String key , long delta) {
		return redisTemplate.opsForValue().increment(key, delta);
	}
	
	/**
	 * 键自增/减 时间限制 单位(s)
	 * @param key
	 * @param delta
	 * @return
	 */
	public long increment(String key , long delta , long time) {
		long inc = redisTemplate.opsForValue().increment(key, delta);
		redisTemplate.expire(key, time, TimeUnit.SECONDS);
		return inc;
	}
	
	/**
	 * 加入活动更新活动对应缓存信息 , 每次调用增加一次
	 * 调用后返回当前加入对应的票号，
	 * 票号规则
	 * 1.不同活动可能会重复
	 * 2.最大票数99W9999
	 * 3.同一个活动票号不一定连续
	 * @param gameId
	 * @param memberId
	 * @return 加入活动产生的票号（每一个活动独立拥有的数字型编码,不同活动可能重复）
	 * @author zhaopeng
	 * @since 2019年12月16日
	 */
	public String joinGame(String gameId ,String memberIdNumber) {
		setSetKey(String.format(ACTIVITY_RECORD_ALL_QUERY, memberIdNumber), gameId); //添加加入活动id记录
		setSetKey(String.format(ACTIVITY_JOIN_MEMBER_COUNT, gameId), memberIdNumber); //加入活动参与人
		increment(String.format(ACTIVITY_JOIN_TICKET_COUNT, gameId), DELTA_ADD);//活动票数自增
		long incKey = increment(ACTIVITY_TICKET_ID, DELTA_ADD); //获得票号
		String tickNum = (incKey % 100000 + 100000) +"" ; //票号规则
		setSetKey(String.format(ACTIVITY_GAME_JOIN_TICKET, gameId), tickNum); //活动票池加入票号
		return tickNum;
	}
	
	/**
	 * 缓存异常时还原加入信息
	 * @param gameId
	 * @param member
	 * @param tickNum
	 * @author zhaopeng
	 * @since 2019年12月19日
	 */
	public void joinExecption(String gameId , String member , String tickNum) {
		delSetVal(String.format(ACTIVITY_RECORD_ALL_QUERY, member), gameId);
		delSetVal(String.format(ACTIVITY_JOIN_MEMBER_COUNT, gameId), member);
		increment(String.format(ACTIVITY_JOIN_TICKET_COUNT, gameId), DELTA_SUB);
		delSetVal(String.format(ACTIVITY_GAME_JOIN_TICKET, gameId), tickNum);
	}
	
	/**
	 * 获得欢乐幸运号中奖号码
	 * @param gameId
	 * @param winNumber 中奖票数
	 * @return
	 * @author zhaopeng
	 * @since 2019年12月17日
	 */
	public Set<String> getLuckyNumberWinTicket(String gameId , int winNumber) {
		List<String> tickets = new ArrayList<String>(getSetKey(String.format(ACTIVITY_GAME_JOIN_TICKET, gameId)));
		Set<String> winTicket = new HashSet<String>();
		while(winTicket.size() < winNumber ) {
			int index = (int) (Math.random() * (tickets.size()));
			winTicket.add(tickets.get(index));
		}
		return winTicket;
	}
	
	/**
	 * 活动结束更新对应缓存信息
	 * @param gameId
	 * @author zhaopeng
	 * @since 2019年12月16日
	 */
	public void gameOver(String gameId) {
		Set<String> members = getSetKey(String.format(ACTIVITY_JOIN_MEMBER_COUNT, gameId)); //当前活动已加入memberId
		/*members.stream().forEach(member ->{ //移除参与人当前参与活动
			delSetVal(String.format(ACTIVITY_RECORD_ALL_QUERY, member), gameId);
		});*/
		delKey(String.format(ACTIVITY_JOIN_MEMBER_COUNT, gameId));//移除活动加入人列表
		delKey(String.format(ACTIVITY_JOIN_TICKET_COUNT, gameId)); //移除活动票总数
		delKey(String.format(ACTIVITY_GAME_JOIN_TICKET, gameId)); //移除活动票池数据
		delKey(String.format(ACTIVITY_GAME_STATE, gameId)); //移除活动状态
	}
	
	/**
	 * 活动状态发生变化更新对应缓存
	 * @param gameId
	 * @param state
	 * @author zhaopeng
	 * @since 2019年12月17日
	 */
	public void gameStateChange(String gameId , int state) {
		setKey(String.format(ACTIVITY_GAME_STATE, gameId), state + "");
		Set<String> members = getSetKey(String.format(ACTIVITY_JOIN_MEMBER_COUNT, gameId)); //当前活动需要通知的member id 集合
		if(CollectionUtils.isNotEmpty(members)) {
			members.stream().forEach(member->{
				setSetKey(String.format(ACTIVITY_RECORD_MY_QUERY, member), gameId + "_1"); //添加指定活动状态变更记录
			//	increment(String.format(ACTIVITY_RECORD_MY_QUERY, member), DELTA_ADD); //为member增加一个提醒
			});
		}
	}
	
	/**
	 * 指定活动添加待分享记录
	 * @param gameId
	 * @param member
	 * @author zhaopeng
	 * @since 2019年12月24日
	 */
	public void gameAppendWx(String gameId , String member) {
		setSetKey(String.format(ACTIVITY_RECORD_MY_QUERY, member), gameId + "_2");//添加指定活动未分享记录
	}
	
	/**
	 * 点击活动详情，尝试清除状态变化记录
	 * @param member
	 * @param gameId
	 * @author zhaopeng
	 * @since 2019年12月24日
	 */
	public void readChange(String member , String gameId) {
		if(redisSetHasVal(String.format(ACTIVITY_RECORD_MY_QUERY, member), gameId + "_1")) { //如果有活动状态变更
			delSetVal(String.format(ACTIVITY_RECORD_MY_QUERY, member), gameId + "_1");
		}
	}
	
	/**
	 * 分享后，尝试清除分享状态记录
	 * @param member
	 * @param gameId
	 * @author zhaopeng
	 * @since 2019年12月24日
	 */
	public void hasAppendWx(String member , String gameId) {
		if(redisSetHasVal(String.format(ACTIVITY_RECORD_MY_QUERY, member), gameId + "_2")) { //如果有活动状态变更
			delSetVal(String.format(ACTIVITY_RECORD_MY_QUERY, member), gameId + "_2");
		}
	}
	
	/**
	 * 获得指定活动参与人数，票池总数
	 * memberCount : xx , ticketCount:xx+
	 * @param gameId
	 * @return
	 * @author zhaopeng
	 * @since 2019年12月16日
	 */
	public Map<String, Integer> getJoinCount(String gameId) {
		Map<String, Integer> count = new HashMap<String, Integer>();
		if(redisKeyExist(String.format(ACTIVITY_JOIN_MEMBER_COUNT, gameId))) {
			count.put("member", redisTemplate.opsForSet().members(String.format(ACTIVITY_JOIN_MEMBER_COUNT, gameId)).size());
			count.put("ticket", Integer.parseInt(getKey(String.format(ACTIVITY_JOIN_TICKET_COUNT, gameId))));
			return count;
		}else {
			count.put("member", 0);
			count.put("ticket", 0);
			return count;
		}
	}
	/**
	 * 获得访客活动红点、我的活动红点
	 * @param memberIdNumber
	 * @return
	 * @author zhaopeng
	 * @since 2019年12月16日
	 */
	public Map<String, Object> getActivityRecord(String memberIdNumber) {
		Map<String, Object> record = new HashMap<String, Object>();
		if(this.redisKeyExist(String.format(ACTIVITY_RECORD_ALL_QUERY, memberIdNumber))) {
			record.put("all", getSetKey(String.format(ACTIVITY_RECORD_ALL_QUERY, memberIdNumber)).size() + "");
			if(this.redisKeyExist( String.format(ACTIVITY_RECORD_MY_QUERY, memberIdNumber))) {
				record.put("my", getSetKeySize(String.format(ACTIVITY_RECORD_MY_QUERY, memberIdNumber))); //我的活动默认为0，当产生活动状态变化与未分享记录变化时自增/减
			}
			else {
				record.put("my", "0");
			}
			return record;
		}
		record.put("all", "0");
		record.put("my", "0");
		return record;
	}
	
	/**
	 * set中是否存在值
	 * @param key
	 * @param val
	 * @return
	 * @author zhaopeng
	 * @since 2019年12月16日
	 */
	public boolean redisSetHasVal(String key , String val) {
		return redisTemplate.opsForSet().isMember(key, val);
	}
	
	/**
	 * 键值存在判断
	 * @param keys
	 * @return
	 * @author zhaopeng
	 * @since 2019年12月16日
	 */
	public boolean redisKeyExist(String... keys) {
		if(keys != null && keys.length > 0) {
			for(String key : keys) {
				if(!redisTemplate.hasKey(key)) return false;
			}
			return true;
		}
		return false;
	}
	
	/**
	 * 获得集合长度
	 * @param key
	 * @return
	 * @author zhaopeng
	 * @since 2019年12月24日
	 */
	public long getSetKeySize(String key) {
		return redisTemplate.opsForSet().size(key);
	}
	
	public void setSetKey(String key , String val) {
		redisTemplate.opsForSet().add(key, val);
	}
	
	public void setKey(String key , String val) {
		redisTemplate.opsForValue().set(key, val);
	}
	
	public Set<String> getSetKey(String key) {
		return redisTemplate.opsForSet().members(key);
	}
	
	public String getKey(String key) {
		return redisTemplate.opsForValue().get(key);
	}
	
	public void delSetVal(String key , Object val) {
		redisTemplate.opsForSet().remove(key, val);
	}
	
	public void delKey(String key) {
		redisTemplate.delete(key);
	}
}
