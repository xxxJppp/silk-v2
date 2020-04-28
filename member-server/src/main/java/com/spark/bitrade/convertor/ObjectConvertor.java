package com.spark.bitrade.convertor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.spark.bitrade.entity.Member;
import com.spark.bitrade.vo.AccountRunning;
import com.spark.bitrade.vo.CanalAccountRunning;
import com.spark.bitrade.vo.CanalMember;

public class ObjectConvertor {

	public static AccountRunning convert2AccountRunning(CanalAccountRunning car) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		AccountRunning ar = new AccountRunning();
		ar.setId(car.getId());
		ar.setCoinUnit(car.getCoin_unit());
		ar.setMemberId(car.getMember_id());
		ar.setUpdateTime(new Timestamp(sdf.parse(car.getUpdate_time()).getTime()));
		ar.setCreateTime(new Timestamp(sdf.parse(car.getCreate_time()).getTime()));

		ar.setFee(null != car.getFee() ? new BigDecimal(car.getFee()) : new BigDecimal(0));
		ar.setFeeDiscount(null != car.getFee_discount() ? new BigDecimal(car.getFee_discount()) : new BigDecimal(0));
		ar.setTradeBalance(null != car.getTrade_balance() ? new BigDecimal(car.getTrade_balance()) : new BigDecimal(0));
		ar.setTradeFrozen(null != car.getTrade_frozen() ? new BigDecimal(car.getTrade_frozen()) : new BigDecimal(0));
		ar.setRate(null != car.getRate() ? new BigDecimal(car.getRate()) : new BigDecimal(0));
		ar.setRefId(car.getRef_id());
		ar.setTradeType(car.getTrade_type());
//		ar.setBizType(car.getBiz_type());
		ar.setOrderMatchType(car.getOrder_match_type() != null ? car.getOrder_match_type():0);
		return ar;
	}
	
	public static Member convert2Member(CanalMember cm) {
		Member m = new Member();
		m.setId(cm.getId());
		m.setInviterId(cm.getInviter_id());
		return m;
	}

}
