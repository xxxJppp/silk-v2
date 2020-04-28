package com.spark.bitrade.service;

import java.util.List;

import com.spark.bitrade.entity.MemberRecommendCommision;

public interface CommisionService {

	public List<MemberRecommendCommision> transferCommision(List<MemberRecommendCommision> commisionList);
	
	public void distribute();
	
}
