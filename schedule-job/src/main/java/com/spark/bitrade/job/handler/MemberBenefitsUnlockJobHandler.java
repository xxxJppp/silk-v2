package com.spark.bitrade.job.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.spark.bitrade.service.IMemberBenefitsService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;

import lombok.extern.slf4j.Slf4j;


/**
 * 
 * @author xianming
 *
 */
@JobHandler(value = "memberBenefitsUnlockJobHandler")
@Component
@Slf4j
public class MemberBenefitsUnlockJobHandler extends IJobHandler {

	@Autowired
	private IMemberBenefitsService mMemberBenefitsService;
    @Override
    public ReturnT<String> execute(String param) throws Exception {
       
    	try {
    		log.info("start to unlock member benefits...");
			this.mMemberBenefitsService.memberBenefitsUnlock();
			log.info("end to unlock member benefits...");
			 return ReturnT.SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			 return ReturnT.FAIL;
		}
       
    }
}
