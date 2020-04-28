package com.spark.bitrade.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.entity.MemberFeeDayStat;
import com.spark.bitrade.mapper.MemberFeeDayStatMapper;
import com.spark.bitrade.service.MemberFeeDayStatService;

/**
 * <p>
 * 会员购买情况日统计 服务实现类
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-18
 */
@Service
public class MemberFeeDayStatServiceImpl extends ServiceImpl<MemberFeeDayStatMapper, MemberFeeDayStat> implements MemberFeeDayStatService {

	@Autowired
	private MemberFeeDayStatMapper memberFeeDailyStatMapper; 

	public boolean updateDailyStat(MemberFeeDayStat stat) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date d = stat.getStatisticDate();
		String date = sdf.format(d);
		MemberFeeDayStat currentStat = this.getMemberFeeDayStatByDay(date);
		int count = 0;
		if(null == currentStat) {
			
			count = this.memberFeeDailyStatMapper.insert(stat);
		} else {
			stat.setVersion(currentStat.getVersion());
			count = this.memberFeeDailyStatMapper.updateByStatDate(stat);
		}
		
		return count == 1;
	}

	@Override
	public MemberFeeDayStat getMemberFeeDayStatByDay(String day) {
		MemberFeeDayStat currentStat = this.memberFeeDailyStatMapper.getCurrentDayStat(day);
		return currentStat;
	}
}
