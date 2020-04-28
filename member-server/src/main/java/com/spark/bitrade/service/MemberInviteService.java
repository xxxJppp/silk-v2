package com.spark.bitrade.service;

import java.util.List;

public interface MemberInviteService {
	
	public List<Long> getMemberInviteChainIdList(long memberId);
	
	public List<Long> createMemberInviteChain(long memberId);

}
