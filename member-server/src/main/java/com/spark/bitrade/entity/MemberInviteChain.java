package com.spark.bitrade.entity;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class MemberInviteChain implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1582218933089310860L;
	private Long memberId;
	private List<Long> inviteMemberIdList;
	
}
